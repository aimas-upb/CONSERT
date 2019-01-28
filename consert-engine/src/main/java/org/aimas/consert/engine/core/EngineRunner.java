package org.aimas.consert.engine.core;

import org.kie.api.runtime.KieSession;

public class EngineRunner implements Runnable {
	private KieSession kSession;
	
	public EngineRunner(KieSession kSession) {
		this.kSession = kSession;
	}
	
	@Override
	public void run() {
		kSession.fireUntilHalt();
		
//		if (Thread.currentThread().isInterrupted()) {
//			Thread.currentThread().destroy();
//		}
	}
	
}
