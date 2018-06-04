package org.aimas.consert.engine.api;

import org.aimas.consert.model.content.EntityDescription;

public interface EntityDescriptionListener {
	void notifyEntityDescriptionInserted(EntityDescription assertion);
	
	void notifyEntityDescriptionDeleted(EntityDescription assertion);
}