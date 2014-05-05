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
package org.openehealth.ipf.gazelle.validation.camel;

import org.apache.camel.builder.RouteBuilder;
import org.openehealth.ipf.gazelle.validation.profile.ItiProfile;
import org.openehealth.ipf.gazelle.validation.profile.ItiTransactions;

/**
 * @author Boris Stanojevic
 */
public class TestRouteBuilder extends RouteBuilder {

    private GazelleProfileValidators validators = new GazelleProfileValidators();

    @Override
    public void configure() throws Exception {

        from("direct:iti8")
            .process(validators.gazelleValidatingProcessor(ItiTransactions.ITI8))
            .end();

        from("direct:iti10")
            .process(validators.gazelleValidatingProcessor(ItiProfile.ITI_10_ADT_A31))
            .end();

        from("direct:iti21")
            .process(validators.gazelleValidatingProcessor(ItiTransactions.ITI21))
            .end();

    }
}
