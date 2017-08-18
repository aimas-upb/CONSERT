package org.aimas.consert.engine.api;

public interface ContextAssertionListenerRegistrer {
	
	void addEventListener(ContextAssertionListener eventListener);
	
	void removeEventListener(ContextAssertionListener updateListener);
}
