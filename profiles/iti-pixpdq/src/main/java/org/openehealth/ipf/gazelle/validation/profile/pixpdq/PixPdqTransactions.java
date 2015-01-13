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
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;

import java.util.Arrays;
import java.util.List;

import static org.openehealth.ipf.gazelle.validation.profile.pixpdq.ItiPixPdqProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum PixPdqTransactions implements HL7v2Transactions {

    ITI8  (ITI_8_ACK_A01, ITI_8_ACK_A04, ITI_8_ACK_A05, ITI_8_ACK_A08, ITI_8_ACK_A40,
           ITI_8_ADT_A08, ITI_8_ADT_A01, ITI_8_ADT_A04, ITI_8_ADT_A40, ITI_8_ADT_A05, ITI_8_ACK),
    ITI9  (ITI_9_RSP_K23, ITI_9_QBP_Q23),
    ITI10 (ITI_10_ADT_A31, ITI_10_ACK),
    ITI21 (ITI_21_ACK_J01, ITI_21_QBP_Q22, ITI_21_QCN_J01, ITI_21_RSP_K22),
    ITI22 (ITI_22_ACK_J01, ITI_22_QBP_ZV1, ITI_22_RSP_ZV2, ITI_22_QCN_J01),
    ITI64 (ITI_64_ADT_A43, ITI_64_ACK_A43);

    private final List<ConformanceProfile> transactionTypes;

    PixPdqTransactions(ConformanceProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<ConformanceProfile> conformanceProfiles() {
        return transactionTypes;
    }

}
