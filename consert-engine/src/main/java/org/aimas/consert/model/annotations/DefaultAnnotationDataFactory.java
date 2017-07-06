package org.aimas.consert.model.annotations;

import org.aimas.consert.model.content.ContextAssertion;

public class DefaultAnnotationDataFactory implements AnnotationDataFactory {

	@Override
    public DefaultAnnotationData getForClass(Class<? extends ContextAssertion> clazz) {
	    return new DefaultAnnotationData();
    }
	
}
