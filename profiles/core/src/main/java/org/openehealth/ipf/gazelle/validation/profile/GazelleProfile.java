package org.openehealth.ipf.gazelle.validation.profile;

/**
 *
 */
public interface GazelleProfile {

    public String profileId();
    public String transaction();
    public String triggerEvent();
    public String type();
    public String event();
    public String structure();
    public String hl7version();

}
