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
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;

/**
 * @author Boris Stanojevic
 */
public enum CardProfile implements ConformanceProfile {

    CARD_7_MDM_T02_RC             ("1.3.6.1.4.12559.11.1.1.138", "CARD-7", "MDM^T02^MDM_T02", "2.5"),
    CARD_7_MDM_T10_RC             ("1.3.6.1.4.12559.11.1.1.139", "CARD-7", "MDM^T10^MDM_T02", "2.5"),
    CARD_7_MDM_T02_RM             ("1.3.6.1.4.12559.11.1.1.140", "CARD-7", "MDM^T02^MDM_T02", "2.5"),
    CARD_7_MDM_T10_RM             ("1.3.6.1.4.12559.11.1.1.141", "CARD-7", "MDM^T10^MDM_T02", "2.5"),
    CARD_8_MDM_T01                ("1.3.6.1.4.12559.11.1.1.142", "CARD-8", "MDM^T01^MDM_T01", "2.5"),
    CARD_8_MDM_T09                ("1.3.6.1.4.12559.11.1.1.143", "CARD-8", "MDM^T09^MDM_T01", "2.5"),
    CARD_8_ACK_T01                ("1.3.6.1.4.12559.11.1.1.144", "CARD-8", "ACK^T01^ACK", "2.5"),
    CARD_8_ACK_T09                ("1.3.6.1.4.12559.11.1.1.145", "CARD-8", "ACK^T09^ACK", "2.5"),
    CARD_7_ACK_ALL                ("1.3.6.1.4.12559.11.1.1.146", "CARD-7", "ACK^ALL^ACK", "2.5");

    private final ConformanceProfileInfo info;

    CardProfile(String profileId, String transaction, String triggerEvent, String hl7version){
        info = new ConformanceProfileInfoImpl("card/" + profileId, transaction, triggerEvent, hl7version);
    }

    @Override
    public ConformanceProfileInfo profileInfo() {
        return info;
    }}
