/*
 * Copyright 2026 the original author or authors.
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

import ca.uhn.hl7v2.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.gazelle.validation.core.util.ProfileValidationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileNotHL7Compliant;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileValidationMessage.*;

public class ProfileValidationMessageTest {

    @Test
    public void testComponentIndexIsFormattedCorrectly() {
        assertEquals("Component 'XPN'[3] is defined in the profile but does not exist in the Java class representing the segment",
                format(COMPONENT_TYPE_MISMATCH, "XPN", 3));
    }

    @Test
    public void testShortArgumentsAreNotPadded() {
        assertEquals("PID must have at least 1 repetitions (has 0)",
                format(LESS_THAN_MINIMUM_CARDINALITY, "PID", 1, 0));
    }

    @Test
    public void testAllMessagesUsePlainOrPositionalSpecifiers() {
        // %1s, %2s... are width specifiers, not positional ones
        var widthSpecifier = Pattern.compile("%\\d+s");
        for (var message : ProfileValidationMessage.values()) {
            assertFalse(widthSpecifier.matcher(message.errorMessage()).find(), message.name());
        }
    }

    private static String format(ProfileValidationMessage message, Object... details) {
        List<ValidationException> exceptions = new ArrayList<>();
        profileNotHL7Compliant(exceptions, message, details);
        return exceptions.get(0).getMessage();
    }
}
