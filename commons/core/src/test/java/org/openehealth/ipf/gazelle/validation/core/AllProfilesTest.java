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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XStaticDef;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;
import org.openehealth.ipf.gazelle.validation.profile.card.CardProfile;
import org.openehealth.ipf.gazelle.validation.profile.card.CardTransactions;
import org.openehealth.ipf.gazelle.validation.profile.lab.LabProfile;
import org.openehealth.ipf.gazelle.validation.profile.lab.LabTransactions;
import org.openehealth.ipf.gazelle.validation.profile.pam.ItiPamProfile;
import org.openehealth.ipf.gazelle.validation.profile.pam.PamTransactions;
import org.openehealth.ipf.gazelle.validation.profile.pat.PatProfile;
import org.openehealth.ipf.gazelle.validation.profile.pat.PatTransactions;
import org.openehealth.ipf.gazelle.validation.profile.pcd.PcdProfile;
import org.openehealth.ipf.gazelle.validation.profile.pcd.PcdTransactions;
import org.openehealth.ipf.gazelle.validation.profile.pharmh.PharmhProfile;
import org.openehealth.ipf.gazelle.validation.profile.pharmh.PharmhTransactions;
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.ItiPixPdqProfile;
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.PixPdqTransactions;
import org.openehealth.ipf.gazelle.validation.profile.rad.RadProfile;
import org.openehealth.ipf.gazelle.validation.profile.rad.RadTransactions;
import org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.isCompatibleVersion;

/**
 * Loads every conformance profile shipped by the profile modules, so that missing, renamed or unparseable
 * profile files are detected at build time instead of at runtime, and checks that each profile's metadata
 * matches what its {@link ConformanceProfile} constant declares.
 */
public class AllProfilesTest {

    private static final List<Class<? extends Enum<? extends ConformanceProfile>>> PROFILE_ENUMS = List.of(
            CardProfile.class, ItiPamProfile.class, ItiPixPdqProfile.class, LabProfile.class,
            PatProfile.class, PcdProfile.class, PharmhProfile.class, RadProfile.class);

    private static final List<Class<? extends Enum<? extends HL7v2Transactions>>> TRANSACTION_ENUMS = List.of(
            CardTransactions.class, PamTransactions.class, PixPdqTransactions.class, LabTransactions.class,
            PatTransactions.class, PcdTransactions.class, PharmhTransactions.class, RadTransactions.class);

    // Profiles whose declared HL7 version differs from the version in the Gazelle profile.
    // The declarations follow the IHE technical framework, which disagrees with the Gazelle profile here:
    // the Gazelle profile itself is wrong or outdated and should be corrected upstream. Until then, the
    // versions must be compatible (see MessageUtils#isCompatibleVersion), so that messages with either
    // version are matched to the profile and get only a COMPATIBLE_MSH_VERSION_FIELD warning.
    private static final Set<ConformanceProfile> KNOWN_VERSION_MISMATCHES = Set.of(
            // PaLM TF-2c Rev. 11.0 3.44.3 / 3.45.3: HL7 v2.5.1 / 2.5.1. Gazelle profile: HL7Version="2.5"
            LabProfile.LAB_61_OML_O33,
            LabProfile.LAB_61_ORL_O34,
            LabProfile.LAB_62_QBP_SLI,
            LabProfile.LAB_62_RSP_SLI,
            // PaLM TF-2c Rev. 11.0 3.46.3: HL7 V.2.5.1 (LBL supplement Rev. 1.2 TI 2015 still said V.2.5).
            // Gazelle profile: HL7Version="2.5", but Topics="confsig-IHE-2.5.1-..."
            LabProfile.LAB_63_OML_O33,
            LabProfile.LAB_63_ORL_O34,
            // ARPH supplement Rev. 2.0 TI 2010 Z.3: HL7 2.5.1. Gazelle profile: HL7Version="2.5"
            PatProfile.PAT_10_ORU_R01);

    private final CachingGazelleProfileRule rule = new CachingGazelleProfileRule(ItiPixPdqProfile.ITI_8_ADT_A01);
    private final HapiContext context = gazelleContext();

    @AfterAll
    public static void tearDown() {
        CachingGazelleProfileRule.clearCache();
    }

    @TestFactory
    public Stream<DynamicTest> everyProfileLoadsAndMatchesItsDeclaration() {
        return PROFILE_ENUMS.stream()
                .flatMap(type -> Arrays.stream(type.getEnumConstants()))
                .map(constant -> (ConformanceProfile) constant)
                .map(profile -> DynamicTest.dynamicTest(profile.toString(), () -> checkProfile(profile)));
    }

    @TestFactory
    public Stream<DynamicTest> everyTransactionReferencesLoadableProfiles() {
        return TRANSACTION_ENUMS.stream()
                .flatMap(type -> Arrays.stream(type.getEnumConstants()))
                .map(constant -> (HL7v2Transactions) constant)
                .map(transaction -> DynamicTest.dynamicTest(transaction.toString(), () -> {
                    assertFalse(transaction.conformanceProfiles().isEmpty(), "no profiles");
                    for (var profile : transaction.conformanceProfiles()) {
                        assertNotNull(rule.parseProfile(context, profile.profileInfo().profileId()), profile.toString());
                    }
                }));
    }

    private void checkProfile(ConformanceProfile profile) throws Exception {
        var info = profile.profileInfo();
        var loaded = rule.parseProfile(context, info.profileId()).getProfile();

        if (KNOWN_VERSION_MISMATCHES.contains(profile)) {
            assertNotEquals(info.hl7version(), loaded.getHL7Version(),
                    profile + ": HL7 version mismatch has been fixed, remove from KNOWN_VERSION_MISMATCHES");
            assertTrue(isCompatibleVersion(info.hl7version(), loaded.getHL7Version()),
                    profile + ": declared HL7 version " + info.hl7version() + " is not compatible with profile version " +
                            loaded.getHL7Version());
        } else {
            assertEquals(info.hl7version(), loaded.getHL7Version(), profile + ": HL7 version");
        }

        var staticDef = loaded.getDynamicDevesAndHL7V2XStaticDevesAndHL7V2XStaticDefReves().stream()
                .filter(HL7V2XStaticDef.class::isInstance)
                .map(HL7V2XStaticDef.class::cast)
                .findFirst();
        assertTrue(staticDef.isPresent(), profile + ": profile has no static definition");
        assertEquals(info.type(), staticDef.get().getMsgType(), profile + ": message type");
        // "ALL" in the profile matches any trigger event (GazelleProfileRule skips the event check then)
        if (!"ALL".equals(staticDef.get().getEventType())) {
            assertEquals(info.event(), staticDef.get().getEventType(), profile + ": trigger event");
        }
    }

    private static HapiContext gazelleContext() {
        HapiContext context = new DefaultHapiContext();
        context.setProfileStore(new GazelleProfileStore());
        return context;
    }
}
