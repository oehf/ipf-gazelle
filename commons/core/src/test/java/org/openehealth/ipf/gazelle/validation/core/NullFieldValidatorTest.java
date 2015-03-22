/*
 * Copyright 2015 the original author or authors.
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
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.ValidationException;
import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;

import java.io.InputStream;

/**
 * @author Dmytro Rud
 */
public class NullFieldValidatorTest extends AbstractGazelleProfileValidatorTest {

    private static final String NULL = "\"\"";

    private Message getMessage(String substanceId) throws Exception {
        final String messageString =
                "MSH|^~\\&|sa|sf|ra|rf|20150321213310+0200||OUL^R22^OUL_R22|123|P|2.5.1|||ER|AL||UNICODE UTF-8|||LAB-29^IHE\r" +
                "INV|" + substanceId + "|OK\r";
        return new PipeParser().parse(messageString);
    }

    private GazelleProfileRule getRule(String fileName) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("profiles/" + fileName + ".xml");
        HL7V2XConformanceProfile conformanceProfile = (HL7V2XConformanceProfile) unmarshaller.unmarshal(stream);
        return new GazelleProfileRule(conformanceProfile);
    }

    private ValidationException[] validate(String substanceId, String ruleFileName, boolean shallFail) throws Exception {
        Message message = getMessage(substanceId);
        GazelleProfileRule rule = getRule(ruleFileName);
        ValidationException[] exceptions = rule.apply(message);
        assertEquals(shallFail ? 1 : 0, countExceptions(exceptions, Severity.ERROR));
        return exceptions;
    }

    @Test
    public void testOriginalProfile() throws Exception {
        validate("",                       "original", true);  // field is empty
        validate("id",                     "original", true);  // CE.3 is not set
        validate("\"\"",                   "original", true);  // CE.1 is NULL, CE.3 is not set -- SHALL NOT FAIL !!!
        validate("\"\"^^HL70451",          "original", false); // CE.1 is NULL, CE.3 is set -- SHALL FAIL !!!
        validate("id^^HL70451",            "original", false); // CE.1 and CE.3 are set
        validate("id^description^HL70451", "original", false); // CE.1, CE.2, CE.3 are all set
    }

    @Test
    public void testProfileWithNulls() throws Exception {
        validate("",                       "withNulls", true);  // field is empty
        validate("id",                     "withNulls", true);  // CE.3 is not set
        validate("\"\"",                   "withNulls", false); // CE.1 is NULL, CE.3 is not set

        ValidationException[] exceptions = validate("\"\"^^HL70451", "withNulls", true); // CE.1 is NULL, CE.3 is set
        assertEquals("Element 'name of coding system' cannot be present when the field is set to NULL at INV-1",
                exceptions[0].getMessage());

        validate("id^^HL70451",            "withNulls", false); // CE.1 and CE.3 are set
        validate("id^description^HL70451", "withNulls", false); // CE.1, CE.2, CE.3 are all set
    }
}
