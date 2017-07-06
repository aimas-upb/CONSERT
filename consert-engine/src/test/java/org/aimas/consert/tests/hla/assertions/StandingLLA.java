package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;

/*
 * Class for modeling a standing  low level activity
*/
public class StandingLLA extends LLA {
    public StandingLLA() {
        super(LLAType.STANDING);
    }

    public StandingLLA(Person person, AnnotationData annotationData) {
        super(person, LLAType.STANDING, annotationData);
    }
}
