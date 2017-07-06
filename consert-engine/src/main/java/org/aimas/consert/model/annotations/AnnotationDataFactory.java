package org.aimas.consert.model.annotations;

import org.aimas.consert.model.content.ContextAssertion;

public interface AnnotationDataFactory {
	DefaultAnnotationData getForClass(Class<? extends ContextAssertion> clazz);
}
