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
public enum ItiProfile implements GazelleProfile {


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
    ITI_30_ACK                    ("1.3.6.1.4.12559.11.1.1.33",  "ITI-30", "ACK", "2.5"),
    ITI_22_QBP_ZV1                ("1.3.6.1.4.12559.11.1.1.34",  "ITI-22", "QBP^ZV1^QBP_Q21", "2.5"),
    ITI_21_RSP_K22                ("1.3.6.1.4.12559.11.1.1.35",  "ITI-21", "RSP^K22^RSP_K21", "2.5"),
    ITI_30_ADT_A28                ("1.3.6.1.4.12559.11.1.1.36",  "ITI-30", "ADT^A28^ADT_A05", "2.5"),
    ITI_30_ADT_A37                ("1.3.6.1.4.12559.11.1.1.37",  "ITI-30", "ADT^A37^ADT_A37", "2.5"),
    ITI_30_ADT_A47                ("1.3.6.1.4.12559.11.1.1.38",  "ITI-30", "ADT^A47^ADT_A30", "2.5"),
    ITI_30_ADT_A31                ("1.3.6.1.4.12559.11.1.1.39",  "ITI-30", "ADT^A31^ADT_A05", "2.5"),
    ITI_30_ADT_A40                ("1.3.6.1.4.12559.11.1.1.40",  "ITI-30", "ADT^A40^ADT_A39", "2.5"),
    ITI_30_ADT_A24                ("1.3.6.1.4.12559.11.1.1.41",  "ITI-30", "ADT^A24^ADT_A24", "2.5"),
    ITI_22_RSP_ZV2                ("1.3.6.1.4.12559.11.1.1.42",  "ITI-22", "RSP^ZV2^RSP_ZV2", "2.5"),
    ITI_22_QCN_J01                ("1.3.6.1.4.12559.11.1.1.43",  "ITI-22", "QCN^J01^QCN_J01", "2.5"),
    ITI_31_ACK                    ("1.3.6.1.4.12559.11.1.1.44",  "ITI-31", "ACK", "2.5"),
    ITI_31_ADT_A16                ("1.3.6.1.4.12559.11.1.1.45",  "ITI-31", "ADT^A16^ADT_A16", "2.5"),
    ITI_31_ADT_A07                ("1.3.6.1.4.12559.11.1.1.46",  "ITI-31", "ADT^A07^ADT_A06", "2.5"),
    ITI_31_ADT_A25                ("1.3.6.1.4.12559.11.1.1.47",  "ITI-31", "ADT^A25^ADT_A21", "2.5"),
    ITI_31_ADT_A52                ("1.3.6.1.4.12559.11.1.1.48",  "ITI-31", "ADT^A52^ADT_A52", "2.5"),
    ITI_31_ADT_A08                ("1.3.6.1.4.12559.11.1.1.49",  "ITI-31", "ADT^A08^ADT_A01", "2.5"),
    ITI_31_ADT_A26                ("1.3.6.1.4.12559.11.1.1.50",  "ITI-31", "ADT^A26^ADT_A21", "2.5"),
    ITI_31_ADT_A44                ("1.3.6.1.4.12559.11.1.1.51",  "ITI-31", "ADT^A44^ADT_A43", "2.5"),
    ITI_31_ADT_A53                ("1.3.6.1.4.12559.11.1.1.52",  "ITI-31", "ADT^A53^ADT_A52", "2.5"),
    ITI_31_ADT_A09                ("1.3.6.1.4.12559.11.1.1.53",  "ITI-31", "ADT^A09^ADT_A09", "2.5"),
    ITI_31_ADT_A27                ("1.3.6.1.4.12559.11.1.1.54",  "ITI-31", "ADT^A27^ADT_A21", "2.5"),
    ITI_31_ADT_A54                ("1.3.6.1.4.12559.11.1.1.55",  "ITI-31", "ADT^A54^ADT_A54", "2.5"),
    ITI_31_ADT_A55                ("1.3.6.1.4.12559.11.1.1.56",  "ITI-31", "ADT^A55^ADT_A52", "2.5"),
    ITI_31_ADT_A38                ("1.3.6.1.4.12559.11.1.1.57",  "ITI-31", "ADT^A38^ADT_A38", "2.5"),
    ITI_31_ADT_Z99                ("1.3.6.1.4.12559.11.1.1.58",  "ITI-31", "ADT^Z99^ADT_A01", "2.5"),
    ITI_31_ADT_A10                ("1.3.6.1.4.12559.11.1.1.59",  "ITI-31", "ADT^A10^ADT_A09", "2.5"),
    ITI_31_ADT_A01                ("1.3.6.1.4.12559.11.1.1.60",  "ITI-31", "ADT^A01^ADT_A01", "2.5"),
    ITI_31_ADT_A11                ("1.3.6.1.4.12559.11.1.1.61",  "ITI-31", "ADT^A11^ADT_A09", "2.5"),
    ITI_31_ADT_A02                ("1.3.6.1.4.12559.11.1.1.62",  "ITI-31", "ADT^A02^ADT_A02", "2.5"),
    ITI_31_ADT_A12                ("1.3.6.1.4.12559.11.1.1.63",  "ITI-31", "ADT^A12^ADT_A12", "2.5"),
    ITI_31_ADT_A03                ("1.3.6.1.4.12559.11.1.1.64",  "ITI-31", "ADT^A03^ADT_A03", "2.5"),
    ITI_31_ADT_A21                ("1.3.6.1.4.12559.11.1.1.65",  "ITI-31", "ADT^A21^ADT_A21", "2.5"),
    ITI_31_ADT_A13                ("1.3.6.1.4.12559.11.1.1.66",  "ITI-31", "ADT^A13^ADT_A01", "2.5"),
    ITI_31_ADT_A04                ("1.3.6.1.4.12559.11.1.1.67",  "ITI-31", "ADT^A04^ADT_A01", "2.5"),
    ITI_31_ADT_A22                ("1.3.6.1.4.12559.11.1.1.68",  "ITI-31", "ADT^A22^ADT_A21", "2.5"),
    ITI_31_ADT_A40                ("1.3.6.1.4.12559.11.1.1.69",  "ITI-31", "ADT^A40^ADT_A39", "2.5"),
    ITI_31_ADT_A14                ("1.3.6.1.4.12559.11.1.1.70",  "ITI-31", "ADT^A14^ADT_A05", "2.5"),
    ITI_31_ADT_A05                ("1.3.6.1.4.12559.11.1.1.71",  "ITI-31", "ADT^A05^ADT_A05", "2.5"),
    ITI_31_ADT_A32                ("1.3.6.1.4.12559.11.1.1.72",  "ITI-31", "ADT^A32^ADT_A21", "2.5"),
    ITI_31_ADT_A15                ("1.3.6.1.4.12559.11.1.1.73",  "ITI-31", "ADT^A15^ADT_A15", "2.5"),
    ITI_31_ADT_A06                ("1.3.6.1.4.12559.11.1.1.74",  "ITI-31", "ADT^A06^ADT_A06", "2.5"),
    ITI_31_ADT_A33                ("1.3.6.1.4.12559.11.1.1.75",  "ITI-31", "ADT^A33^ADT_A21", "2.5"),
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

    private final String profileId;
    private final String transaction;
    private final String triggerEvent;
    private final String hl7version;

    ItiProfile(String profileId, String transaction, String triggerEvent, String hl7version){
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
