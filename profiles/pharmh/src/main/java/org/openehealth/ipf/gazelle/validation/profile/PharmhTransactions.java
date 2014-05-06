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

import static org.openehealth.ipf.gazelle.validation.profile.PharmhProfile.*;

/**
 * @author Boris Stanojevic
 */
public enum PharmhTransactions implements HL7v2InteractionId {


    PHARMH1 (PHARM_H1_ORP_O10, PHARM_H1_OMP_O09),
    PHARMH2 (PHARM_H2_RDE_O11, PHARM_H2_RRE_O12_PRES_PLACER, PHARM_H2_RRE_O12_MED_DIS),
    PHARMH3 (PHARM_H3_RRG_O16_PRES_PLACER, PHARM_H3_RGV_O15, PHARM_H3_RRG_O16_MED_ADM_INFO, PHARM_H3_RRG_O16),
    PHARMH4 (PHARM_H4_RAS_O17, PHARM_H4_RRA_O18_PRES_PLACER, PHARM_H4_RRA_O18_MED_DIS),
    PHARMH5 (PHARM_H5_ORP_O10_MED_ADM_INFO, PHARM_H5_OMP_O09, PHARM_H5_ORP_O10_MED_DIS),
    PHARMH6 (PHARM_H6_RRE_O12, PHARM_H6_RDE_O11);

    private final List<ConformanceProfile> transactionTypes;

    PharmhTransactions(ConformanceProfile... transactionTypes){
        this.transactionTypes = Arrays.asList(transactionTypes);
    }

    public List<ConformanceProfile> conformanceProfiles() {
        return transactionTypes;
    }

}
