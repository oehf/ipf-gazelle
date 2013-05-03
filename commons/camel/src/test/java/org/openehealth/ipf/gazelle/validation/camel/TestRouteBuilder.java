package org.openehealth.ipf.gazelle.validation.camel;

import org.apache.camel.builder.RouteBuilder;
import org.openehealth.ipf.gazelle.validation.core.GazelleProfile;

/**
 *
 */
public class TestRouteBuilder extends RouteBuilder {

    private GazelleProfileValidators validators = new GazelleProfileValidators();

    @Override
    public void configure() throws Exception {

        from("direct:iti10")
            .process(validators.gazelleValidatingProcessor(GazelleProfile.ITI_10_ADT_A31))
            .end();

        from("direct:iti21")
            .process(validators.gazelleValidatingProcessor(GazelleProfile.ITI_21_QBP_Q22))
            .end();

    }
}
