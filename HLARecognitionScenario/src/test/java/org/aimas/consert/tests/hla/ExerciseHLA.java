package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;


/*
 *   Class for modeling an exercise  high level activity
*/
public class ExerciseHLA extends HLA {
    
	public ExerciseHLA() {
    	super(Type.DISCUSSING);
    }
	
    public ExerciseHLA(Person person, AnnotationData annotationData) {
    	super(person, Type.EXERCISE, annotationData);
    }
}
