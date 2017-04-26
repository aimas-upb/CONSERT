package org.aimas.consert.eventmodel;

import java.util.EventListener;

public interface AtomicEventListener extends EventListener {
	void eventInserted(AtomicEventInserted event);
}
