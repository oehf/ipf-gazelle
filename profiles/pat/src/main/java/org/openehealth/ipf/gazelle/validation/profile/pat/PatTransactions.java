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
package org.openehealth.ipf.gazelle.validation.profile.pat;

import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;

import java.util.Arrays;
import java.util.List;

import static org.openehealth.ipf.gazelle.validation.profile.pat.PatProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum PatTransactions implements HL7v2Transactions {


    PAT10(PAT_10_ORU_R01);

    private final List<ConformanceProfile> transactionTypes;

    PatTransactions(ConformanceProfile... transactionTypes) {
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<ConformanceProfile> conformanceProfiles() {
        return transactionTypes;
    }

}
