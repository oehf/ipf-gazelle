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


import java.io.IOException;

import ca.uhn.hl7v2.conf.store.ProfileStore;
import org.junit.Before;
import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.profile.ItiPamProfile;
import org.openehealth.ipf.gazelle.validation.profile.ItiPixPdqProfile;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Boris Stanojevic
 */
public class ConformanceProfileStoreTest extends AbstractGazelleProfileValidatorTest {

    private ProfileStore profileStore;

    @Before
    public void setup() throws Exception {
        profileStore = hapiContext.getProfileStore();
    }

    @Test
    public void testITI30ProfileStore() throws IOException {
        String profileString = profileStore.getProfile(ItiPamProfile.ITI_30_ADT_A47.profileInfo().profileId());
        assertNotNull(profileString);
        assertTrue(profileString.contains("HL7v2xStaticDef"));
        assertTrue(profileString.contains("EventType=\"A47\""));
        assertTrue(profileString.contains("MsgStructID=\"ADT_A30\""));
    }

    @Test
    public void testITI9ProfileStore() throws IOException {
        String profileString = profileStore.getProfile(ItiPixPdqProfile.ITI_9_RSP_K23.profileInfo().profileId());
        assertNotNull(profileString);
        assertTrue(profileString.contains("HL7v2xStaticDef"));
        assertTrue(profileString.contains("EventType=\"K23\""));
        assertTrue(profileString.contains("MsgStructID=\"RSP_K23\""));
    }

    @Test(expected = NullPointerException.class)
    public void testNonExistingProfile() throws IOException {
        profileStore.getProfile("BLAH");
    }

    @Test(expected = NullPointerException.class)
    public void testNullProfile() throws IOException {
        profileStore.getProfile(null);
    }
}
