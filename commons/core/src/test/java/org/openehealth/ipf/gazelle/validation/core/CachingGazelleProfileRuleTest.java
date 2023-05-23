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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfile;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfo;
import org.openehealth.ipf.gazelle.validation.profile.ConformanceProfileInfoImpl;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

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
        profileRule1.reset();
    }

    @Test
    public void testCaching() throws Exception {
        ConformanceProfileInfo cpi = new ConformanceProfileInfoImpl("4711", "myTransaction", "A01", "2.5");
        expect(mockProfile.profileInfo()).andReturn(cpi);
        HapiContext context = new DefaultHapiContext();

        GazelleProfileRule obtainedRule = profileRule1.parseProfile(context, "4711");
        assertSame(obtainedRule, profileRule1.parseProfile(context, "4711"));
        assertSame(obtainedRule, profileRule2.parseProfile(context, "4711"));
    }

    @Test
    public void testConcurrentCaching() throws Exception {
        ConformanceProfileInfo cpi = new ConformanceProfileInfoImpl("4711", "myTransaction", "A01", "2.5");
        expect(mockProfile.profileInfo()).andReturn(cpi);
        final HapiContext context = new DefaultHapiContext();

        ExecutorService executor = Executors.newCachedThreadPool();
        final List<GazelleProfileRule> rules = new CopyOnWriteArrayList<>();
        int runs = 100;
        final CountDownLatch latch = new CountDownLatch(runs);
        for (int i = 0; i < runs; i++) {
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
        GazelleProfileRule expected = rules.get(0);
        for (int i = 1; i < runs; i++) {
            assertSame(expected, rules.get(i));
        }
    }


    private static class TestCachingGazelleProfileRule extends CachingGazelleProfileRule {

        public TestCachingGazelleProfileRule(ConformanceProfile profile) {
            super(profile);
        }

        @Override
        protected GazelleProfileRule loadRule(HapiContext hapiContext, String profileId) throws JAXBException, IOException {
            try {
                Thread.sleep(100); // may take some time
                return new GazelleProfileRule(createMock(HL7V2XConformanceProfile.class));
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
