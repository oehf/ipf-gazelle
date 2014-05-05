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

import static org.openehealth.ipf.gazelle.validation.profile.PatProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum PatTransactions implements IHETransaction {


    PAT10(PAT_10_ORU_R01);

    private final List<GazelleProfile> transactionTypes;

    PatTransactions(GazelleProfile... transactionTypes) {
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<GazelleProfile> transactionTypes() {
        return transactionTypes;
    }

}
