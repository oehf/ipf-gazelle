/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.gazelle.validation.core;


import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.conf.store.ProfileStore;
import ca.uhn.hl7v2.conf.store.URLProfileStore;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationException;
import ca.uhn.hl7v2.validation.impl.AbstractMessageRule;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;
import org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.guessGazelleProfile;

/**
 * A modified conformance profile rule that wraps and caches {@link GazelleProfileRule} instances. Note that the HapiContext associated
 * with the message to be validated must return a profile store that provides the Gazelle conformance profiles, usually an instance
 * of {@link org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore}, from {@link ca.uhn.hl7v2.HapiContext#getProfileStore()}.
 * <p>
 * Parsed profiles are cached JVM-wide per profile store and profile ID. Use {@link #clearCache()} to discard them.
 */
public class CachingGazelleProfileRule extends AbstractMessageRule {

    // Rules are cached per ProfileStore, because the same profile ID may resolve to different profile
    // definitions in different stores. Stores are weak keys, so the rules of a discarded store can be
    // garbage-collected (the cached rules must therefore never reference their store).
    // Within a store, we don't expect so many profile rules that we need eviction.
    private static final Map<ProfileStore, ConcurrentMap<String, GazelleProfileRule>> VALIDATOR_CACHE =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Logger LOG = LoggerFactory.getLogger(CachingGazelleProfileRule.class);

    private final HL7v2Transactions iheTransaction;
    private final ConformanceProfile profile;

    /**
     * When this constructor is used, the conformance profile is guessed from the actual type of the
     * message to be checked.
     *
     * @param iheTransaction IHETransaction enum
     */
    public CachingGazelleProfileRule(HL7v2Transactions iheTransaction) {
        this.iheTransaction = Objects.requireNonNull(iheTransaction, "iheTransaction must not be null");
        this.profile = null;
    }

    /**
     * When this constructor is used, a static conformance profile is used for validation.
     *
     * @param profile GazelleProfile enum
     */
    public CachingGazelleProfileRule(ConformanceProfile profile) {
        this.iheTransaction = null;
        this.profile = Objects.requireNonNull(profile, "profile must not be null");
    }

    /**
     * Obtains a {@link org.openehealth.ipf.gazelle.validation.core.GazelleProfileRule} and checks the
     * message against it.
     *
     * @param message message to be validated
     * @return an (potentially empty) array of profile violations
     */
    @Override
    public ValidationException[] apply(Message message) {
        try {
            var profile = this.profile == null ? guessGazelleProfile(iheTransaction, message) : this.profile;
            if (profile == null) {
                return failed("No matching profile could be loaded for message of type " + message.getClass().getName());
            }
            var parser = message.getParser();
            if (parser == null) {
                return failed("Message of type " + message.getClass().getName() + " has no parser, so its HapiContext " +
                        "and profile store cannot be determined");
            }
            var rule = parseProfile(parser.getHapiContext(), profile.profileInfo().profileId());
            if (rule == null) {
                return failed("Cannot parse conformance profile " + profile.profileInfo().profileId() + " for message of type " + message.getClass().getName());
            }
            return rule.apply(message);
        } catch (Exception e) {
            return failed(e);
        }
    }

    /**
     * Parses the conformance profile and caches an instance of {@link org.openehealth.ipf.gazelle.validation.core.GazelleProfileRule}
     * that checks against this profile.
     *
     * @param hapiContext HapiContext from the message
     * @param profileId   profile id
     * @return GazelleProfileRule
     */
    protected GazelleProfileRule parseProfile(HapiContext hapiContext, String profileId) throws JAXBException, IOException {
        var profileStore = hapiContext.getProfileStore();
        if (profileStore == null) {
            throw new IllegalStateException("HapiContext has no ProfileStore configured; expected an instance of " +
                    GazelleProfileStore.class.getName());
        }
        var storeCache = VALIDATOR_CACHE.computeIfAbsent(profileStore, store -> new ConcurrentHashMap<>());

        // computeIfAbsent loads each profile exactly once, even if many threads request it concurrently.
        // Failed loads (exception or null) are not cached, so they are retried on the next request.
        try {
            return storeCache.computeIfAbsent(profileId, id -> {
                LOG.debug("Conformance Profile {} requested, but has not been parsed yet", id);
                try {
                    var rule = loadRule(hapiContext, id);
                    LOG.debug("Added conformance profile {} to cache", id);
                    return rule;
                } catch (JAXBException | IOException e) {
                    throw new ProfileLoadingException(e);
                }
            });
        } catch (ProfileLoadingException e) {
            if (e.getCause() instanceof JAXBException jaxbException) throw jaxbException;
            throw (IOException) e.getCause();
        }
    }

