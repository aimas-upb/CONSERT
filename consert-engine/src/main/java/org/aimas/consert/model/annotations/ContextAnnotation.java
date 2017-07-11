package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
	"annotation = " + Constants.ANNOTATION_NS,
	"rdfbeans = " + Constants.RDFBEANS_URI
})
@RDFBean("annotation:ContextAnnotation")
public abstract class ContextAnnotation {
	protected static long instanceCt = 1;
	
	protected String annotationIdentifier;
	
	@RDF("annotation:hasValue")
	public abstract Object getValue();
	
	@RDF("rdfbeans:bindingClass")
	public String getBindingClassName() {
		return getClass().getName();
	}
	
	public void setBindingClassName(String bindingClassName) {}
	
	@RDFSubject
	public String getAnnotationIdentifier() {
		if (annotationIdentifier == null) {
			annotationIdentifier = Constants.ANNOTATION_NS + getClass().getSimpleName() + "-" + (instanceCt++); 
		}
		
		return annotationIdentifier;
	}
	
	public void setAnnotationIdentifier(String annotationIdentifier) {
		this.annotationIdentifier = annotationIdentifier;
	}
}
