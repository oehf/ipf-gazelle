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
import ca.uhn.hl7v2.Severity;
import ca.uhn.hl7v2.conf.store.DefaultCodeStoreRegistry;
import ca.uhn.hl7v2.model.Message;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.apache.commons.lang3.Validate;
import org.openehealth.ipf.commons.core.modules.api.ValidationException;
import org.openehealth.ipf.gazelle.validation.core.GazelleProfile;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.core.GazelleProfileValidator;
import org.openehealth.ipf.gazelle.validation.core.IHETransaction;
import org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.guessGazelleProfile;

/**
 * @author Boris Stanojevic
 */
public class GazelleProfileValidators {

    private HapiContext hapiContext;

    private final Map<String, HL7V2XConformanceProfile> parsedProfileMap = new HashMap();

    private static Unmarshaller unmarshaler;

    static {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HL7V2XConformanceProfile.class);
            unmarshaler = jaxbContext.createUnmarshaller();
        } catch (JAXBException jaxbException){
             throw new RuntimeException(jaxbException.getMessage());
        }
    }

    public GazelleProfileValidators(){
        this.hapiContext = createHapiContext();
    }

    public GazelleProfileValidators(HapiContext hapiContext){
        this.hapiContext = hapiContext;
    }

    public Processor gazelleValidatingProcessor(final GazelleProfile gazelleProfile) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                doValidate(exchange, gazelleProfile);
            }
        };
    }

    public Processor gazelleValidatingProcessor(final IHETransaction iheTransaction) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                doValidate(exchange,
                        guessGazelleProfile(iheTransaction, exchange.getIn().getBody(Message.class)));
            }
        };
    }

    public Processor gazelleValidatingProcessor() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                doValidate(exchange, guessGazelleProfile(exchange.getIn().getBody(Message.class)));
            }
        };
    }

    protected void doValidate(Exchange exchange, final GazelleProfile gazelleProfile)
            throws IOException, JAXBException {

        Validate.notNull(gazelleProfile, "Gazelle profile not found, check your MSH-9 and MSH-12 values.");

        Message message = exchange.getIn().getBody(Message.class);
        Validate.notNull(message, "Exchange does not contain required 'ca.uhn.hl7v2.model.Message' type");

        GazelleProfileValidator validator = new GazelleProfileValidator(hapiContext);
        HL7V2XConformanceProfile staticDef = parseProfile(gazelleProfile.profileId());
        HL7Exception[] exceptions = validator.validate(message, staticDef);

        List<HL7Exception> fatalExceptions = new ArrayList<HL7Exception>();
        for (HL7Exception exception: exceptions){
            if (exception.getSeverity().equals(Severity.ERROR)){
                fatalExceptions.add(exception);
            }
        }

        if (fatalExceptions.size() > 0){
            throw new ValidationException("Message validation failed", fatalExceptions);
        }
    }

    protected HapiContext createHapiContext() {
        HapiContext hapiContext = new DefaultHapiContext();
        hapiContext.setProfileStore(new GazelleProfileStore());
        hapiContext.getParserConfiguration().setValidating(false);
        hapiContext.setCodeStoreRegistry(new DefaultCodeStoreRegistry());

        return hapiContext;
    }

    synchronized protected HL7V2XConformanceProfile parseProfile(String profileId) throws JAXBException, IOException {
        String profileString = hapiContext.getProfileStore().getProfile(profileId);
        HL7V2XConformanceProfile conformanceProfile;
        if (parsedProfileMap.containsKey(profileId)){
            conformanceProfile = parsedProfileMap.get(profileId);
        } else {
            conformanceProfile =
                    (HL7V2XConformanceProfile)unmarshaler.unmarshal(new ByteArrayInputStream(profileString.getBytes()));
            parsedProfileMap.put(profileId, conformanceProfile);
        }
        return conformanceProfile;
    }

//    protected StaticDef parseProfile(String profileId) throws ProfileException, IOException {
//        RuntimeProfile runtimeProfile;
//        if (parsedProfileMap.containsKey(profileId)){
//            runtimeProfile = parsedProfileMap.get(profileId);
//        } else {
//            String profileString = hapiContext.getProfileStore().getProfile(profileId);
//            ProfileParser profileParser = new ProfileParser(false);
//
//            runtimeProfile = profileParser.parse(profileString);
//            MetaData metaData = new MetaData();
//            metaData.setVersion(runtimeProfile.getHL7Version());
//            runtimeProfile.getMessage().setMetaData(metaData);
//            parsedProfileMap.put(profileId, runtimeProfile);
//        }
//        return runtimeProfile.getMessage();
//    }

}
