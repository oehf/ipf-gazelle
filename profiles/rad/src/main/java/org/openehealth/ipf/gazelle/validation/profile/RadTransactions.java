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

import java.util.Arrays;
import java.util.List;

import static org.openehealth.ipf.gazelle.validation.profile.RadProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum RadTransactions implements HL7v2Transactions {

    RAD1  (RAD_1_ADT_A01, RAD_1_ADT_A04, RAD_1_ADT_A05, RAD_1_ADT_A11, RAD_1_ADT_A38),
    RAD2  (RAD_2_ORM_O01_NW_V25, RAD_2_ORM_O01_CA_V25, RAD_2_ORM_O01_NW_V231,
           RAD_2_ORM_O01_CA_V231, RAD_2_ACK_O01_EYE, RAD_2_ACK_O19, RAD_2_OMG_O19_NW,
           RAD_2_OMG_O19_CA, RAD_2_ACK_O01_RAD),
    RAD3  (RAD_3_ACK_O19, RAD_3_OMG_O19_OC, RAD_3_OMG_O19_SC, RAD_3_OMG_O19_SN,
           RAD_3_ORG_O20, RAD_3_ORM_O01_OC_EYE, RAD_3_ORM_O01_OC_RAD, RAD_3_ORM_O01_SC_RAD,
           RAD_3_ORM_O01_SN_EYE, RAD_3_ORR_O02_V231, RAD_3_ORR_O02_V25,
           RAD_3_ORM_O01_SC_EYE, RAD_3_ORM_O01_SN_RAD),
    RAD4  (RAD_4_ORM_O01, RAD_4_ACK_O01, RAD_4_OMI_O23, RAD_4_ACK_O23),
    RAD12 (RAD_12_ADT_A02, RAD_12_ADT_A03, RAD_12_ADT_A06, RAD_12_ADT_A07,
           RAD_12_ADT_A08, RAD_12_ADT_A12, RAD_12_ADT_A13, RAD_12_ADT_A40),
    RAD13 (RAD_13_ACK_O01, RAD_13_ORM_O01, RAD_13_OMI_O23, RAD_13_ACK_O23),
    RAD28 (RAD_28_ORU_R01),
    RAD35 (RAD_35_DFT_P03),
    RAD36 (RAD_36_BAR_P01, RAD_36_BAR_P05, RAD_36_BAR_P06),
    RAD48 (RAD_48_SIU_S12, RAD_48_SIU_S13, RAD_48_SIU_S15);

    private final List<ConformanceProfile> transactionTypes;

    RadTransactions(ConformanceProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<ConformanceProfile> conformanceProfiles() {
        return transactionTypes;
    }

}
