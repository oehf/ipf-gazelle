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

import java.util.List;

import ca.uhn.hl7v2.validation.ValidationException;

/**
 * @author Boris Stanojevic
 */
public abstract class ProfileAssertions {

    private ProfileAssertions() {
    }

    public static void profileViolatedWhen(boolean errorCondition, List<ValidationException> exceptions,
                                           ProfileValidationMessage validationErrorMessage, Object... details) {
        if (errorCondition) {
            var he = new ValidationException(String.format(validationErrorMessage.errorMessage(), details));
            he.setError(validationErrorMessage.errorCode());
            he.setSeverity(validationErrorMessage.severity());
            exceptions.add(he);
        }
    }

    public static void profileNotHL7Compliant(List<ValidationException> exceptions, ProfileValidationMessage validationErrorMessage, Object... details) {
        var he = new ValidationException(String.format(validationErrorMessage.errorMessage(), details));
        he.setError(validationErrorMessage.errorCode());
        he.setSeverity(validationErrorMessage.severity());
        exceptions.add(he);
    }

}
