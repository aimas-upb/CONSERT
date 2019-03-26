package org.aimas.consert.engine.api;

public interface ContextAssertionListenerRegistrer {
	
	void addContextAssertionListener(ContextAssertionListener eventListener);
	
	void removeContextAssertionListener(ContextAssertionListener updateListener);
}
