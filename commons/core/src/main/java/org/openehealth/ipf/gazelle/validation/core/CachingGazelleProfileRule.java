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
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationException;
import ca.uhn.hl7v2.validation.impl.AbstractMessageRule;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.guessGazelleProfile;

/**
 * A modified conformance profile rule that wraps and caches {@link GazelleProfileRule} instances. Note that the HapiContext associated
 * with the message to be validated MUST return an instance of {@link org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore}
 * as a return value to {@link ca.uhn.hl7v2.HapiContext#getProfileStore()}.
 */
public class CachingGazelleProfileRule extends AbstractMessageRule {

    // Don't expect so many profile rules that we need eviction here
    private static final ConcurrentHashMap<String, GazelleProfileRule> VALIDATOR_CACHE = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(CachingGazelleProfileRule.class);

    private HL7v2Transactions iheTransaction;
    private ConformanceProfile profile;

    /**
     * When this constructor is used, the conformance profile is guessed from the actual type of the
     * message to be checked.
     *
     * @param iheTransaction IHETransaction enum
     */
    public CachingGazelleProfileRule(HL7v2Transactions iheTransaction) {
        this.iheTransaction = iheTransaction;
    }

    /**
     * When this constructor is used, a static confirmance profile is used for validation.
     *
     * @param profile GazelleProfile enum
     */
    public CachingGazelleProfileRule(ConformanceProfile profile) {
        this.profile = profile;
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
            ConformanceProfile profile = this.profile == null ? guessGazelleProfile(iheTransaction, message) : this.profile;
            if (profile == null) {
                return failed("No matching profile could be loaded for message of type " + message.getClass().getName());
            }
            GazelleProfileRule rule = parseProfile(message.getParser().getHapiContext(), profile.profileInfo().profileId());
            if (rule == null) {
                return failed("Cannot parse conformance profile " + profile.profileInfo().profileId() + " for message of type " + message.getClass().getName());
            }
            return rule.apply(message);
        } catch (Exception e) {
            return failed(e);
        }
    }

    /**
     * Parses the confirmance profile and caches an instance of {@link org.openehealth.ipf.gazelle.validation.core.GazelleProfileRule}
     * that checks against this profile.
     *
     * @param hapiContext HapiContext from the message
     * @param profileId   profile id
     * @return GazelleProfileRule
     * @throws JAXBException
     * @throws IOException
     */
    protected GazelleProfileRule parseProfile(HapiContext hapiContext, String profileId) throws JAXBException, IOException {

        GazelleProfileRule validator = VALIDATOR_CACHE.get(profileId);
        if (validator != null) return validator;

        // Several threads could attempt to load the same profile, but this should only happen at the very
        // beginning. As a benefit we don't need any locking here.
        LOG.debug("Conformance Profile {} requested, but has not been parsed yet", profileId);
        GazelleProfileRule loaded = loadRule(hapiContext, profileId);

        if (VALIDATOR_CACHE.putIfAbsent(profileId, loaded) == null) {
            LOG.debug("Added conformance profile {} to cache", profileId);
            return loaded;
        } else {
            LOG.debug("Parsed conformance profile {}, but was already added");
            return VALIDATOR_CACHE.get(profileId);
        }

    }

    protected GazelleProfileRule loadRule(HapiContext hapiContext, String profileId) throws JAXBException, IOException {
        String profileString = hapiContext.getProfileStore().getProfile(profileId);
        HL7V2XConformanceProfile conformanceProfile =
                (HL7V2XConformanceProfile) getUnmarshaller().unmarshal(new ByteArrayInputStream(profileString.getBytes()));
        GazelleProfileRule validator = new GazelleProfileRule(conformanceProfile);
        return validator;
    }

    // Unmarshaller are not thread-safe, but creation should happen only once for each profile
    private Unmarshaller getUnmarshaller() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HL7V2XConformanceProfile.class);
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException jaxbException) {
            throw new RuntimeException(jaxbException.getMessage());
        }
    }

    // Just for testing purposes
    void reset() {
        VALIDATOR_CACHE.clear();
    }
}
