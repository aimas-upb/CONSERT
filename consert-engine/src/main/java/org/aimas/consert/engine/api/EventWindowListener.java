package org.aimas.consert.engine.api;

import org.aimas.consert.model.eventwindow.EventWindow;

public interface EventWindowListener {
	void notifyEventWindowSubmitted(EventWindow eventWindow);
}
