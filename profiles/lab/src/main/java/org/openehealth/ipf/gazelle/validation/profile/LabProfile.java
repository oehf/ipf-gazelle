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
public enum LabProfile implements GazelleProfile {

    LAB_4_OML_O21                 ("1.3.6.1.4.12559.11.1.1.1",   "LAB-4",  "OML^O21^OML_O21", "2.5"),
    LAB_22_RSP_WOS                ("1.3.6.1.4.12559.11.1.1.100", "LAB-22", "RSP^WOS^RSP_K11", "2.5"),
    LAB_23_ACK_R22                ("1.3.6.1.4.12559.11.1.1.101", "LAB-23", "ACK^R22^ACK",     "2.5"),
    LAB_26_ACK_U03                ("1.3.6.1.4.12559.11.1.1.102", "LAB-26", "ACK^U03^ACK",     "2.5"),
    LAB_1_OML_O21_OP              ("1.3.6.1.4.12559.11.1.1.106", "LAB-1",  "OML^O21^OML_O21", "2.5"),
    LAB_1_OML_O33_OP              ("1.3.6.1.4.12559.11.1.1.107", "LAB-1",  "OML^O33^OML_O33", "2.5"),
    LAB_1_OML_O35_OP              ("1.3.6.1.4.12559.11.1.1.108", "LAB-1",  "OML^O35^OML_O35", "2.5"),
    LAB_2_ORL_O34                 ("1.3.6.1.4.12559.11.1.1.109", "LAB-2",  "ORL^O34^ORL_O34", "2.5"),
    LAB_2_ORL_O36                 ("1.3.6.1.4.12559.11.1.1.110", "LAB-2",  "ORL^O36^ORL_O36", "2.5"),
    LAB_2_ORL_O22                 ("1.3.6.1.4.12559.11.1.1.111", "LAB-2",  "ORL^O22^ORL_O22", "2.5"),
    LAB_23_OUL_R22                ("1.3.6.1.4.12559.11.1.1.115", "LAB-23", "OUL^R22^OUL_R22", "2.5"),
    LAB_26_SSU_U03                ("1.3.6.1.4.12559.11.1.1.116", "LAB-26", "SSU^U03^SSU_U03", "2.5"),
    LAB_22_QBP_WOS                ("1.3.6.1.4.12559.11.1.1.117", "LAB-22", "QBP^WOS^QBP_Q11", "2.5"),
    LAB_51_MFN_M08                ("1.3.6.1.4.12559.11.1.1.118", "LAB-51", "MFN^M08^MFN_M08", "2.5"),
    LAB_51_MFN_M09                ("1.3.6.1.4.12559.11.1.1.119", "LAB-51", "MFN^M09^MFN_M09", "2.5"),
    LAB_1_ORL_O34_OF              ("1.3.6.1.4.12559.11.1.1.12",  "LAB-1",  "ORL^O34^ORL_O34", "2.5"),
    LAB_51_MFN_M10                ("1.3.6.1.4.12559.11.1.1.120", "LAB-51", "MFN^M10^MFN_M10", "2.5"),
    LAB_51_MFN_M11                ("1.3.6.1.4.12559.11.1.1.121", "LAB-51", "MFN^M11^MFN_M11", "2.5"),
    LAB_51_MFK_M08                ("1.3.6.1.4.12559.11.1.1.122", "LAB-51", "MFK^M08^MFK_M08", "2.5"),
    LAB_51_MFK_M09                ("1.3.6.1.4.12559.11.1.1.123", "LAB-51", "MFK^M09^MFK_M09", "2.5"),
    LAB_51_MFK_M10                ("1.3.6.1.4.12559.11.1.1.124", "LAB-51", "MFK^M10^MFK_M10", "2.5"),
    LAB_51_MFK_M11                ("1.3.6.1.4.12559.11.1.1.125", "LAB-51", "MFK^M11^MFK_M11", "2.5"),
    LAB_1_ORL_O36_OF              ("1.3.6.1.4.12559.11.1.1.13",  "LAB-1",  "ORL^O36^ORL_O36", "2.5"),
    LAB_32_ACK_R33                ("1.3.6.1.4.12559.11.1.1.131", "LAB-32", "ACK^R33^ACK", "2.5"),
    LAB_32_ORU_R30                ("1.3.6.1.4.12559.11.1.1.132", "LAB-32", "ORU^R30^ORU_R30", "2.5"),
    LAB_32_ORU_R31                ("1.3.6.1.4.12559.11.1.1.133", "LAB-32", "ORU^R31^ORU_R30", "2.5"),
    LAB_61_ORL_O34                ("1.3.6.1.4.12559.11.1.1.134", "LAB-61", "ORL^O34^ORL_O34", "2.5"),
    LAB_62_QBP_SLI                ("1.3.6.1.4.12559.11.1.1.135", "LAB-62", "QBP^SLI^QBP_Q11", "2.5"),
    LAB_61_OML_O33                ("1.3.6.1.4.12559.11.1.1.136", "LAB-61", "OML^O33^OML_O33", "2.5"),
    LAB_62_RSP_SLI                ("1.3.6.1.4.12559.11.1.1.137", "LAB-62", "RSP^SLI^RSP_K11", "2.5"),
    LAB_1_ORL_O22_OF              ("1.3.6.1.4.12559.11.1.1.14",  "LAB-1",  "ORL^O22^ORL_O22", "2.5"),
    LAB_2_OML_O21                 ("1.3.6.1.4.12559.11.1.1.15",  "LAB-2",  "OML^O21^OML_O21", "2.5"),
    LAB_2_OML_O33                 ("1.3.6.1.4.12559.11.1.1.16",  "LAB-2",    "OML^O33^OML_O33", "2.5"),
    LAB_2_OML_O35                 ("1.3.6.1.4.12559.11.1.1.17",  "LAB-2",    "OML^O35^OML_O35", "2.5"),
    LAB_63_OML_O33                ("1.3.6.1.4.12559.11.1.1.171", "LAB-63", "OML^O33^OML_O33", "2.5.1"),
    LAB_63_ORL_O34                ("1.3.6.1.4.12559.11.1.1.172", "LAB-63", "ORL^O34^ORL_O34", "2.5.1"),
    LAB_35_OML_O21                ("1.3.6.1.4.12559.11.1.1.173", "LAB-35", "OML^O21^OML_O21", "2.5.1"),
    LAB_36_ACK                    ("1.3.6.1.4.12559.11.1.1.174", "LAB-36", "ACK", "2.5.1"),
    LAB_35_ORL_O22                ("1.3.6.1.4.12559.11.1.1.175", "LAB-35", "ORL^O22^ORL_O22", "2.5.1"),
    LAB_36_ORU_R01                ("1.3.6.1.4.12559.11.1.1.176", "LAB-36", "ORU^R01^ORU_R01", "2.5.1"),
    LAB_27_RSP_K11                ("1.3.6.1.4.12559.11.1.1.177", "LAB-27", "RSP^K11^RSP_K11", "2.5.1"),
    LAB_28_OML_O33                ("1.3.6.1.4.12559.11.1.1.178", "LAB-28", "OML^O33^OML_O33", "2.5.1"),
    LAB_29_ACK_R22                ("1.3.6.1.4.12559.11.1.1.179", "LAB-29", "ACK^R22^ACK", "2.5.1"),
    LAB_3_OUL_R22                 ("1.3.6.1.4.12559.11.1.1.18",  "LAB-3",  "OUL^R22^OUL_R22", "2.5"),
    LAB_27_QBP_Q11                ("1.3.6.1.4.12559.11.1.1.180", "LAB-27", "QBP^Q11^QBP_Q11", "2.5.1"),
    LAB_28_ORL_O34                ("1.3.6.1.4.12559.11.1.1.181", "LAB-28", "ORL^O34^ORL_O34", "2.5.1"),
    LAB_29_OUL_R22                ("1.3.6.1.4.12559.11.1.1.182", "LAB-29", "OUL^R22^OUL_R22", "2.5.1"),
    LAB_1_OML_O21_OF              ("1.3.6.1.4.12559.11.1.1.186", "LAB-1", "OML^O21^OML_O21", "2.5"),
    LAB_1_OML_O33_OF              ("1.3.6.1.4.12559.11.1.1.187", "LAB-1", "OML^O33^OML_O33", "2.5"),
    LAB_1_OML_O35_OF              ("1.3.6.1.4.12559.11.1.1.188", "LAB-1", "OML^O35^OML_035", "2.5"),
    LAB_1_ORL_O22_OP              ("1.3.6.1.4.12559.11.1.1.189", "LAB-1", "ORL^O22^ORL_O22", "2.5"),
    LAB_3_OUL_R24                 ("1.3.6.1.4.12559.11.1.1.19",  "LAB-3", "OUL^R24^OUL_R24", "2.5"),
    LAB_1_ORL_O34_OP              ("1.3.6.1.4.12559.11.1.1.190", "LAB-1", "ORL^O34^ORL_O34", "2.5"),
    LAB_1_ORL_O36_OP              ("1.3.6.1.4.12559.11.1.1.191", "LAB-1", "ORL^O36^ORL_O36", "2.5"),
    LAB_4_OML_O33                 ("1.3.6.1.4.12559.11.1.1.2",   "LAB-4", "OML^O33^OML_O33", "2.5"),
    LAB_3_ORU_R01                 ("1.3.6.1.4.12559.11.1.1.20",  "LAB-3", "ORU^R01^ORU_R01", "2.5"),
    LAB_3_ACK                     ("1.3.6.1.4.12559.11.1.1.22",  "LAB-3", "ACK", "2.5"),
    LAB_4_OML_O35                 ("1.3.6.1.4.12559.11.1.1.3",   "LAB-4", "OML^O35^OML_O35", "2.5"),
    LAB_5_ACK                     ("1.3.6.1.4.12559.11.1.1.6",   "LAB-5",  "ACK", "2.5"),
    LAB_4_ORL_O34                 ("1.3.6.1.4.12559.11.1.1.93",  "LAB-4", "ORL^O34^ORL_O34", "2.5"),
    LAB_4_ORL_O36                 ("1.3.6.1.4.12559.11.1.1.94",  "LAB-4", "ORL^O36^ORL_O36", "2.5"),
    LAB_4_ORL_O22                 ("1.3.6.1.4.12559.11.1.1.95",  "LAB-4", "ORL^O22^ORL_O22", "2.5"),
    LAB_5_OUL_R22                 ("1.3.6.1.4.12559.11.1.1.96",  "LAB-5", "OUL^R22^OUL_R22", "2.5"),
    LAB_5_OUL_R23                 ("1.3.6.1.4.12559.11.1.1.97",  "LAB-5", "OUL^R23^OUL_R23", "2.5"),
    LAB_5_OUL_R24                 ("1.3.6.1.4.12559.11.1.1.98",  "LAB-5", "OUL^R24^OUL_R24", "2.5"),
    LAB_21_OML_O33                ("1.3.6.1.4.12559.11.1.1.99",  "LAB-21", "OML^O33^OML_O33", "2.5");


    private final String profileId;
    private final String transaction;
    private final String triggerEvent;
    private final String hl7version;

    LabProfile(String profileId, String transaction, String triggerEvent, String hl7version){
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
