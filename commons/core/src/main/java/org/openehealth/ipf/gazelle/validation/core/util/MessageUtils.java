package org.openehealth.ipf.gazelle.validation.core.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import org.openehealth.ipf.gazelle.validation.core.GazelleProfile;

/**
 * @author Boris Stanojevic
 */
public class MessageUtils {

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

    /**
     *
     * @param message
     * @return GazelleProfile that matches message type, event, structure & version
     * @throws HL7Exception
     */
    public static GazelleProfile guessGazelleProfile(Message message) throws HL7Exception {
        Terser terser = new Terser(message);
        for (GazelleProfile gazelleProfile: GazelleProfile.values()){
            if (gazelleProfile.type().equals(messageType(terser))
                && gazelleProfile.event().equals(triggerEvent(terser))
                && gazelleProfile.structure().equals(messageStructure(terser))
                && gazelleProfile.hl7version().equals(messageVersion(terser))){
                return gazelleProfile;
            }
        }
        return null;
    }
}
