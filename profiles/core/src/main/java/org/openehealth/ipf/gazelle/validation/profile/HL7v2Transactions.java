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

import java.util.List;


/**
 * Holder for conformance profiles that all belong to a certain group. An example for such a group is
 * an IHE transaction, but there is no restriction to IHE at this point.
 *
 * Implementations of this interface return a dedicated set of conformance profiles that ultimately
 * carry an ID reference to the profile definition and information about the (set of) messages the
 * profile applies to. T
 *
 * There is a number of enum implementations for IHE transactions in dedicated modules
 */
public interface HL7v2Transactions {

    public List<ConformanceProfile> conformanceProfiles();

}
