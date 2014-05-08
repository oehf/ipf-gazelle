/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openehealth.ipf.gazelle.validation.profile;

/**
 * Default implementation of a ConformanceProfileInfo
 */
public class ConformanceProfileInfoImpl implements ConformanceProfileInfo {

    private final String profileId;
    private final String transaction;
    private final String triggerEvent;
    private final String hl7version;

    ConformanceProfileInfoImpl(String profileId, String transaction, String triggerEvent, String hl7version) {
        this.profileId = profileId;
        this.triggerEvent = triggerEvent;
        this.transaction = transaction;
        this.hl7version = hl7version;
    }

    public String profileId() {
        return profileId;
    }

    public String transaction() {
        return transaction;
    }

    public String triggerEvent() {
        return triggerEvent;
    }

    public String type() {
        return triggerEvent.split("\\^")[0];
    }

    public String event() {
        return triggerEvent.split("\\^").length > 1 ? triggerEvent.split("\\^")[1] : "";
    }

    public String structure() {
        return triggerEvent.split("\\^").length > 2 ? triggerEvent.split("\\^")[2] : "";
    }

    public String hl7version() {
        return hl7version;
    }
}
