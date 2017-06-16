package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;

/*
 * Class for modeling a sitting  low level activity
*/
public class SittingLLA extends LLA {
    public SittingLLA() {
        super(Type.SITTING);
    }

    public SittingLLA(Person person, AnnotationData annotationData) {
        super(person, Type.SITTING, annotationData);
    }
}
