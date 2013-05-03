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


import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.conf.ProfileException;
import ca.uhn.hl7v2.conf.parser.ProfileParser;
import ca.uhn.hl7v2.conf.spec.RuntimeProfile;
import ca.uhn.hl7v2.conf.store.DefaultCodeStoreRegistry;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.core.GazelleProfile;
import org.openehealth.ipf.gazelle.validation.profile.store.GazzelleProfileStore;

import java.io.IOException;

/**
 * @author Boris Stanojevic
 */
public class GazelleProfileValidatorsTest {

    CamelContext camelContext = new DefaultCamelContext();

    Parser parser = new PipeParser();

    @Test
    public void testIti10() throws Exception {
        camelContext.addRoutes(new TestRouteBuilder());
        camelContext.start();
        ProducerTemplate template = new DefaultProducerTemplate(camelContext);
        template.start();
        template.sendBody("direct:iti10", getParsedMessage("hl7/iti-10.hl7"));
    }

    protected String getMessageAsString(String resourcePath){
        String message = null;
        try {
            message = IOUtils.toString(this.getClass().getClassLoader().getResource(resourcePath));
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        assert message != null;
        return message.replaceAll("\n", "\r");
    }

    protected Message getParsedMessage(String path) throws HL7Exception {
        return parser.parse(getMessageAsString(path));
    }
}
