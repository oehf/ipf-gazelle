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

import static org.openehealth.ipf.gazelle.validation.profile.LabProfile.*;
/**
 * @author Boris Stanojevic
 */
public enum LabTransactions implements IHETransaction {

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
    LAB63 (LAB_63_OML_O33, LAB_63_ORL_O34);

    private final List<GazelleProfile> transactionTypes;

    LabTransactions(GazelleProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<GazelleProfile> transactionTypes() {
        return transactionTypes;
    }

}
