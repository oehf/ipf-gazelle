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
package org.openehealth.ipf.gazelle.validation.core.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.conf.check.ProfileNotFollowedException;
import ca.uhn.hl7v2.conf.check.ProfileNotHL7CompliantException;

/**
 * @author Boris Stanojevic
 */
public abstract class ProfileAssertions {

    private ProfileAssertions() {
    }

    public static HL7Exception profileNotFollowedAssert(boolean errorCondition,
                                                        ProfileValidationMessage validationErrorMessage, Object... details) {
        if (errorCondition) {
            HL7Exception he = new ProfileNotFollowedException(String.format(validationErrorMessage.errorMessage(), details));
            he.setError(validationErrorMessage.errorCode());
            he.setSeverity(validationErrorMessage.severity());
            return he;
        }
        return null;
    }

    public static HL7Exception profileNotHL7Compliant(ProfileValidationMessage validationErrorMessage, Object... details) {
        HL7Exception he = new ProfileNotHL7CompliantException(String.format(validationErrorMessage.errorMessage(), details));
        he.setError(validationErrorMessage.errorCode());
        he.setSeverity(validationErrorMessage.severity());
        return he;
    }

}
