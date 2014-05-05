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
import ca.uhn.hl7v2.Severity;
import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ItiProfile;

import static junit.framework.Assert.assertEquals;

/**
 * @author Boris Stanojevic
 */
public class GazelleProfileValidatorTest extends AbstractGazelleProfileValidatorTest{

    @Test
    public void testIti8() throws Exception {
        HL7V2XConformanceProfile runtimeProfile = unmarshalProfile(ItiProfile.ITI_8_ADT_A40.profileId());
        GazelleProfileValidator validator = new GazelleProfileValidator(hapiContext);

        HL7Exception[] exceptions = validator.validate(getParsedMessage("hl7/iti-8.hl7"), runtimeProfile);
        printOutExceptions(exceptions);

        assertEquals(1, countExceptions(exceptions,  Severity.ERROR));
        assertEquals(3, countExceptions(exceptions,  Severity.WARNING));
        assertEquals(1, countExceptions(exceptions,  Severity.INFO));
    }

    @Test
    public void testIti21() throws Exception {
        HL7V2XConformanceProfile runtimeProfile = unmarshalProfile(ItiProfile.ITI_21_QBP_Q22.profileId());
        GazelleProfileValidator validator = new GazelleProfileValidator(hapiContext);

        HL7Exception[] exceptions = validator.validate(getParsedMessage("hl7/iti-21.hl7"), runtimeProfile);
        printOutExceptions(exceptions);

        assertEquals(0, countExceptions(exceptions,  Severity.ERROR));
        assertEquals(1, countExceptions(exceptions,  Severity.WARNING));
        assertEquals(2, countExceptions(exceptions,  Severity.INFO));
    }

    @Test
    public void testIti10() throws Exception {
        HL7V2XConformanceProfile runtimeProfile = unmarshalProfile(ItiProfile.ITI_10_ADT_A31.profileId());
        GazelleProfileValidator validator = new GazelleProfileValidator(hapiContext);

        HL7Exception[] exceptions = validator.validate(getParsedMessage("hl7/iti-10.hl7"), runtimeProfile);
        printOutExceptions(exceptions);

        assertEquals(1, countExceptions(exceptions,  Severity.ERROR));
        assertEquals(1, countExceptions(exceptions,  Severity.WARNING));
        assertEquals(0, countExceptions(exceptions,  Severity.INFO));
    }

}
