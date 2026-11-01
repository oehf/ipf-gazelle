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
import java.util.Objects;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.ValidationException;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;

import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileNotHL7Compliant;
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
     * @return GazelleProfile that matches message type, event & version. A profile with exactly the message's
     * version is preferred over one with a {@link #isCompatibleVersion(String, String) compatible} version.
     */
    public static ConformanceProfile guessGazelleProfile(HL7v2Transactions iheTransaction, Message message)
            throws HL7Exception {
        // Read the MSH values once, not once per candidate profile
        var msh = msh(message);
        var type = Terser.get(msh, 9, 0, 1, 1);
        var event = Terser.get(msh, 9, 0, 2, 1);
        var version = Terser.get(msh, 12, 0, 1, 1);
        ConformanceProfile compatibleMatch = null;
        for (var conformanceProfile : iheTransaction.conformanceProfiles()) {
            var info = conformanceProfile.profileInfo();
            if (info.type().equals(type) && info.event().equals(event)) {
                // && info.structure().equals(structure)
                if (info.hl7version().equals(version)) {
                    return conformanceProfile;
                }
                if (compatibleMatch == null && isCompatibleVersion(info.hl7version(), version)) {
                    compatibleMatch = conformanceProfile;
                }
            }
        }
        return compatibleMatch;
    }

    /**
     * Checks whether two HL7 versions are compatible, i.e. equal or one is a revision of the other (e.g.
     * 2.5 and 2.5.1). Some Gazelle profiles state the base version (2.5) where the IHE technical framework
     * requires the revision (2.5.1), so such versions must be accepted interchangeably.
     *
     * @param version1 HL7 version, e.g. "2.5"
     * @param version2 HL7 version, e.g. "2.5.1"
     * @return true if the versions are compatible
     */
    public static boolean isCompatibleVersion(String version1, String version2) {
        if (version1 == null || version2 == null) return false;
        if (version1.equals(version2)) return true;
        return version1.startsWith(version2 + ".") || version2.startsWith(version1 + ".");
    }

    /**
     * @return the MSH segment of the message
     */
    public static Segment msh(Message message) throws HL7Exception {
        return (Segment) message.get("MSH");
    }

    public static void checkMSHTypeField(String profileValue, Terser terser, List<ValidationException> violations) {
        checkMSHField("/MSH-9-1", () -> mshField("/MSH-9-1", terser), profileValue, ProfileValidationMessage.WRONG_MSH_TYPE_FIELD, violations);
    }

    public static void checkMSHEventField(String profileValue, Terser terser, List<ValidationException> violations) {
        if (!"ALL".equals(profileValue))
            checkMSHField("/MSH-9-2", () -> mshField("/MSH-9-2", terser), profileValue, ProfileValidationMessage.WRONG_MSH_EVENT_FIELD, violations);
    }

    public static void checkMSHStructureField(String profileValue, Terser terser, List<ValidationException> violations) {
        checkMSHField("/MSH-9-3", () -> mshField("/MSH-9-3", terser), profileValue, ProfileValidationMessage.WRONG_MSH_STRUCTURE_FIELD, violations);
    }

    public static void checkMSHVersionField(String profileValue, Terser terser, List<ValidationException> violations) {
        checkMSHVersion(() -> mshField("/MSH-12-1", terser), profileValue, violations);
    }

    // The following variants take the MSH segment directly, which is considerably faster than Terser path lookups

    public static void checkMSHTypeField(String profileValue, Segment msh, List<ValidationException> violations) {
        checkMSHField("/MSH-9-1", () -> Terser.get(msh, 9, 0, 1, 1), profileValue, ProfileValidationMessage.WRONG_MSH_TYPE_FIELD, violations);
    }

    public static void checkMSHEventField(String profileValue, Segment msh, List<ValidationException> violations) {
        if (!"ALL".equals(profileValue))
            checkMSHField("/MSH-9-2", () -> Terser.get(msh, 9, 0, 2, 1), profileValue, ProfileValidationMessage.WRONG_MSH_EVENT_FIELD, violations);
    }

    public static void checkMSHStructureField(String profileValue, Segment msh, List<ValidationException> violations) {
        checkMSHField("/MSH-9-3", () -> Terser.get(msh, 9, 0, 3, 1), profileValue, ProfileValidationMessage.WRONG_MSH_STRUCTURE_FIELD, violations);
    }

    public static void checkMSHVersionField(String profileValue, Segment msh, List<ValidationException> violations) {
        checkMSHVersion(() -> Terser.get(msh, 12, 0, 1, 1), profileValue, violations);
    }

    @FunctionalInterface
    private interface FieldReader {
        String read() throws HL7Exception;
    }

    // A compatible but different version (e.g. 2.5.1 vs. 2.5) is reported as warning, any other difference as error
    private static void checkMSHVersion(FieldReader reader, String profileValue, List<ValidationException> violations) {
        try {
            var mshValue = reader.read();
            if (!Objects.equals(mshValue, profileValue)) {
                var validationMessage = isCompatibleVersion(mshValue, profileValue)
                        ? ProfileValidationMessage.COMPATIBLE_MSH_VERSION_FIELD
                        : ProfileValidationMessage.WRONG_MSH_VERSION_FIELD;
                profileNotHL7Compliant(violations, validationMessage, mshValue, profileValue);
            }
        } catch (HL7Exception e) {
            violations.add(new ValidationException("Could not obtain /MSH-12-1", e));
        }
    }

    private static void checkMSHField(String fieldNo, FieldReader reader, String profileValue,
                                      ProfileValidationMessage validationMessage, List<ValidationException> violations) {
        try {
            var mshValue = reader.read();
            profileViolatedWhen(!Objects.equals(mshValue, profileValue), violations,
                    validationMessage, mshValue, profileValue);
        } catch (HL7Exception e) {
            violations.add(new ValidationException("Could not obtain " + fieldNo, e));
        }
    }
}
