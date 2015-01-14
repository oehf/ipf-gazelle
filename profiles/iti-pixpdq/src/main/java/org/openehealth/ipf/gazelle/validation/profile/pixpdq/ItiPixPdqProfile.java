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
package org.openehealth.ipf.gazelle.validation.profile.pixpdq;

import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;

/**
 * @author Boris Stanojevic
 */
public enum ItiPixPdqProfile implements ConformanceProfile {

    ITI_8_ADT_A08                 ("1.3.6.1.4.12559.11.1.1.23",  "ITI-8", "ADT^A08^ADT_A01", "2.3.1"),
    ITI_8_ADT_A01                 ("1.3.6.1.4.12559.11.1.1.24",  "ITI-8", "ADT^A01^ADT_A01", "2.3.1"),
    ITI_8_ADT_A04                 ("1.3.6.1.4.12559.11.1.1.25",  "ITI-8", "ADT^A04^ADT_A01", "2.3.1"),
    ITI_8_ADT_A40                 ("1.3.6.1.4.12559.11.1.1.26",  "ITI-8", "ADT^A40^ADT_A39", "2.3.1"),
    ITI_8_ADT_A05                 ("1.3.6.1.4.12559.11.1.1.27",  "ITI-8", "ADT^A05^ADT_A01", "2.3.1"),
    ITI_10_ADT_A31                ("1.3.6.1.4.12559.11.1.1.28",  "ITI-10", "ADT^A31^ADT_A05", "2.5"),
    ITI_8_ACK                     ("1.3.6.1.4.12559.11.1.1.29",  "ITI-8", "ACK", "2.3.1"),
    ITI_9_RSP_K23                 ("1.3.6.1.4.12559.11.1.1.30",  "ITI-9", "RSP^K23^RSP_K23", "2.5"),
    ITI_21_QBP_Q22                ("1.3.6.1.4.12559.11.1.1.31",  "ITI-21", "QBP^Q22^QBP_Q21", "2.5"),
    ITI_21_QCN_J01                ("1.3.6.1.4.12559.11.1.1.32",  "ITI-21", "QCN^J01^QCN_J01", "2.5"),
    ITI_22_QBP_ZV1                ("1.3.6.1.4.12559.11.1.1.34",  "ITI-22", "QBP^ZV1^QBP_Q21", "2.5"),
    ITI_21_RSP_K22                ("1.3.6.1.4.12559.11.1.1.35",  "ITI-21", "RSP^K22^RSP_K21", "2.5"),
    ITI_22_RSP_ZV2                ("1.3.6.1.4.12559.11.1.1.42",  "ITI-22", "RSP^ZV2^RSP_ZV2", "2.5"),
    ITI_22_QCN_J01                ("1.3.6.1.4.12559.11.1.1.43",  "ITI-22", "QCN^J01^QCN_J01", "2.5"),
    ITI_10_ACK                    ("1.3.6.1.4.12559.11.1.1.44",  "ITI-10", "ACK", "2.5"),
    ITI_9_QBP_Q23                 ("1.3.6.1.4.12559.11.1.1.92",  "ITI-9", "QBP^Q23^QBP_Q21", "2.5"),
    ITI_8_ACK_A01                 ("1.3.6.1.4.12559.11.1.1.147", "ITI-8",  "ACK^A01^ACK", "2.3.1"),
    ITI_8_ACK_A04                 ("1.3.6.1.4.12559.11.1.1.148", "ITI-8",  "ACK^A04^ACK", "2.3.1"),
    ITI_8_ACK_A05                 ("1.3.6.1.4.12559.11.1.1.149", "ITI-8",  "ACK^A05^ACK", "2.3.1"),
    ITI_8_ACK_A08                 ("1.3.6.1.4.12559.11.1.1.150", "ITI-8",  "ACK^A08^ACK", "2.3.1"),
    ITI_8_ACK_A40                 ("1.3.6.1.4.12559.11.1.1.151", "ITI-8",  "ACK^A40^ACK", "2.3.1"),
    ITI_21_ACK_J01                ("1.3.6.1.4.12559.11.1.1.192", "ITI-21", "ACK^J01^ACK", "2.5"),
    ITI_22_ACK_J01                ("1.3.6.1.4.12559.11.1.1.193", "ITI-22", "ACK^J01^ACK", "2.5"),
    ITI_64_ADT_A43                ("1.3.6.1.4.12559.11.1.1.213", "ITI-64", "ADT^A43^ADT_A43", "2.5"),
    ITI_64_ACK_A43                ("1.3.6.1.4.12559.11.1.1.214", "ITI-64", "ACK^A43^ACK", "2.5");

    private final ConformanceProfileInfo info;

    ItiPixPdqProfile(String profileId, String transaction, String triggerEvent, String hl7version){
        info = new ConformanceProfileInfoImpl(profileId, transaction, triggerEvent, hl7version);
    }

    @Override
    public ConformanceProfileInfo profileInfo() {
        return info;
    }
}
