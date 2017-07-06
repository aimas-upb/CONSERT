package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;

/*
 * Class for modeling a sitting  low level activity
*/
public class SittingLLA extends LLA {
    public SittingLLA() {
        super(LLAType.SITTING);
    }

    public SittingLLA(Person person, AnnotationData annotationData) {
        super(person, LLAType.SITTING, annotationData);
    }
}
