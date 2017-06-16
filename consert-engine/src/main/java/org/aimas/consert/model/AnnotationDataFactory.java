package org.aimas.consert.model;

public interface AnnotationDataFactory {
	DefaultAnnotationData getForClass(Class<? extends ContextAssertion> clazz);
}
