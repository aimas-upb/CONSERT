package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;

/*
 * Class for modeling a walking  low level activity
*/
public class WalkingLLA extends LLA {
    
	public WalkingLLA() {
        super(Type.WALKING);
    }

    public WalkingLLA(Person person, AnnotationData annotationData) {
        super(person, Type.WALKING, annotationData);
    }
}
