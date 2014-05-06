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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationException;
import ca.uhn.hl7v2.validation.impl.AbstractMessageRule;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.GazelleProfile;
import org.openehealth.ipf.gazelle.validation.profile.IHETransaction;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.guessGazelleProfile;

/**
 * A modified conformance profile rule that wraps and caches {@link GazelleProfileRule} instances. Note that the HapiContext associated
 * with the message to be validated MUST return an instance of {@link org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore}
 * as a return value to {@link ca.uhn.hl7v2.HapiContext#getProfileStore()}.
 */
public class CachingGazelleProfileRule extends AbstractMessageRule {

    private static final Map<String, GazelleProfileRule> VALIDATOR_CACHE = new LinkedHashMap<String, GazelleProfileRule>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GazelleProfileRule> eldest) {
            return size() > 50;
        }
    };

    private static final Unmarshaller UNMARSHALLER;

    static {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HL7V2XConformanceProfile.class);
            UNMARSHALLER = jaxbContext.createUnmarshaller();
        } catch (JAXBException jaxbException) {
            throw new RuntimeException(jaxbException.getMessage());
        }
    }

    private IHETransaction iheTransaction;
    private GazelleProfile profile;

    /**
     * When this constructor is used, the conformance profile is guessed from the actual type of the
     * message to be checked.
     *
     * @param iheTransaction IHETransaction enum
     */
    public CachingGazelleProfileRule(IHETransaction iheTransaction) {
        this.iheTransaction = iheTransaction;
    }

    /**
     * When this constructor is used, a static confirmance profile is used for validation.
     *
     * @param profile GazelleProfile enum
     */
    public CachingGazelleProfileRule(GazelleProfile profile) {
        this.profile = profile;
    }

    /**
     * Obtains a {@link org.openehealth.ipf.gazelle.validation.core.GazelleProfileRule} and checks the
     * message against it.
     *
     * @param message
     * @return
     */
    @Override
    public ValidationException[] apply(Message message) {
        try {
            GazelleProfile profile = this.profile == null ? guessGazelleProfile(iheTransaction, message) : this.profile;
            GazelleProfileRule rule = parseProfile(message.getParser().getHapiContext(), profile.profileId());
            return rule.apply(message);
        } catch (Exception e) {
            return failed("No matching profile could be loaded for message of type " + message.getClass().getName());
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
    synchronized protected GazelleProfileRule parseProfile(HapiContext hapiContext, String profileId) throws JAXBException, IOException {
        String profileString = hapiContext.getProfileStore().getProfile(profileId);
        GazelleProfileRule validator;
        if (VALIDATOR_CACHE.containsKey(profileId)) {
            validator = VALIDATOR_CACHE.get(profileId);
        } else {
            HL7V2XConformanceProfile conformanceProfile =
                    (HL7V2XConformanceProfile) UNMARSHALLER.unmarshal(new ByteArrayInputStream(profileString.getBytes()));
            validator = new GazelleProfileRule(conformanceProfile);
            VALIDATOR_CACHE.put(profileId, validator);
        }
        return validator;
    }
}
