package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/*
 * Class for modeling a standing  low level activity
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:StandingLLA")
public class StandingLLA extends LLA {
    public StandingLLA() {
        super(new LLAType("STANDING"));
    }

    @Override
    public ContextAssertion cloneContent() {
        return new StandingLLA(person, null);
    }

    public StandingLLA(Person person, AnnotationData annotationData) {
        super(person, new LLAType("STANDING"), annotationData);
    }
}
