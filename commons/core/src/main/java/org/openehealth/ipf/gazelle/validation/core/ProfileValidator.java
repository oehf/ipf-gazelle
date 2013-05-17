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
package org.openehealth.ipf.gazelle.validation.core;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;

/**
 * @author Boris Stanojevic
 */
public interface ProfileValidator<T> {

    /**
     * Validates the given message against the given static definition from
     * a conformance profile and additional definition from {{GazelleProfile}} enum.
     * Conformance profiles are XML representations of
     * domain-specific constraints on a message (see HL7 2.5 section 2.12).
     * @param message given HL7 Message
     * @param profile conformance profile static definition
     * @return a list of exceptions representing points of non-conformance (may not be a complete list)
     */
    public HL7Exception[] validate(Message message, T profile);

}
