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

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.conf.store.ClasspathProfileStore;
import ca.uhn.hl7v2.conf.store.ProfileStore;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.gazelle.validation.profile.pam.ItiPamProfile;
import org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the actual loading and parsing of conformance profiles (as opposed to {@link CachingGazelleProfileRuleTest},
 * which tests caching with a stubbed loader).
 */
public class CachingGazelleProfileRuleLoadingTest {

    private final CachingGazelleProfileRule rule = new CachingGazelleProfileRule(ItiPamProfile.ITI_30_ADT_A47);

    @AfterEach
    public void tearDown() {
        CachingGazelleProfileRule.clearCache();
    }

    @Test
    public void testLoadGazelleProfile() throws Exception {
        var loaded = rule.parseProfile(context(new GazelleProfileStore()),
                ItiPamProfile.ITI_30_ADT_A47.profileInfo().profileId());
        assertNotNull(loaded.getProfile().getMetaData());
        assertEquals("2.5", loaded.getProfile().getHL7Version());
    }

    @Test
    public void testMissingProfileInUrlStore() {
        var e = assertThrows(IOException.class, () -> rule.parseProfile(context(new GazelleProfileStore()), "BLAH"));
        assertTrue(e.getMessage().contains("BLAH"), e.getMessage());
    }

    @Test
    public void testMissingProfileInOtherStore() {
        var e = assertThrows(IOException.class, () -> rule.parseProfile(context(new StringProfileStore(null)), "BLAH"));
        assertTrue(e.getMessage().contains("BLAH"), e.getMessage());
    }

    @Test
    public void testFailedLoadIsNotCached() {
        var context = context(new GazelleProfileStore());
        assertThrows(IOException.class, () -> rule.parseProfile(context, "BLAH"));
        assertThrows(IOException.class, () -> rule.parseProfile(context, "BLAH"));
    }

    @Test
    public void testEncodingIsTakenFromXmlDeclaration() throws Exception {
        // latin1.xml is ISO-8859-1 encoded and declares so. Decoding it with a default charset first would corrupt it.
        var loaded = rule.parseProfile(context(new ClasspathProfileStore("/profiles")), "latin1");
        assertEquals("Grüße", loaded.getProfile().getMetaData().getName());
        assertEquals("Müller", loaded.getProfile().getMetaData().getOrgName());
    }

    @Test
    public void testExternalEntitiesAreRejectedInUrlStore() {
        assertThrows(JAXBException.class,
                () -> rule.parseProfile(context(new ClasspathProfileStore("/profiles")), "xxe"));
    }

    @Test
    public void testExternalEntitiesAreRejectedInOtherStore() throws IOException {
        String xxe;
        try (InputStream in = getClass().getResourceAsStream("/profiles/xxe.xml")) {
            xxe = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertThrows(JAXBException.class, () -> rule.parseProfile(context(new StringProfileStore(xxe)), "xxe"));
    }

    private static HapiContext context(ProfileStore profileStore) {
        HapiContext context = new DefaultHapiContext();
        context.setProfileStore(profileStore);
        return context;
    }

    // A store that is not URL-based, serving one fixed profile string (or none)
    private record StringProfileStore(String profile) implements ProfileStore {

        @Override
        public String getProfile(String id) {
            return profile;
        }

        @Override
        public void persistProfile(String id, String profile) {
            throw new UnsupportedOperationException();
        }
    }
}
