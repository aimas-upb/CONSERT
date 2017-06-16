package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;

/*
 * Class for modeling a discussing  high level activity 
*/
public class DiscussingHLA extends HLA {
    public DiscussingHLA() {
        super(Type.DISCUSSING);
    }
    
    
    public DiscussingHLA(Person person, AnnotationData annotationData) {
        super(person, Type.DISCUSSING, annotationData);
    }
}
