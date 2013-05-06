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
import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.core.util.MessageUtils;

/**
 * @author Boris Stanojevic
 */
public class IHETransactionTest extends AbstractGazelleProfileValidatorTest {

    @Test
    public void testIHETransaction(){
        IHETransaction iheTransaction = IHETransaction.ITI8;

        assert (iheTransaction.transactionTypes().size() == 11);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ACK);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ADT_A01);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ADT_A04);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ADT_A05);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ADT_A08);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ADT_A40);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ACK_A01);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ACK_A04);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ACK_A05);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ACK_A08);
        assert iheTransaction.transactionTypes().contains(GazelleProfile.ITI_8_ACK_A40);
    }

    @Test
    public void testGuessTransactionProfile() throws HL7Exception {
        Message message = getParsedMessage("hl7/iti-8.hl7");
        GazelleProfile profile = MessageUtils.guessGazelleProfile(IHETransaction.ITI8, message);
        assert profile != null;
        assert profile.equals(GazelleProfile.ITI_8_ADT_A40);

        message = getParsedMessage("hl7/iti-10.hl7");
        profile = MessageUtils.guessGazelleProfile(IHETransaction.ITI10, message);
        assert profile != null;
        assert profile.equals(GazelleProfile.ITI_10_ADT_A31);

        message = getParsedMessage("hl7/iti-21.hl7");
        profile = MessageUtils.guessGazelleProfile(IHETransaction.ITI21, message);
        assert profile != null;
        assert profile.equals(GazelleProfile.ITI_21_QBP_Q22);

        profile = MessageUtils.guessGazelleProfile(IHETransaction.ITI22, message);
        assert profile == null;
    }
}
