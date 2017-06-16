package org.aimas.consert.engine;

import java.util.EventListener;

public interface AtomicEventListener extends EventListener {
	void eventInserted(AtomicEventInserted event);
}
