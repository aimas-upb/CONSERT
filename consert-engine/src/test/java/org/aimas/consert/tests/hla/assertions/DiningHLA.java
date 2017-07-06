package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;


/*
 *   Class for modeling a dining  high level activity
*/
public class DiningHLA extends HLA {

    public DiningHLA() {
        super(HLAType.DINING);
    }

    public DiningHLA(Person person, AnnotationData annotationData) {
        super(person, HLAType.DINING, annotationData);
    }
}
