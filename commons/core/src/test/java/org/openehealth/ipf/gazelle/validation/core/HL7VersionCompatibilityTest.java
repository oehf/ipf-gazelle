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

import ca.uhn.hl7v2.ErrorCode;
import ca.uhn.hl7v2.Severity;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;
import org.openehealth.ipf.gazelle.validation.profile.lab.LabProfile;
import org.openehealth.ipf.gazelle.validation.profile.lab.LabTransactions;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.guessGazelleProfile;
import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.isCompatibleVersion;

/**
 * Tests that HL7 versions which are revisions of each other (e.g. 2.5 and 2.5.1) are accepted interchangeably,
 * because some Gazelle profiles state 2.5 where the IHE technical framework requires 2.5.1.
 */
public class HL7VersionCompatibilityTest extends AbstractGazelleProfileValidatorTest {

    private static final String OML_O33 =
            "MSH|^~\\&|LIP|FAC|LB|FAC|20240101120000||OML^O33^OML_O33|MSG1|P|%s\r" +
            "PID|||123^^^HOSP^PI||Doe^John\r" +
            "SPM|1|SP1\r" +
            "ORC|NW|PO1\r";

    @AfterEach
    public void tearDown() {
        CachingGazelleProfileRule.clearCache();
    }

    @Test
    public void testIsCompatibleVersion() {
        assertTrue(isCompatibleVersion("2.5", "2.5"));
        assertTrue(isCompatibleVersion("2.5", "2.5.1"));
        assertTrue(isCompatibleVersion("2.5.1", "2.5"));
        assertFalse(isCompatibleVersion("2.5.1", "2.5.2"));
        assertFalse(isCompatibleVersion("2.3.1", "2.5.1"));
        assertFalse(isCompatibleVersion("2.5", "2.51"));
        assertFalse(isCompatibleVersion("2.4", "2.5"));
        assertFalse(isCompatibleVersion(null, "2.5"));
        assertFalse(isCompatibleVersion("2.5", null));
    }

    @Test
    public void testGuessPrefersExactVersion() throws Exception {
        var v25 = profile("2.5");
        var v251 = profile("2.5.1");
        // order must not matter: the exact match wins over the compatible one
        assertSame(v251, guessGazelleProfile(transaction(v25, v251), message("2.5.1")));
        assertSame(v251, guessGazelleProfile(transaction(v251, v25), message("2.5.1")));
        assertSame(v25, guessGazelleProfile(transaction(v251, v25), message("2.5")));
    }

    @Test
    public void testGuessFallsBackToCompatibleVersion() throws Exception {
        var v25 = profile("2.5");
        assertSame(v25, guessGazelleProfile(transaction(v25), message("2.5.1")));
        assertNull(guessGazelleProfile(transaction(profile("2.3.1")), message("2.5.1")));
    }

    @Test
    public void testLab61MessageWithIheVersion() throws Exception {
        // LAB_61_OML_O33 declares 2.5.1 (PaLM TF), its Gazelle profile says 2.5
        var message = message("2.5.1");
        assertSame(LabProfile.LAB_61_OML_O33, guessGazelleProfile(LabTransactions.LAB61, message));

        var violations = new CachingGazelleProfileRule(LabTransactions.LAB61).apply(message);
        assertEquals(0, versionViolations(violations, Severity.ERROR));
        assertEquals(1, versionViolations(violations, Severity.WARNING));
    }

    @Test
    public void testLab61MessageWithGazelleVersion() throws Exception {
        var message = message("2.5");
        assertSame(LabProfile.LAB_61_OML_O33, guessGazelleProfile(LabTransactions.LAB61, message));

        var violations = new CachingGazelleProfileRule(LabTransactions.LAB61).apply(message);
        assertEquals(0, versionViolations(violations, Severity.ERROR));
        assertEquals(0, versionViolations(violations, Severity.WARNING));
    }

    @Test
    public void testIncompatibleVersionIsStillAnError() throws Exception {
        // force validation against the profile, as guessing would (correctly) not find a profile for 2.3.1
        var violations = new CachingGazelleProfileRule(LabProfile.LAB_61_OML_O33).apply(message("2.3.1"));
        assertEquals(1, versionViolations(violations, Severity.ERROR));
    }

    private Message message(String version) throws Exception {
        return hapiContext.getPipeParser().parse(String.format(OML_O33, version));
    }

    private static long versionViolations(ValidationException[] violations, Severity severity) {
        return Arrays.stream(violations)
                .filter(v -> v.getError() == ErrorCode.UNSUPPORTED_VERSION_ID)
                .filter(v -> v.getSeverity() == severity)
                .count();
    }

    private static ConformanceProfile profile(String version) {
        var info = new ConformanceProfileInfoImpl("id-" + version, "LAB-61", "OML^O33^OML_O33", version);
        return () -> info;
    }

    private static HL7v2Transactions transaction(ConformanceProfile... profiles) {
        return () -> List.of(profiles);
    }
}
