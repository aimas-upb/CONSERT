package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;


/*
 *   Class for modeling an exercise  high level activity
*/
public class ExerciseHLA extends HLA {
    
	public ExerciseHLA() {
    	super(HLAType.DISCUSSING);
    }
	
    public ExerciseHLA(Person person, AnnotationData annotationData) {
    	super(person, HLAType.EXERCISE, annotationData);
    }
}
