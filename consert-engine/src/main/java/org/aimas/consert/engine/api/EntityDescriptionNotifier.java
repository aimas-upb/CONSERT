package org.aimas.consert.engine.api;

import java.util.LinkedList;
import java.util.List;

import org.aimas.consert.model.content.EntityDescription;

public class EntityDescriptionNotifier {
	
	private static EntityDescriptionNotifier instance = null;
	
	private List<EntityDescriptionListener> registeredListeners;
	
	private EntityDescriptionNotifier() {
		registeredListeners = new LinkedList<EntityDescriptionListener>();
	}
	
	public void addfactListener(EntityDescriptionListener factListener) {
		synchronized(registeredListeners) {
			registeredListeners.add(factListener);
		}
	}
	
	public void removefactListener(EntityDescriptionListener factListener) {
		synchronized(registeredListeners) {
			registeredListeners.remove(factListener);
		}
	}
	
	public void notifyFactInserted(EntityDescription entityDescription) {
		synchronized(registeredListeners) {
			for (EntityDescriptionListener factListener : registeredListeners) {
				factListener.notifyEntityDescriptionInserted(entityDescription);
			}
		}
	}
	
	
	public void notifyFactDeleted(EntityDescription entityDescription) {
		synchronized(registeredListeners) {
			for (EntityDescriptionListener factListener : registeredListeners) {
				factListener.notifyEntityDescriptionDeleted(entityDescription);
			}
		}
	}
	
	public static EntityDescriptionNotifier getNewInstance() {
		//if (instance == null) {
		//	instance = new ContextAssertionNotifier();
		//}
		
		//return instance;
		return new EntityDescriptionNotifier();
	}
}
