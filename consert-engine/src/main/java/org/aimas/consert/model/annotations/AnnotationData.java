package org.aimas.consert.model.annotations;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@RDFNamespaces({
	"annotation = " + Constants.ANNOTATION_NS
})
@RDFBean("annotation:AnnotationData")
@JsonDeserialize(as=DefaultAnnotationData.class)
public interface AnnotationData {
	boolean allowsAnnotationContinuity(AnnotationData annotationData);
	boolean allowsAnnotationInsertion();
	
	AnnotationData applyCombinationOperator(AnnotationData otherAnn);
	
	AnnotationData applyExtensionOperator(AnnotationData otherAnn);
	
	@RDF("annotation:timestamp")
	long getTimestamp();
	
	@RDF("annotation:duration")
	long getDuration();
	
	boolean hasSameValidity(AnnotationData otherAnn);
	
	boolean hasIncludedValidity(AnnotationData otherAnn);

	AnnotationData cloneAnnotations();
}
