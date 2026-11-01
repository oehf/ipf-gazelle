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

import ca.uhn.hl7v2.Severity;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.gazelle.validation.profile.rad.RadProfile;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Some Gazelle profiles define fields without a Length attribute (e.g. MSA-5 in many ACK profiles,
 * which is withdrawn since HL7 v2.5). Validating a message containing such a segment must not fail.
 */
public class MissingFieldLengthTest extends AbstractGazelleProfileValidatorTest {

    // RAD_2_ACK_O19 defines MSA-5 without Length
    private static final String ACK_O19 =
            "MSH|^~\\&|OF|FAC|OP|FAC|20240101120000||ACK^O19^ACK|MSG2|P|2.5.1\r" +
            "MSA|AA|MSG1\r";

    @Test
    public void testAckWithFieldWithoutLength() throws Exception {
        var rule = new GazelleProfileRule(unmarshalProfile(RadProfile.RAD_2_ACK_O19));
        var message = hapiContext.getPipeParser().parse(ACK_O19);

        var violations = assertDoesNotThrow(() -> rule.apply(message));
        printOutExceptions(violations);
        assertEquals(0, countExceptions(violations, Severity.ERROR),
                () -> Arrays.toString(violations));
    }
}
