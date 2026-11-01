/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;
import org.openehealth.ipf.gazelle.validation.profile.HL7v2Transactions;
import org.openehealth.ipf.gazelle.validation.profile.store.GazelleProfileStore;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class CachingGazelleProfileRuleTest {

    private CachingGazelleProfileRule profileRule1;
    private CachingGazelleProfileRule profileRule2;
    private ConformanceProfile mockProfile;

    @BeforeEach
    public void setup() {
        mockProfile = createMock(ConformanceProfile.class);
        profileRule1 = new TestCachingGazelleProfileRule(mockProfile);
        profileRule2 = new TestCachingGazelleProfileRule(mockProfile);
    }

    @AfterEach
    public void tearDown() {
        CachingGazelleProfileRule.clearCache();
        TestCachingGazelleProfileRule.LOADS.set(0);
    }

    @Test
    public void testCaching() throws Exception {
        ConformanceProfileInfo cpi = new ConformanceProfileInfoImpl("4711", "myTransaction", "A01", "2.5");
        expect(mockProfile.profileInfo()).andReturn(cpi);
        HapiContext context = new DefaultHapiContext();

        var obtainedRule = profileRule1.parseProfile(context, "4711");
        assertSame(obtainedRule, profileRule1.parseProfile(context, "4711"));
        assertSame(obtainedRule, profileRule2.parseProfile(context, "4711"));
    }

    @Test
    public void testCachingIsPerProfileStore() throws Exception {
        HapiContext gazelleContext1 = new DefaultHapiContext();
        gazelleContext1.setProfileStore(new GazelleProfileStore());
        HapiContext gazelleContext2 = new DefaultHapiContext();
        gazelleContext2.setProfileStore(new GazelleProfileStore());
        HapiContext otherContext = new DefaultHapiContext();
        otherContext.setProfileStore(new ClasspathProfileStore("/some/other/profiles"));

        var gazelleRule = profileRule1.parseProfile(gazelleContext1, "4711");
        // Separate GazelleProfileStore instances serve the same profiles and share cached rules
        assertSame(gazelleRule, profileRule1.parseProfile(gazelleContext2, "4711"));
        // A different store may define a different profile under the same ID
        var otherRule = profileRule1.parseProfile(otherContext, "4711");
        assertNotSame(gazelleRule, otherRule);
        assertSame(otherRule, profileRule1.parseProfile(otherContext, "4711"));
    }

    @Test
    public void testMissingProfileStore() {
        HapiContext context = new DefaultHapiContext();
        context.setProfileStore(null);
        assertThrows(IllegalStateException.class, () -> profileRule1.parseProfile(context, "4711"));
    }

    @Test
    public void testClearCache() throws Exception {
        HapiContext context = new DefaultHapiContext();
        assertEquals(0, CachingGazelleProfileRule.getCacheSize());
        var rule = profileRule1.parseProfile(context, "4711");
        profileRule1.parseProfile(context, "4712");
        assertEquals(2, CachingGazelleProfileRule.getCacheSize());

        CachingGazelleProfileRule.clearCache();
        assertEquals(0, CachingGazelleProfileRule.getCacheSize());
        assertNotSame(rule, profileRule1.parseProfile(context, "4711"));
        assertEquals(1, CachingGazelleProfileRule.getCacheSize());
    }

    @Test
    public void testConstructorsRejectNull() {
        assertThrows(NullPointerException.class, () -> new CachingGazelleProfileRule((ConformanceProfile) null));
        assertThrows(NullPointerException.class, () -> new CachingGazelleProfileRule((HL7v2Transactions) null));
    }

    @Test
    public void testConcurrentCaching() throws Exception {
        ConformanceProfileInfo cpi = new ConformanceProfileInfoImpl("4711", "myTransaction", "A01", "2.5");
        expect(mockProfile.profileInfo()).andReturn(cpi);
        final HapiContext context = new DefaultHapiContext();

        var executor = Executors.newCachedThreadPool();
        final List<GazelleProfileRule> rules = new CopyOnWriteArrayList<>();
        var runs = 100;
        final var latch = new CountDownLatch(runs);
        for (var i = 0; i < runs; i++) {
            Thread.sleep(10);
            executor.submit(() -> {
                try {
                    rules.add(profileRule1.parseProfile(context, "4711"));
                    latch.countDown();
                } catch (Exception e) {
                    // do nothing
                }
            });
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(runs, rules.size());
        // no cache stampede: concurrent requests must not parse the same profile more than once
        assertEquals(1, TestCachingGazelleProfileRule.LOADS.get());
        var expected = rules.get(0);
        for (var i = 1; i < runs; i++) {
            assertSame(expected, rules.get(i));
        }
    }


    private static class TestCachingGazelleProfileRule extends CachingGazelleProfileRule {

        static final AtomicInteger LOADS = new AtomicInteger();

        public TestCachingGazelleProfileRule(ConformanceProfile profile) {
            super(profile);
        }

        @Override
        protected GazelleProfileRule loadRule(HapiContext hapiContext, String profileId) {
            try {
                LOADS.incrementAndGet();
                Thread.sleep(100); // may take some time
                return new GazelleProfileRule(createMock(HL7V2XConformanceProfile.class));
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
