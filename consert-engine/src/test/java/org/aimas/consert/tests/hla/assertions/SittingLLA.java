package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/*
 * Class for modeling a sitting  low level activity
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:SittingLLA")
public class SittingLLA extends LLA {
    public SittingLLA() {
        super(new LLAType("SITTING"));
    }

    public SittingLLA(Person person, AnnotationData annotationData) {
        super(person, new LLAType("SITTING"), annotationData);
    }
}
