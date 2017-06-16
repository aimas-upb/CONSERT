package org.aimas.consert.model;

public class DefaultAnnotationDataFactory implements AnnotationDataFactory {

	@Override
    public DefaultAnnotationData getForClass(Class<? extends ContextAssertion> clazz) {
	    return new DefaultAnnotationData();
    }
	
}
