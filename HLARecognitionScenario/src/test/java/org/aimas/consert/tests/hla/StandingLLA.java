package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;

/*
 * Class for modeling a standing  low level activity
*/
public class StandingLLA extends LLA {
    public StandingLLA() {
        super(Type.STANDING);
    }

    public StandingLLA(Person person, AnnotationData annotationData) {
        super(person, Type.STANDING, annotationData);
    }
}
