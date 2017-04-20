package org.aimas.consert.tests;

import org.kie.api.runtime.KieSession;

public class EngineRunner implements Runnable {
	private KieSession kSession;
	
	public EngineRunner(KieSession kSession) {
		this.kSession = kSession;
	}
	
	public void run() {
		kSession.fireUntilHalt();
	}
	
}
