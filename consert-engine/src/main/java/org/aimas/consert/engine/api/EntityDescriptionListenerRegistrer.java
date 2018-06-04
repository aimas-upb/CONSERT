package org.aimas.consert.engine.api;

public interface EntityDescriptionListenerRegistrer {
	
	void addFactListener(EntityDescriptionListener factListener);
	
	void removeFactListener(EntityDescriptionListener factListener);
}
