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

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.Severity;
import ca.uhn.hl7v2.conf.ProfileException;
import ca.uhn.hl7v2.conf.parser.ProfileParser;
import ca.uhn.hl7v2.conf.spec.MetaData;
import ca.uhn.hl7v2.conf.spec.RuntimeProfile;
import ca.uhn.hl7v2.conf.store.DefaultCodeStoreRegistry;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Boris Stanojevic
 */
public abstract class AbstractGazelleProfileValidatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractGazelleProfileValidatorTest.class);

    protected HapiContext hapiContext;

    protected Unmarshaller unmarshaller;

    @BeforeEach
    public void onBefore() throws IOException, JAXBException {
        hapiContext = createHapiContext(false);
        var jaxbContext = JAXBContext.newInstance(HL7V2XConformanceProfile.class);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    protected void printOutExceptions(ValidationException... exceptions) {
        if (exceptions != null) {
            for (var exc : exceptions) {
                switch (exc.getSeverity()) {
                    case ERROR:
                        LOG.error("ERROR:", exc);
                        break;
                    case WARNING:
                        LOG.warn("WARN:", exc);
                        break;
                    case INFO:
                        LOG.warn("INFO:", exc);
                        break;
                }
            }
        }
    }

    protected int countExceptions(ValidationException[] exceptions, Severity severity) {
        var count = 0;
        for (var exc : exceptions) {
            if (severity.equals(exc.getSeverity())) {
                ++count;
            }
        }
        return count;
    }

    protected String getMessageAsString(String resourcePath) {
        String message = null;
        try {
            message = IOUtils.toString(this.getClass().getClassLoader().getResource(resourcePath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        assert message != null;
        return message.replaceAll("\n", "\r");
    }

    protected HapiContext createHapiContext(boolean validating) {
        HapiContext hapiContext = new DefaultHapiContext();
        hapiContext.setProfileStore(new GazelleProfileStore());
        hapiContext.getParserConfiguration().setValidating(validating);
        hapiContext.setCodeStoreRegistry(new DefaultCodeStoreRegistry());
        return hapiContext;
    }

    protected RuntimeProfile parseProfile(String profileId)
            throws ProfileException, IOException {
        var profileString = hapiContext.getProfileStore().getProfile(profileId);
        var profileParser = new ProfileParser(false);
        var runtimeProfile = profileParser.parse(profileString);
        var metaData = new MetaData();
        metaData.setVersion(runtimeProfile.getHL7Version());
        runtimeProfile.getMessage().setMetaData(metaData);
        return runtimeProfile;
    }

    protected HL7V2XConformanceProfile unmarshalProfile(ConformanceProfile profile)
            throws IOException, JAXBException {

        var profileString = hapiContext.getProfileStore().getProfile(profile.profileInfo().profileId());
        return (HL7V2XConformanceProfile) unmarshaller.unmarshal(new ByteArrayInputStream(profileString.getBytes()));
    }

    protected Message getParsedMessage(String path) throws HL7Exception {
        return hapiContext.getPipeParser().parse(getMessageAsString(path));
    }

}