    /**
     * Loads and parses a conformance profile from the profile store of the HapiContext. This method is called at most
     * once per profile store and profile ID (unless it fails), and must not call {@link #parseProfile(HapiContext, String)}.
     *
     * @param hapiContext HapiContext from the message
     * @param profileId   profile id
     * @return GazelleProfileRule
     */
    protected GazelleProfileRule loadRule(HapiContext hapiContext, String profileId) throws JAXBException, IOException {
        var profileStore = hapiContext.getProfileStore();
        // For URL-based stores (incl. GazelleProfileStore), parse the raw bytes so that the XML parser
        // determines the encoding from the XML declaration. ProfileStore#getProfile decodes using the
        // platform default charset, which corrupts non-ASCII content on non-UTF-8 platforms.
        if (profileStore instanceof URLProfileStore urlProfileStore) {
            var url = urlProfileStore.getURL(profileId);
            if (url == null) {
                throw new IOException("Conformance profile " + profileId + " not found in profile store " + profileStore);
            }
            try (var in = url.openStream()) {
                return new GazelleProfileRule(unmarshal(new StreamSource(in, url.toExternalForm())));
            }
        }
        var profileString = profileStore.getProfile(profileId);
        if (profileString == null) {
            throw new IOException("Conformance profile " + profileId + " not found in profile store " + profileStore);
        }
        return new GazelleProfileRule(unmarshal(new StreamSource(new StringReader(profileString), profileId)));
    }

    private static HL7V2XConformanceProfile unmarshal(StreamSource source) throws JAXBException {
        XMLStreamReader reader = null;
        try {
            reader = secureXmlInputFactory().createXMLStreamReader(source);
            var result = JAXBIntrospector.getValue(JaxbContextHolder.CONTEXT.createUnmarshaller().unmarshal(reader));
            if (result instanceof HL7V2XConformanceProfile conformanceProfile) {
                return conformanceProfile;
            }
            throw new JAXBException("Expected a HL7v2xConformanceProfile in " + source.getSystemId() +
                    ", but got " + (result == null ? null : result.getClass().getName()));
        } catch (XMLStreamException e) {
            throw new JAXBException("Could not read conformance profile " + source.getSystemId(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    LOG.debug("Could not close XML reader for {}", source.getSystemId(), e);
                }
            }
        }
    }

    // Profile stores are pluggable, so do not trust their content: no DTDs, no external entities.
    // XMLInputFactory is not guaranteed to be thread-safe; profiles are loaded rarely, so create one per load.
    private static XMLInputFactory secureXmlInputFactory() {
        var factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory;
    }

    // JAXBContext is thread-safe and expensive to create, so it is created once, lazily on first profile load.
    private static final class JaxbContextHolder {
        private static final JAXBContext CONTEXT;

        static {
            try {
                CONTEXT = JAXBContext.newInstance(HL7V2XConformanceProfile.class);
            } catch (JAXBException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    // Tunnels checked exceptions out of ConcurrentHashMap#computeIfAbsent
    private static final class ProfileLoadingException extends RuntimeException {
        ProfileLoadingException(Exception cause) {
            super(cause);
        }
    }

    /**
     * Discards all cached conformance profile rules, e.g. after profiles in a profile store have changed.
     * Rules are re-parsed on demand.
     */
    public static void clearCache() {
        VALIDATOR_CACHE.clear();
    }

    /**
     * @return the number of conformance profile rules currently cached, across all profile stores
     */
    public static int getCacheSize() {
        synchronized (VALIDATOR_CACHE) {
            return VALIDATOR_CACHE.values().stream().mapToInt(Map::size).sum();
        }
    }
}
