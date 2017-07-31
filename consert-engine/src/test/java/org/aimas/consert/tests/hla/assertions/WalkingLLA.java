package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/*
 * Class for modeling a walking  low level activity
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:WalkingLLA")
public class WalkingLLA extends LLA {
    
	public WalkingLLA() {
        super(new LLAType("WALKING"));
    }

    public WalkingLLA(Person person, AnnotationData annotationData) {
        super(person, new LLAType("WALKING"), annotationData);
    }
}
