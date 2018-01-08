package org.aimas.consert.utils;

import java.util.HashMap;
import java.util.Map;

import org.aimas.consert.model.content.ContextAssertion;
import org.apache.log4j.Logger;

public class AssertionLogger {
	private Logger logger;
	
	private Map<Class, Integer> insertionCounter = new HashMap<Class, Integer>();
	private Map<Class, Integer> deleteCounter = new HashMap<Class, Integer>();
	
	public AssertionLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void logAssertionInsert(ContextAssertion assertion) {
		String msg = "[INSERT] Assertion: " + assertion;
		
		logger.info(msg);
		if (insertionCounter.containsKey(assertion.getClass())) {
			int ct = insertionCounter.get(assertion.getClass());
			
			insertionCounter.put(getClass(), (ct + 1));
		}
		else {
			insertionCounter.put(assertion.getClass(), 1);
		}
	}
	
	public void logAssertionRemove(ContextAssertion assertion) {
		String msg = "[REMOVE] Assertion: " + assertion;
		
		logger.info(msg);
		if (deleteCounter.containsKey(assertion.getClass())) {
			int ct = deleteCounter.get(assertion.getClass());
			
			deleteCounter.put(getClass(), (ct + 1));
		}
		else {
			deleteCounter.put(assertion.getClass(), 1);
		}
	}
	
	
	public void printCounters() {
		for (Class k : insertionCounter.keySet()) {
			logger.info("[COUNT] INSERTION count for " + k.getSimpleName() + ": " + insertionCounter.get(k));
			
			if (deleteCounter.containsKey(k)) {
				logger.info("[COUNT] REMOVAL count for " + k.getSimpleName() + ": " + deleteCounter.get(k));
			}
		}
	}
}
