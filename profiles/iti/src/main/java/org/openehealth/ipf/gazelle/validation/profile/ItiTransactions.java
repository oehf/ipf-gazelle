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

import static org.openehealth.ipf.gazelle.validation.profile.ItiProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum ItiTransactions implements IHETransaction {

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
    ITI64 (ITI_64_ADT_A43, ITI_64_ACK_A43);

    private final List<GazelleProfile> transactionTypes;

    ItiTransactions(GazelleProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<GazelleProfile> transactionTypes() {
        return transactionTypes;
    }

}
