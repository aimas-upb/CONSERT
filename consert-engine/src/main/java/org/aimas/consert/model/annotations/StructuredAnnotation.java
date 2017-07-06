package org.aimas.consert.model.annotations;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;

@RDFBean("annotation:StructuredAnnotation")
public interface StructuredAnnotation extends ContextAnnotation {
	
	@RDF("annotation:hasContinuityFunction")
	String getContinuityFunction();
	
	@RDF("annotation:hasJoinOperator")
	String getExtensionOperator();
	
	@RDF("annotation:hasMeetOperator")
	String getCombinationOperator();
}
