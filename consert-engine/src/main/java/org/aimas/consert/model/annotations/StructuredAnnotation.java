package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:StructuredAnnotation")
public abstract class StructuredAnnotation extends ContextAnnotation {

	@RDF("annotation:hasContinuityFunction")
	public abstract String getContinuityFunction();

	@RDF("annotation:hasJoinOperator")
	public abstract String getExtensionOperator();
	
	@RDF("annotation:hasMeetOperator")
	public abstract String getCombinationOperator();
	
	@Override
	public boolean isStructured() {
		return true;
	}
	
	public abstract StructuredAnnotation applyCombinationOperator(StructuredAnnotation other);
	
	public abstract StructuredAnnotation applyExtensionOperator(StructuredAnnotation other);
	
	public abstract boolean allowsContinuity(StructuredAnnotation other);
	
	public abstract boolean allowsInsertion();
}
