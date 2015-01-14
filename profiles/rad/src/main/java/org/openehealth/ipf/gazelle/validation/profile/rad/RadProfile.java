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
package org.openehealth.ipf.gazelle.validation.profile.rad;


import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;

/**
 * @author Boris Stanojevic
 */
public enum RadProfile implements ConformanceProfile {

    RAD_4_ORM_O01                 ("1.3.6.1.4.12559.11.1.1.10",  "RAD-4",  "ORM^O01^ORM_O01", "2.3.1"),
    RAD_2_ORM_O01_NW_V25          ("1.3.6.1.4.12559.11.1.1.103", "RAD-2",  "ORM^O01^ORM_O01", "2.5"),
    RAD_2_ORM_O01_CA_V25          ("1.3.6.1.4.12559.11.1.1.104", "RAD-2",  "ORM^O01^ORM_O01", "2.5"),
    RAD_3_ORR_O02_V25             ("1.3.6.1.4.12559.11.1.1.105", "RAD-3",  "ORR^O02^ORR_O02", "2.5"),
    RAD_35_DFT_P03                ("1.3.6.1.4.12559.11.1.1.11",  "RAD-35", "DFT^P03^DFT_P03", "2.3.1"),
    RAD_28_ORU_R01                ("1.3.6.1.4.12559.11.1.1.112", "RAD-28", "ORU^R01^ORU_R01", "2.5"),
    RAD_48_SIU_S15                ("1.3.6.1.4.12559.11.1.1.130", "RAD-48", "SIU^S15^SIU_S15", "2.4"),
    RAD_13_ACK_O01                ("1.3.6.1.4.12559.11.1.1.183", "RAD-13", "ACK^O01^ACK", "2.3.1"),
    RAD_4_ACK_O01                 ("1.3.6.1.4.12559.11.1.1.184", "RAD-4",  "ACK^O01^ACK", "2.3.1"),
    RAD_13_ORM_O01                ("1.3.6.1.4.12559.11.1.1.185", "RAD-13", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_2_ORM_O01_NW_V231         ("1.3.6.1.4.12559.11.1.1.194", "RAD-2", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_2_ORM_O01_CA_V231         ("1.3.6.1.4.12559.11.1.1.195", "RAD-2", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_3_ORR_O02_V231            ("1.3.6.1.4.12559.11.1.1.196", "RAD-3", "ORR^O02^ORR_O02", "2.3.1"),
    RAD_3_ORM_O01_SN_EYE          ("1.3.6.1.4.12559.11.1.1.197", "RAD-3", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_3_ORM_O01_SC_EYE          ("1.3.6.1.4.12559.11.1.1.198", "RAD-3", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_3_ORM_O01_OC_EYE          ("1.3.6.1.4.12559.11.1.1.199", "RAD-3", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_2_ACK_O01_EYE             ("1.3.6.1.4.12559.11.1.1.200", "RAD-2", "ACK^O01^ACK", "2.3.1"),
    RAD_3_OMG_O19_SN              ("1.3.6.1.4.12559.11.1.1.201", "RAD-3", "OMG^O19^OMG_019", "2.5.1"),
    RAD_3_OMG_O19_SC              ("1.3.6.1.4.12559.11.1.1.202", "RAD-3", "OMG^O19^OMG_019", "2.5.1"),
    RAD_3_OMG_O19_OC              ("1.3.6.1.4.12559.11.1.1.203", "RAD-3", "OMG^O19^OMG_019", "2.5.1"),
    RAD_2_ACK_O19                 ("1.3.6.1.4.12559.11.1.1.204", "RAD-2", "ACK^O19^ACK", "2.5.1"),
    RAD_2_OMG_O19_NW              ("1.3.6.1.4.12559.11.1.1.205", "RAD-2", "OMG^O19^OMG_O19", "2.5.1"),
    RAD_2_OMG_O19_CA              ("1.3.6.1.4.12559.11.1.1.206", "RAD-2", "OMG^O19^OMG_O19", "2.5.1"),
    RAD_3_ORG_O20                 ("1.3.6.1.4.12559.11.1.1.207", "RAD-3", "ORG^O20^ORG_O20", "2.5.1"),
    RAD_3_ACK_O19                 ("1.3.6.1.4.12559.11.1.1.208", "RAD-3", "ACK^O19^ACK", "2.5.1"),
    RAD_4_OMI_O23                 ("1.3.6.1.4.12559.11.1.1.209", "RAD-4", "OMI^O23^OMI_O23", "2.5.1"),
    RAD_2_ACK_O01_RAD             ("1.3.6.1.4.12559.11.1.1.21",  "RAD-2", "ACK^O01^ACK", "2.3.1"),
    RAD_13_OMI_O23                ("1.3.6.1.4.12559.11.1.1.210", "RAD-13", "OMI^O23^OMI_O23", "2.5.1"),
    RAD_4_ACK_O23                 ("1.3.6.1.4.12559.11.1.1.211", "RAD-4", "ACK^O23^ACK", "2.5.1"),
    RAD_13_ACK_O23                ("1.3.6.1.4.12559.11.1.1.212", "RAD-13", "ACK^O23^ACK", "2.5.1"),
    RAD_48_SIU_S12                ("1.3.6.1.4.12559.11.1.1.4",   "RAD-48", "SIU^S12^SIU_S12", "2.3.1"),
    RAD_48_SIU_S13                ("1.3.6.1.4.12559.11.1.1.5",   "RAD-48", "SIU^S13^SIU_S13", "2.3.1"),
    RAD_3_ORM_O01_OC_RAD          ("1.3.6.1.4.12559.11.1.1.7",   "RAD-3",  "ORM^O01^ORM_O01", "2.3.1"),
    RAD_1_ADT_A38                 ("1.3.6.1.4.12559.11.1.1.76",  "RAD-1", "ADT^A38^ADT_A38", "2.3.1"),
    RAD_1_ADT_A01                 ("1.3.6.1.4.12559.11.1.1.77",  "RAD-1", "ADT^A01^ADT_A01", "2.3.1"),
    RAD_1_ADT_A11                 ("1.3.6.1.4.12559.11.1.1.78",  "RAD-1", "ADT^A11^ADT_A09", "2.3.1"),
    RAD_1_ADT_A04                 ("1.3.6.1.4.12559.11.1.1.79",  "RAD-1", "ADT^A04^ADT_A01", "2.3.1"),
    RAD_3_ORM_O01_SC_RAD          ("1.3.6.1.4.12559.11.1.1.8",   "RAD-3", "ORM^O01^ORM_O01", "2.3.1"),
    RAD_1_ADT_A05                 ("1.3.6.1.4.12559.11.1.1.80",  "RAD-1", "ADT^A05^ADT_A01", "2.3.1"),
    RAD_12_ADT_A07                ("1.3.6.1.4.12559.11.1.1.81",  "RAD-12", "ADT^A07^ADT_A06", "2.3.1"),
    RAD_12_ADT_A08                ("1.3.6.1.4.12559.11.1.1.82",  "RAD-12", "ADT^A08^ADT_A01", "2.3.1"),
    RAD_12_ADT_A02                ("1.3.6.1.4.12559.11.1.1.83",  "RAD-12", "ADT^A02^ADT_A02", "2.3.1"),
    RAD_12_ADT_A03                ("1.3.6.1.4.12559.11.1.1.84",  "RAD-12", "ADT^A03^ADT_A03", "2.3.1"),
    RAD_12_ADT_A12                ("1.3.6.1.4.12559.11.1.1.85",  "RAD-12", "ADT^A12^ADT_A12", "2.3.1"),
    RAD_12_ADT_A13                ("1.3.6.1.4.12559.11.1.1.86",  "RAD-12", "ADT^A13^ADT_A01", "2.3.1"),
    RAD_12_ADT_A40                ("1.3.6.1.4.12559.11.1.1.87",  "RAD-12", "ADT^A40^ADT_A39", "2.3.1"),
    RAD_12_ADT_A06                ("1.3.6.1.4.12559.11.1.1.88",  "RAD-12", "ADT^A06^ADT_A06", "2.3.1"),
    RAD_36_BAR_P01                ("1.3.6.1.4.12559.11.1.1.89",  "RAD-36", "BAR^P01^BAR_P01", "2.3.1"),
    RAD_3_ORM_O01_SN_RAD          ("1.3.6.1.4.12559.11.1.1.9",   "RAD-3",  "ORM^O01^ORM_O01", "2.3.1"),
    RAD_36_BAR_P05                ("1.3.6.1.4.12559.11.1.1.90",  "RAD-36", "BAR^P05^BAR_P01", "2.3.1"),
    RAD_36_BAR_P06                ("1.3.6.1.4.12559.11.1.1.91",  "RAD-36", "BAR^P06^BAR_P06", "2.3.1");

    private final ConformanceProfileInfo info;

    RadProfile(String profileId, String transaction, String triggerEvent, String hl7version){
        info = new ConformanceProfileInfoImpl(profileId, transaction, triggerEvent, hl7version);
    }

    @Override
    public ConformanceProfileInfo profileInfo() {
        return info;
    }
}
