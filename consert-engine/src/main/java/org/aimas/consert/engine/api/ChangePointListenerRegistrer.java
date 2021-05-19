package org.aimas.consert.engine.api;

public interface ChangePointListenerRegistrer {
	
	void addChangePointListener(ChangePointListener changePointListener);
	
	void removeChangePointListener(ChangePointListener changePointListener);
	
}
