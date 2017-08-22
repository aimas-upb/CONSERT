package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;


/*
 *   Class for modeling an exercise  high level activity
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:ExerciseHLA")
public class ExerciseHLA extends HLA {
    
	public ExerciseHLA() {
    	super(new HLAType("EXERCISE"));
    }
	
    public ExerciseHLA(Person person, AnnotationData annotationData) {
    	super(person, new HLAType("EXERCISE"), annotationData);
    }
}
