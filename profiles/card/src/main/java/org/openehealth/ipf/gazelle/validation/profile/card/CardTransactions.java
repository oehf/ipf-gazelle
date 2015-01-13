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
package org.openehealth.ipf.gazelle.validation.profile.card;

import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;

import java.util.Arrays;
import java.util.List;

/**
 * @author Boris Stanojevic
 */
public enum CardTransactions implements HL7v2Transactions {

    CARD7 (CardProfile.CARD_7_MDM_T02_RC, CardProfile.CARD_7_MDM_T10_RC, CardProfile.CARD_7_MDM_T02_RM, CardProfile.CARD_7_MDM_T10_RM, CardProfile.CARD_7_ACK_ALL),
    CARD8 (CardProfile.CARD_8_MDM_T01, CardProfile.CARD_8_MDM_T09, CardProfile.CARD_8_ACK_T01, CardProfile.CARD_8_ACK_T09);

    private final List<ConformanceProfile> transactionTypes;

    CardTransactions(ConformanceProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<ConformanceProfile> conformanceProfiles() {
        return transactionTypes;
    }

}
