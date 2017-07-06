package org.aimas.consert.model.content;


public interface ContextEntity {
	
	String getEntityId();
	
	boolean isLiteral();
	
	Object getValue();
}
