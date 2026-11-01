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
package org.openehealth.ipf.gazelle.validation.profile.store;

import ca.uhn.hl7v2.conf.store.ClasspathProfileStore;

/**
 * @author Boris Stanojevic
 */
public class GazelleProfileStore extends ClasspathProfileStore {

    private static final String DEFAULT_CLASSPATH_PREFIX = "/org/openehealth/ipf/gazelle/validation/profile/v2";

    public GazelleProfileStore() {
        super(DEFAULT_CLASSPATH_PREFIX);
    }

    /**
     * All instances of the same class serve identical profiles, so they are considered equal. This lets
     * caches keyed by profile store (e.g. parsed profile rules) be shared across separately created instances.
     * Subclasses that serve different profiles are distinguished by their class.
     */
    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
