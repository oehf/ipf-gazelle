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
package org.openehealth.ipf.gazelle.validation.profile.pcd;

import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;

/**
 * @author Boris Stanojevic
 */
public enum PcdProfile implements ConformanceProfile {

    PCD_1_ACK_R01_DEV_OBS_CONSUMER("1.3.6.1.4.12559.11.1.1.113", "PCD-1",  "ACK^R01^ACK",     "2.6"),
    PCD_2_QSB_Z02                 ("1.3.6.1.4.12559.11.1.1.114", "PCD-2",  "QSB^Z02^QSB_Q16", "2.5"),
    PCD_1_ACK_R01_DEV_OBS_FILTER  ("1.3.6.1.4.12559.11.1.1.126", "PCD-1",  "ACK^R01^ACK", "2.6"),
    PCD_1_ORU_R01_DEV_OBS_FILTER  ("1.3.6.1.4.12559.11.1.1.127", "PCD-1",  "ORU^R01^ORU_R01", "2.6"),
    PCD_2_ACK_Z02                 ("1.3.6.1.4.12559.11.1.1.128", "PCD-2",  "ACK^Z02^ACK", "2.5"),
    PCD_1_ORU_R01_DEV_OBS_REPORTER("1.3.6.1.4.12559.11.1.1.129", "PCD-1",  "ORU^R01^ORU_R01", "2.6");


    private final ConformanceProfileInfo info;

    PcdProfile(String profileId, String transaction, String triggerEvent, String hl7version){
        info = new ConformanceProfileInfoImpl(profileId, transaction, triggerEvent, hl7version);
    }

    @Override
    public ConformanceProfileInfo profileInfo() {
        return info;
    }
}
