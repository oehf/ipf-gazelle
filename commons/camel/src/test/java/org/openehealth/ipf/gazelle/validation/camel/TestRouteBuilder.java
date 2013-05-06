package org.openehealth.ipf.gazelle.validation.camel;

import org.apache.camel.builder.RouteBuilder;
import org.openehealth.ipf.gazelle.validation.core.GazelleProfile;
import org.openehealth.ipf.gazelle.validation.core.IHETransaction;

/**
 * @author Boris Stanojevic
 */
public class TestRouteBuilder extends RouteBuilder {

    private GazelleProfileValidators validators = new GazelleProfileValidators();

    @Override
    public void configure() throws Exception {

        from("direct:iti8")
            .process(validators.gazelleValidatingProcessor(IHETransaction.ITI8))
            .end();

        from("direct:iti10")
            .process(validators.gazelleValidatingProcessor(GazelleProfile.ITI_10_ADT_A31))
            .end();

        from("direct:iti21")
            .process(validators.gazelleValidatingProcessor())
            .end();

    }
}
