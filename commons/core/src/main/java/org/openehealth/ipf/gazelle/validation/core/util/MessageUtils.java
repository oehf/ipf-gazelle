/*
 * Copyright 2013 the original author or authors.
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
package org.openehealth.ipf.gazelle.validation.core.util;

import java.util.List;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.ValidationException;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;

import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileViolatedWhen;

/**
 * @author Boris Stanojevic
 */
public abstract class MessageUtils {

    private MessageUtils() {
    }

    /**
     * @return event type of the message, e.g. 'ADT'
     */
    public static String messageType(Terser t) throws HL7Exception {
        return t.get("/MSH-9-1");
    }

    /**
     * @return trigger event of the message, e.g. 'A01'
     */
    public static String triggerEvent(Terser t) throws HL7Exception {
        return t.get("/MSH-9-2");
    }

    /**
     * @return structure of the message, e.g. 'ADT_A01'
     */
    public static String messageStructure(Terser t) throws HL7Exception {
        return t.get("/MSH-9-3");
    }

    /**
     * @return structure of the message, e.g. '2.5.1'
     */
    public static String messageVersion(Terser t) throws HL7Exception {
        return t.get("/MSH-12-1");
    }

    public static String mshField(String mshFieldNo, Terser t) throws HL7Exception {
        return t.get(mshFieldNo);
    }


    /**
     * Finds out the exact profile inside the one specific IHETransaction
     * the given message belongs to.
     *
     * @param iheTransaction concrete IHETransaction
     * @param message        hapi message
     * @return GazelleProfile that matches message type, event, structure & version
     * @throws HL7Exception
     */
    public static ConformanceProfile guessGazelleProfile(HL7v2Transactions iheTransaction, Message message)
            throws HL7Exception {
        Terser terser = new Terser(message);
        for (ConformanceProfile conformanceProfile : iheTransaction.conformanceProfiles()) {
            if (matches(conformanceProfile.profileInfo(), terser)) {
                return conformanceProfile;
            }
        }
        return null;
    }

    private static boolean matches(ConformanceProfileInfo conformanceProfileInfo, Terser terser) throws HL7Exception {
        return conformanceProfileInfo.type().equals(messageType(terser))
                && conformanceProfileInfo.event().equals(triggerEvent(terser))
                // && conformanceProfileInfo.structure().equals(messageStructure(terser))
                && conformanceProfileInfo.hl7version().equals(messageVersion(terser));
    }

    public static void checkMSHTypeField(String profileValue, Terser terser, List<ValidationException> violations) {
        checkMSHField("/MSH-9-1", profileValue, terser, ProfileValidationMessage.WRONG_MSH_TYPE_FIELD, violations);
    }

    public static void checkMSHEventField(String profileValue, Terser terser, List<ValidationException> violations) {
        if (!"ALL".equals(profileValue))
            checkMSHField("/MSH-9-2", profileValue, terser, ProfileValidationMessage.WRONG_MSH_EVENT_FIELD, violations);
    }

    public static void checkMSHStructureField(String profileValue, Terser terser, List<ValidationException> violations) {
        checkMSHField("/MSH-9-3", profileValue, terser, ProfileValidationMessage.WRONG_MSH_STRUCTURE_FIELD, violations);
    }

    public static void checkMSHVersionField(String profileValue, Terser terser, List<ValidationException> violations) {
        checkMSHField("/MSH-12-1", profileValue, terser, ProfileValidationMessage.WRONG_MSH_VERSION_FIELD, violations);
    }

    private static void checkMSHField(String fieldNo, String profileValue, Terser terser,
                                      ProfileValidationMessage validationMessage, List<ValidationException> violations) {
        String mshValue;
        try {
            mshValue = mshField(fieldNo, terser);
            profileViolatedWhen(mshValue == null || !mshValue.equals(profileValue), violations,
                    validationMessage, mshValue, profileValue);
        } catch (HL7Exception e) {
            violations.add(new ValidationException("Could not obtain" + fieldNo, e));
        }

    }
}
