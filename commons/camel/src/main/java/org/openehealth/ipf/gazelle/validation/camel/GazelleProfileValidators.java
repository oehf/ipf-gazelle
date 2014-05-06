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
package org.openehealth.ipf.gazelle.validation.camel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

import ca.uhn.hl7v2.Severity;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.MessageRule;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.Validate;
import org.openehealth.ipf.commons.core.modules.api.ValidationException;
import org.openehealth.ipf.gazelle.validation.core.CachingGazelleProfileRule;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2InteractionId;

/**
 * Factory for manually triggering a validation of a message depending on a profile or a defined
 * IHETransaction. In general this should not be necessary when the HapiContext of the parsed
 * message contains a ValidationContext that includes the corresponding
 * {@link org.openehealth.ipf.gazelle.validation.core.GazelleProfileRule GazelleProfileRule}
 * instance as one of its message rules. In that case it would be sufficient to use the
 * <pre>conformsWithDefault</pre> predicate supplied by camel-hl7.
 *
 * @author Boris Stanojevic
 * @author Christian Ohr
 */
public final class GazelleProfileValidators {

    private GazelleProfileValidators() {
    }


    /**
     * Returns a validating Camel processor for a dedicated profile
     *
     * @param conformanceProfile HL7 conformance profile
     * @return a validating Camel processor for a dedicated profile
     */
    public static Processor gazelleValidatingProcessor(final ConformanceProfile conformanceProfile) {
        return new Processor() {

            private CachingGazelleProfileRule validator = new CachingGazelleProfileRule(conformanceProfile);

            @Override
            public void process(Exchange exchange) throws Exception {
                doValidate(exchange, validator);
            }
        };
    }

    /**
     * Returns a validating Camel processor for a message in a IHE transaction. The actual profile
     * to be used is guessed from the message header
     *
     * @param iheTransaction IHE transaktion
     * @return a validating Camel processor for a message in a IHE transaction
     */
    public static Processor gazelleValidatingProcessor(final HL7v2InteractionId iheTransaction) {
        return new Processor() {

            private CachingGazelleProfileRule validator = new CachingGazelleProfileRule(iheTransaction);

            @Override
            public void process(Exchange exchange) throws Exception {
                doValidate(exchange, validator);
            }
        };
    }

    private static void doValidate(Exchange exchange, final MessageRule validator)
            throws IOException, JAXBException {

        Message message = exchange.getIn().getBody(Message.class);
        // TODO make sure this also works the IPF HL7DSL MessageAdapter instances!
        Validate.notNull(message, "Exchange does not contain required 'ca.uhn.hl7v2.model.Message' type");

        ca.uhn.hl7v2.validation.ValidationException[] exceptions = validator.apply(message);

        List<ca.uhn.hl7v2.validation.ValidationException> fatalExceptions = new ArrayList<ca.uhn.hl7v2.validation.ValidationException>();
        for (ca.uhn.hl7v2.validation.ValidationException exception : exceptions) {
            if (exception.getSeverity().equals(Severity.ERROR)) {
                fatalExceptions.add(exception);
            }
        }

        if (fatalExceptions.size() > 0) {
            throw new ValidationException("Message validation failed", fatalExceptions);
        }
    }

}
