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


import org.junit.Test;
import org.openehealth.ipf.gazelle.validation.profile.CardProfile;
import org.openehealth.ipf.gazelle.validation.profile.GazelleProfile;
import org.openehealth.ipf.gazelle.validation.profile.RadProfile;

/**
 * @author Boris Stanojevic
 */
public class GazelleProfileTest {

    @Test
    public void testProfile(){
        GazelleProfile profile = CardProfile.CARD_7_ACK_ALL;
        assert profile.triggerEvent().equals("ACK^ALL^ACK");
        assert profile.type().equals("ACK");
        assert profile.structure().equals("ACK");
        assert profile.event().equals("ALL");
        assert profile.transaction().equals("CARD-7");
        assert profile.profileId().equals("1.3.6.1.4.12559.11.1.1.146");
        assert profile.hl7version().equals("2.5");

        profile = RadProfile.RAD_48_SIU_S12;
        assert profile.triggerEvent().equals("SIU^S12^SIU_S12");
        assert profile.type().equals("SIU");
        assert profile.structure().equals("SIU_S12");
        assert profile.event().equals("S12");
        assert profile.transaction().equals("RAD-48");
        assert profile.profileId().equals("1.3.6.1.4.12559.11.1.1.4");
        assert profile.hl7version().equals("2.3.1");

        GazelleProfile profile1 = RadProfile.RAD_48_SIU_S12;
        assert profile.equals(profile1);
    }
}
