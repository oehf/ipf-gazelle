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
import ca.uhn.hl7v2.conf.ProfileException;
import ca.uhn.hl7v2.conf.check.Validator;
import ca.uhn.hl7v2.conf.spec.message.StaticDef;
import ca.uhn.hl7v2.model.Message;

/**
 * @author Boris Stanojevic
 */
public interface ProfileValidator extends Validator {

    /**
     * Validates the given message against the given static definition from
     * a conformance profile and additional definition from {{GazelleProfile}} enum.
     * Conformance profiles are XML representations of
     * domain-specific constraints on a message (see HL7 2.5 section 2.12).
     * @param message given HL7 Message
     * @param profile static profile definition
     * @param gazelleProfile GazelleProfile enum
     * @throws ProfileException if a problem is encountered that interferes with evaluation
     * @throws HL7Exception if a HL7 message handling problem is encountered
     * @return a list of exceptions representing points of non-conformance (may not be a complete list)
     */
    public HL7Exception[] validate(Message message, StaticDef profile, GazelleProfile gazelleProfile)
            throws ProfileException, HL7Exception;

}
