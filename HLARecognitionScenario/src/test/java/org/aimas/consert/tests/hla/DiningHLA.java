package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;


/*
 *   Class for modeling a dining  high level activity
*/
public class DiningHLA extends HLA {

    public DiningHLA() {
        super(Type.DINING);
    }

    public DiningHLA(Person person, AnnotationData annotationData) {
        super(person, Type.DINING, annotationData);
    }
}
