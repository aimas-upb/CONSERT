package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
	"annotation = " + Constants.ANNOTATION_BASE_URI,
	"rdfbeans = " + Constants.RDFBEANS_URI
})
@RDFBean("annotation:ContextAnnotation")
public interface ContextAnnotation {
	
	@RDF("annotation:hasValue")
	Object getValue();
	
	@RDF("rdfbeans:bindingClass")
	String getBindingClassName();
	
	@RDFSubject
	String getAnnotationIdentifier();
	
	void setAnnotationIdentifier(String annotationIdentifier);
}
