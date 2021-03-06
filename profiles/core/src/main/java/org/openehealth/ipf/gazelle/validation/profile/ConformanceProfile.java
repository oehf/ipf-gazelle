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
 * Interface for ConformanceProfile enumerations. The meta data is extracted into a
 * {@link org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo} instance
 * in order to reduce code deduplication.
 */
public interface ConformanceProfile {

    ConformanceProfileInfo profileInfo();

}
