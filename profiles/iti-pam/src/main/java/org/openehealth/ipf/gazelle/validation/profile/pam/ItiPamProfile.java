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
package org.openehealth.ipf.gazelle.validation.profile.pam;

import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;

/**
 * @author Boris Stanojevic
 */
public enum ItiPamProfile implements ConformanceProfile {


    ITI_30_ACK                    ("1.3.6.1.4.12559.11.1.1.33",  "ITI-30", "ACK"),
    ITI_30_ADT_A28                ("1.3.6.1.4.12559.11.1.1.36",  "ITI-30", "ADT^A28^ADT_A05"),
    ITI_30_ADT_A37                ("1.3.6.1.4.12559.11.1.1.37",  "ITI-30", "ADT^A37^ADT_A37"),
    ITI_30_ADT_A47                ("1.3.6.1.4.12559.11.1.1.38",  "ITI-30", "ADT^A47^ADT_A30"),
    ITI_30_ADT_A31                ("1.3.6.1.4.12559.11.1.1.39",  "ITI-30", "ADT^A31^ADT_A05"),
    ITI_30_ADT_A40                ("1.3.6.1.4.12559.11.1.1.40",  "ITI-30", "ADT^A40^ADT_A39"),
    ITI_30_ADT_A24                ("1.3.6.1.4.12559.11.1.1.41",  "ITI-30", "ADT^A24^ADT_A24"),
    ITI_31_ACK                    ("1.3.6.1.4.12559.11.1.1.44",  "ITI-31", "ACK"),
    ITI_31_ADT_A16                ("1.3.6.1.4.12559.11.1.1.45",  "ITI-31", "ADT^A16^ADT_A16"),
    ITI_31_ADT_A07                ("1.3.6.1.4.12559.11.1.1.46",  "ITI-31", "ADT^A07^ADT_A06"),
    ITI_31_ADT_A25                ("1.3.6.1.4.12559.11.1.1.47",  "ITI-31", "ADT^A25^ADT_A21"),
    ITI_31_ADT_A52                ("1.3.6.1.4.12559.11.1.1.48",  "ITI-31", "ADT^A52^ADT_A52"),
    ITI_31_ADT_A08                ("1.3.6.1.4.12559.11.1.1.49",  "ITI-31", "ADT^A08^ADT_A01"),
    ITI_31_ADT_A26                ("1.3.6.1.4.12559.11.1.1.50",  "ITI-31", "ADT^A26^ADT_A21"),
    ITI_31_ADT_A44                ("1.3.6.1.4.12559.11.1.1.51",  "ITI-31", "ADT^A44^ADT_A43"),
    ITI_31_ADT_A53                ("1.3.6.1.4.12559.11.1.1.52",  "ITI-31", "ADT^A53^ADT_A52"),
    ITI_31_ADT_A09                ("1.3.6.1.4.12559.11.1.1.53",  "ITI-31", "ADT^A09^ADT_A09"),
    ITI_31_ADT_A27                ("1.3.6.1.4.12559.11.1.1.54",  "ITI-31", "ADT^A27^ADT_A21"),
    ITI_31_ADT_A54                ("1.3.6.1.4.12559.11.1.1.55",  "ITI-31", "ADT^A54^ADT_A54"),
    ITI_31_ADT_A55                ("1.3.6.1.4.12559.11.1.1.56",  "ITI-31", "ADT^A55^ADT_A52"),
    ITI_31_ADT_A38                ("1.3.6.1.4.12559.11.1.1.57",  "ITI-31", "ADT^A38^ADT_A38"),
    ITI_31_ADT_Z99                ("1.3.6.1.4.12559.11.1.1.58",  "ITI-31", "ADT^Z99^ADT_A01"),
    ITI_31_ADT_A10                ("1.3.6.1.4.12559.11.1.1.59",  "ITI-31", "ADT^A10^ADT_A09"),
    ITI_31_ADT_A01                ("1.3.6.1.4.12559.11.1.1.60",  "ITI-31", "ADT^A01^ADT_A01"),
    ITI_31_ADT_A11                ("1.3.6.1.4.12559.11.1.1.61",  "ITI-31", "ADT^A11^ADT_A09"),
    ITI_31_ADT_A02                ("1.3.6.1.4.12559.11.1.1.62",  "ITI-31", "ADT^A02^ADT_A02"),
    ITI_31_ADT_A12                ("1.3.6.1.4.12559.11.1.1.63",  "ITI-31", "ADT^A12^ADT_A12"),
    ITI_31_ADT_A03                ("1.3.6.1.4.12559.11.1.1.64",  "ITI-31", "ADT^A03^ADT_A03"),
    ITI_31_ADT_A21                ("1.3.6.1.4.12559.11.1.1.65",  "ITI-31", "ADT^A21^ADT_A21"),
    ITI_31_ADT_A13                ("1.3.6.1.4.12559.11.1.1.66",  "ITI-31", "ADT^A13^ADT_A01"),
    ITI_31_ADT_A04                ("1.3.6.1.4.12559.11.1.1.67",  "ITI-31", "ADT^A04^ADT_A01"),
    ITI_31_ADT_A22                ("1.3.6.1.4.12559.11.1.1.68",  "ITI-31", "ADT^A22^ADT_A21"),
    ITI_31_ADT_A40                ("1.3.6.1.4.12559.11.1.1.69",  "ITI-31", "ADT^A40^ADT_A39"),
    ITI_31_ADT_A14                ("1.3.6.1.4.12559.11.1.1.70",  "ITI-31", "ADT^A14^ADT_A05"),
    ITI_31_ADT_A05                ("1.3.6.1.4.12559.11.1.1.71",  "ITI-31", "ADT^A05^ADT_A05"),
    ITI_31_ADT_A32                ("1.3.6.1.4.12559.11.1.1.72",  "ITI-31", "ADT^A32^ADT_A21"),
    ITI_31_ADT_A15                ("1.3.6.1.4.12559.11.1.1.73",  "ITI-31", "ADT^A15^ADT_A15"),
    ITI_31_ADT_A06                ("1.3.6.1.4.12559.11.1.1.74",  "ITI-31", "ADT^A06^ADT_A06"),
    ITI_31_ADT_A33                ("1.3.6.1.4.12559.11.1.1.75",  "ITI-31", "ADT^A33^ADT_A21");

    private final ConformanceProfileInfo info;

    ItiPamProfile(String profileId, String transaction, String triggerEvent){
        info = new ConformanceProfileInfoImpl(profileId, transaction, triggerEvent, "2.5");
    }

    @Override
    public ConformanceProfileInfo profileInfo() {
        return info;
    }
}
