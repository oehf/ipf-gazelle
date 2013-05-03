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

import ca.uhn.hl7v2.conf.store.ProfileStore;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Boris Stanojevic
 */
public class GazzelleProfileStore implements ProfileStore {

    private static final String DEFAULT_CLASSPATH_PREFIX = "/org/openehealth/ipf/gazelle/validation/profile/v2/";

    private String prefix;

    public GazzelleProfileStore() {
        this(DEFAULT_CLASSPATH_PREFIX);
    }

    public GazzelleProfileStore(String prefix) {
        super();
        this.prefix = prefix;
    }

    @Override
    public String getProfile(String id) throws IOException {
        InputStream ios = this.getClass().getResourceAsStream(prefix + id);
        String result = null;
        try {
            if (ios != null){ result = IOUtils.toString(ios);}
        } finally {
            IOUtils.closeQuietly(ios);
        }

        return result;
    }

    @Override
    public void persistProfile(String ID, String profile) throws IOException {
        throw new UnsupportedOperationException("Can't persist profile -- this profile store is read-only");
    }

}
