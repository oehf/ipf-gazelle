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


import java.util.List;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.core.util.MessageUtils;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2InteractionId;
import org.openehealth.ipf.gazelle.validation.profile.ItiPixPdqProfile;
import org.openehealth.ipf.gazelle.validation.profile.PixPdqTransactions;

/**
 * @author Boris Stanojevic
 */
public class HL7v2IHETransactionTest extends AbstractGazelleProfileValidatorTest {

    @Test
    public void testIHETransaction(){
        HL7v2InteractionId iheTransaction = PixPdqTransactions.ITI8;
        List<ConformanceProfile> profiles = iheTransaction.conformanceProfiles();

        assert profiles.size() == 11;
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ACK);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ADT_A01);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ADT_A04);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ADT_A05);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ADT_A08);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ADT_A40);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ACK_A01);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ACK_A04);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ACK_A05);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ACK_A08);
        assert profiles.contains(ItiPixPdqProfile.ITI_8_ACK_A40);
    }

    @Test
    public void testGuessTransactionProfile() throws HL7Exception {
        Message message = getParsedMessage("hl7/iti-8.hl7");
        ConformanceProfile profile = MessageUtils.guessGazelleProfile(PixPdqTransactions.ITI8, message);
        assert profile != null;
        assert profile.equals(ItiPixPdqProfile.ITI_8_ADT_A40);

        message = getParsedMessage("hl7/iti-10.hl7");
        profile = MessageUtils.guessGazelleProfile(PixPdqTransactions.ITI10, message);
        assert profile != null;
        assert profile.equals(ItiPixPdqProfile.ITI_10_ADT_A31);

        message = getParsedMessage("hl7/iti-21.hl7");
        profile = MessageUtils.guessGazelleProfile(PixPdqTransactions.ITI21, message);
        assert profile != null;
        assert profile.equals(ItiPixPdqProfile.ITI_21_QBP_Q22);

        profile = MessageUtils.guessGazelleProfile(PixPdqTransactions.ITI22, message);
        assert profile == null;
    }
}
