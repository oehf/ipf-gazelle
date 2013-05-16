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
package org.openehealth.ipf.gazelle.validation.core;

import java.util.Arrays;
import java.util.List;

import static org.openehealth.ipf.gazelle.validation.core.GazelleProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum IHETransaction {

    CARD7 (CARD_7_MDM_T02_RC, CARD_7_MDM_T10_RC, CARD_7_MDM_T02_RM, CARD_7_MDM_T10_RM, CARD_7_ACK_ALL),
    CARD8 (CARD_8_MDM_T01, CARD_8_MDM_T09, CARD_8_ACK_T01, CARD_8_ACK_T09),

    ITI8  (ITI_8_ACK_A01, ITI_8_ACK_A04, ITI_8_ACK_A05, ITI_8_ACK_A08, ITI_8_ACK_A40,
           ITI_8_ADT_A08, ITI_8_ADT_A01, ITI_8_ADT_A04, ITI_8_ADT_A40, ITI_8_ADT_A05, ITI_8_ACK),
    ITI9  (ITI_9_RSP_K23, ITI_9_QBP_Q23),
    ITI10 (ITI_10_ADT_A31),
    ITI21 (ITI_21_ACK_J01, ITI_21_QBP_Q22, ITI_21_QCN_J01, ITI_21_RSP_K22),
    ITI22 (ITI_22_ACK_J01, ITI_22_QBP_ZV1, ITI_22_RSP_ZV2, ITI_22_QCN_J01),
    ITI30 (ITI_30_ACK, ITI_30_ADT_A24, ITI_30_ADT_A28, ITI_30_ADT_A37,
           ITI_30_ADT_A31, ITI_30_ADT_A40, ITI_30_ADT_A47),
    ITI31 (ITI_31_ACK, ITI_31_ADT_A16, ITI_31_ADT_A07, ITI_31_ADT_A25, ITI_31_ADT_A52, ITI_31_ADT_A08,
           ITI_31_ADT_A26, ITI_31_ADT_A44, ITI_31_ADT_A53, ITI_31_ADT_A09, ITI_31_ADT_A27, ITI_31_ADT_A54,
           ITI_31_ADT_A55, ITI_31_ADT_A38, ITI_31_ADT_Z99, ITI_31_ADT_A10, ITI_31_ADT_A01,
           ITI_31_ADT_A11, ITI_31_ADT_A02, ITI_31_ADT_A12, ITI_31_ADT_A03, ITI_31_ADT_A21,
           ITI_31_ADT_A13, ITI_31_ADT_A04, ITI_31_ADT_A22, ITI_31_ADT_A40, ITI_31_ADT_A14,
           ITI_31_ADT_A05, ITI_31_ADT_A32, ITI_31_ADT_A15, ITI_31_ADT_A06, ITI_31_ADT_A33),
    ITI64 (ITI_64_ADT_A43, ITI_64_ACK_A43),

    LAB1  (LAB_1_OML_O21_OP, LAB_1_OML_O33_OP, LAB_1_OML_O35_OP, LAB_1_ORL_O34_OF, LAB_1_ORL_O36_OF,
           LAB_1_ORL_O22_OF, LAB_1_OML_O21_OF, LAB_1_OML_O33_OF, LAB_1_OML_O35_OF, LAB_1_ORL_O22_OP,
           LAB_1_ORL_O34_OP, LAB_1_ORL_O36_OP),
    LAB2  (LAB_2_ORL_O34, LAB_2_ORL_O36, LAB_2_ORL_O22, LAB_2_OML_O21, LAB_2_OML_O33, LAB_2_OML_O35),
    LAB3  (LAB_3_OUL_R22, LAB_3_OUL_R24, LAB_3_ORU_R01, LAB_3_ACK),
    LAB4  (LAB_4_OML_O21, LAB_4_OML_O33, LAB_4_OML_O35, LAB_4_ORL_O34, LAB_4_ORL_O36, LAB_4_ORL_O22),
    LAB5  (LAB_5_ACK, LAB_5_OUL_R22, LAB_5_OUL_R23, LAB_5_OUL_R24),
    LAB21 (LAB_21_OML_O33),
    LAB22 (LAB_22_RSP_WOS, LAB_22_QBP_WOS),
    LAB23 (LAB_23_ACK_R22, LAB_23_OUL_R22),
    LAB26 (LAB_26_ACK_U03, LAB_26_SSU_U03),
    LAB27 (LAB_27_RSP_K11, LAB_27_QBP_Q11),
    LAB28 (LAB_28_OML_O33, LAB_28_ORL_O34),
    LAB29 (LAB_29_ACK_R22, LAB_29_OUL_R22),
    LAB32 (LAB_32_ACK_R33, LAB_32_ORU_R30, LAB_32_ORU_R31),
    LAB35 (LAB_35_OML_O21, LAB_35_ORL_O22),
    LAB36 (LAB_36_ORU_R01),
    LAB51 (LAB_51_MFN_M08, LAB_51_MFN_M09, LAB_51_MFN_M10, LAB_51_MFN_M11, LAB_51_MFK_M08,
           LAB_51_MFK_M09, LAB_51_MFK_M10, LAB_51_MFK_M11),
    LAB61 (LAB_61_ORL_O34, LAB_61_OML_O33),
    LAB62 (LAB_62_QBP_SLI, LAB_62_RSP_SLI),
    LAB63 (LAB_63_OML_O33, LAB_63_ORL_O34),

    PAT10 (PAT_10_ORU_R01),

    PCD1  (PCD_1_ACK_R01_DEV_OBS_CONSUMER, PCD_1_ACK_R01_DEV_OBS_FILTER,
           PCD_1_ORU_R01_DEV_OBS_FILTER, PCD_1_ORU_R01_DEV_OBS_REPORTER),
    PCD2  (PCD_2_QSB_Z02, PCD_2_ACK_Z02),

    PHARMH1 (PHARM_H1_ORP_O10, PHARM_H1_OMP_O09),
    PHARMH2 (PHARM_H2_RDE_O11, PHARM_H2_RRE_O12_PRES_PLACER, PHARM_H2_RRE_O12_MED_DIS),
    PHARMH3 (PHARM_H3_RRG_O16_PRES_PLACER, PHARM_H3_RGV_O15, PHARM_H3_RRG_O16_MED_ADM_INFO, PHARM_H3_RRG_O16),
    PHARMH4 (PHARM_H4_RAS_O17, PHARM_H4_RRA_O18_PRES_PLACER, PHARM_H4_RRA_O18_MED_DIS),
    PHARMH5 (PHARM_H5_ORP_O10_MED_ADM_INFO, PHARM_H5_OMP_O09, PHARM_H5_ORP_O10_MED_DIS),
    PHARMH6 (PHARM_H6_RRE_O12, PHARM_H6_RDE_O11),

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

    private final List<GazelleProfile> transactionTypes;

    IHETransaction(GazelleProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<GazelleProfile> transactionTypes() {
        return transactionTypes;
    }

}
