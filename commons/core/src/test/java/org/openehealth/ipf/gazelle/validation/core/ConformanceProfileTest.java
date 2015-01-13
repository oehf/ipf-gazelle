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
import org.openehealth.ipf.gazelle.validation.profile.card.CardProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.rad.RadProfile;

/**
 * @author Boris Stanojevic
 */
public class ConformanceProfileTest {

    @Test
    public void testProfile(){
        ConformanceProfileInfo conformanceProfileInfo = CardProfile.CARD_7_ACK_ALL.profileInfo();
        assert conformanceProfileInfo.triggerEvent().equals("ACK^ALL^ACK");
        assert conformanceProfileInfo.type().equals("ACK");
        assert conformanceProfileInfo.structure().equals("ACK");
        assert conformanceProfileInfo.event().equals("ALL");
        assert conformanceProfileInfo.transaction().equals("CARD-7");
        assert conformanceProfileInfo.profileId().equals("card/1.3.6.1.4.12559.11.1.1.146");
        assert conformanceProfileInfo.hl7version().equals("2.5");

        conformanceProfileInfo = RadProfile.RAD_48_SIU_S12.profileInfo();
        assert conformanceProfileInfo.triggerEvent().equals("SIU^S12^SIU_S12");
        assert conformanceProfileInfo.type().equals("SIU");
        assert conformanceProfileInfo.structure().equals("SIU_S12");
        assert conformanceProfileInfo.event().equals("S12");
        assert conformanceProfileInfo.transaction().equals("RAD-48");
        assert conformanceProfileInfo.profileId().equals("rad/1.3.6.1.4.12559.11.1.1.4");
        assert conformanceProfileInfo.hl7version().equals("2.3.1");

        ConformanceProfileInfo conformanceProfileInfo1 = RadProfile.RAD_48_SIU_S12.profileInfo();
        assert conformanceProfileInfo.equals(conformanceProfileInfo1);
    }
}
