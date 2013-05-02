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


import ca.uhn.hl7v2.conf.store.ProfileStore;
import org.junit.Test;
import java.io.IOException;
import static junit.framework.Assert.*;

/**
 * @author Boris Stanojevic
 */
public class GazelleProfileStoreTest extends AbstractGazelleProfileValidatorTest {

    @Test
    public void testProfileStore() throws IOException {
        ProfileStore profileStore = hapiContext.getProfileStore();
        String profileString = profileStore.getProfile(GazelleProfile.ITI_30_ADT_A47.profileId());
        assertNotNull(profileString);
        assertTrue(profileString.contains("HL7v2xStaticDef"));
        assertTrue(profileString.contains("EventType=\"A47\""));
        assertTrue(profileString.contains("MsgStructID=\"ADT_A30\""));

        profileString = profileStore.getProfile("BLAH");
        assertNull(profileString);

        profileString = hapiContext.getProfileStore().getProfile(null);
        assertNull(profileString);

        profileString = profileStore.getProfile(GazelleProfile.ITI_9_RSP_K23.profileId());
        assertNotNull(profileString);
        assertTrue(profileString.contains("HL7v2xStaticDef"));
        assertTrue(profileString.contains("EventType=\"K23\""));
        assertTrue(profileString.contains("MsgStructID=\"RSP_K23\""));
    }
}
