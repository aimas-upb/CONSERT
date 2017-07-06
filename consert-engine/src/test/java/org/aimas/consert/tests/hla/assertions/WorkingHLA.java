package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;

/*
 * Class for modeling a working  high level activity
*/
public class WorkingHLA extends HLA {
    public WorkingHLA() {
        super(HLAType.WORKING);
    }

    public WorkingHLA(Person person, AnnotationData annotationData) {
        super(person, HLAType.WORKING, annotationData);
    }

}
