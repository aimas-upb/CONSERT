package org.aimas.consert.engine.api;

public interface EventWindowListenerRegistrer {
	
	void addEventWindowListener(EventWindowListener eventWindowListener);
	
	void removeEventWindowListener(EventWindowListener updateListener);
	
}
