package org.aimas.consert.engine.api;

import org.aimas.consert.model.content.ContextAssertion;

public interface ChangePointListener {
	void notifyChangePointAdded(ContextAssertion assertion);
}
