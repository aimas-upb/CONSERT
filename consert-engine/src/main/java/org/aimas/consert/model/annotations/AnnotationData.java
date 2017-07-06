package org.aimas.consert.model.annotations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=DefaultAnnotationData.class)
public interface AnnotationData {
	boolean allowsAnnotationContinuity(AnnotationData annotationData);
	boolean allowsAnnotationInsertion();
	
	AnnotationData applyCombinationOperator(AnnotationData otherAnn);
	
	AnnotationData applyExtensionOperator(AnnotationData otherAnn);
	
	double getTimestamp();
	
	long getDuration();
	
	boolean hasSameValidity(AnnotationData otherAnn);
}
