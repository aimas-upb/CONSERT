package org.aimas.consert.engine.api;

import org.aimas.consert.model.content.ContextAssertion;

public interface ContextAssertionListener {
	void notifyAssertionInserted(ContextAssertion assertion);
	
	void notifyAssertionDeleted(ContextAssertion assertion);
}
