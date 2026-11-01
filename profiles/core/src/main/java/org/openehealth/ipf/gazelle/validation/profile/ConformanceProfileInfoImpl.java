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
public record ConformanceProfileInfoImpl(String profileId, String transaction, String triggerEvent,
                                         String hl7version) implements ConformanceProfileInfo {

    public String type() {
        return component(0);
    }

    public String event() {
        return component(1);
    }

    public String structure() {
        return component(2);
    }

    // Returns the n-th '^'-separated component of the trigger event, or "" if there is none.
    // Avoids String#split, as these accessors are called for every message when guessing its profile.
    private String component(int n) {
        var start = 0;
        for (var i = 0; i < n; i++) {
            start = triggerEvent.indexOf('^', start) + 1;
            if (start == 0) return "";
        }
        var end = triggerEvent.indexOf('^', start);
        return end < 0 ? triggerEvent.substring(start) : triggerEvent.substring(start, end);
    }
}
