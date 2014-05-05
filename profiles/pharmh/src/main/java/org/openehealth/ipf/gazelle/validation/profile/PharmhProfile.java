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
package org.openehealth.ipf.gazelle.validation.profile;

/**
 * @author Boris Stanojevic
 */
public enum PharmhProfile implements GazelleProfile {

    PHARM_H1_ORP_O10              ("1.3.6.1.4.12559.11.1.1.154", "PHARM-H1", "ORP^O10^ORP_O10", "2.6"),
    PHARM_H2_RDE_O11              ("1.3.6.1.4.12559.11.1.1.155", "PHARM-H2", "RDE^O11^RDE_O11", "2.6"),
    PHARM_H2_RRE_O12_PRES_PLACER  ("1.3.6.1.4.12559.11.1.1.156", "PHARM-H2", "RRE^O12^RRE_O12", "2.6"),
    PHARM_H3_RRG_O16_PRES_PLACER  ("1.3.6.1.4.12559.11.1.1.157", "PHARM-H3", "RRG^O16^RRG_O16", "2.6"),
    PHARM_H3_RGV_O15              ("1.3.6.1.4.12559.11.1.1.158", "PHARM-H3", "RGV^O15^RGV_O15", "2.6"),
    PHARM_H4_RAS_O17              ("1.3.6.1.4.12559.11.1.1.159", "PHARM-H4", "RAS^O17^RAS_O17", "2.6"),
    PHARM_H4_RRA_O18_PRES_PLACER  ("1.3.6.1.4.12559.11.1.1.160", "PHARM-H4", "RRA^O18^RRA_O18", "2.6"),
    PHARM_H1_OMP_O09              ("1.3.6.1.4.12559.11.1.1.161", "PHARM-H1", "OMP^O09^OMP_O09", "2.6"),
    PHARM_H3_RRG_O16_MED_ADM_INFO ("1.3.6.1.4.12559.11.1.1.162", "PHARM-H3", "RRG^O16^RRG_O16", "2.6"),
    PHARM_H5_ORP_O10_MED_ADM_INFO ("1.3.6.1.4.12559.11.1.1.163", "PHARM-H5", "ORP^O10^ORP_O10", "2.6"),
    PHARM_H6_RRE_O12              ("1.3.6.1.4.12559.11.1.1.164", "PHARM-H6", "RRE^O12^RRE_O12", "2.6"),
    PHARM_H5_OMP_O09              ("1.3.6.1.4.12559.11.1.1.165", "PHARM-H5", "OMP^O09^OMP_O09", "2.6"),
    PHARM_H3_RRG_O16              ("1.3.6.1.4.12559.11.1.1.166", "PHARM-H3", "RRG^O16^RRG_O16", "2.6"),
    PHARM_H6_RDE_O11              ("1.3.6.1.4.12559.11.1.1.167", "PHARM-H6", "RDE^O11^RDE_O11", "2.6"),
    PHARM_H4_RRA_O18_MED_DIS      ("1.3.6.1.4.12559.11.1.1.168", "PHARM-H4", "RRA^O18^RRA_O18", "2.6"),
    PHARM_H5_ORP_O10_MED_DIS      ("1.3.6.1.4.12559.11.1.1.169", "PHARM-H5", "ORP^O10^ORP_O10", "2.6"),
    PHARM_H2_RRE_O12_MED_DIS      ("1.3.6.1.4.12559.11.1.1.170", "PHARM-H2", "RRE^O12^RRE_O12", "2.6");


    private final String profileId;
    private final String transaction;
    private final String triggerEvent;
    private final String hl7version;

    PharmhProfile(String profileId, String transaction, String triggerEvent, String hl7version){
        this.profileId = profileId;
        this.triggerEvent = triggerEvent;
        this.transaction = transaction;
        this.hl7version = hl7version;
    }

    public String profileId()    { return profileId; }
    public String transaction()  { return transaction; }
    public String triggerEvent() { return triggerEvent; }
    public String type()         { return triggerEvent.split("\\^")[0]; }
    public String event()        { return triggerEvent.split("\\^").length > 1 ? triggerEvent.split("\\^")[1]:""; }
    public String structure()    { return triggerEvent.split("\\^").length > 2 ? triggerEvent.split("\\^")[2]:""; }
    public String hl7version()   { return hl7version; }
}
