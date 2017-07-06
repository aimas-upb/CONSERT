package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;

/*
 * Class for modeling a discussing  high level activity 
*/
public class DiscussingHLA extends HLA {
    public DiscussingHLA() {
        super(HLAType.DISCUSSING);
    }
    
    
    public DiscussingHLA(Person person, AnnotationData annotationData) {
        super(person, HLAType.DISCUSSING, annotationData);
    }
}
