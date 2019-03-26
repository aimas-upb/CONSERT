package org.aimas.consert.model.content;

import java.util.Map;

public class ContextAssertionContent {
	
	private ContextAssertion assertion;
	
	public ContextAssertionContent(ContextAssertion assertion) {
		this.assertion = assertion.cloneContent();
	}
	
	public String getType() {
		return assertion.getClass().getName();
	}
	
	public int getArity() {
		return assertion.getAssertionArity();
	}
	
	public String getAcquisitionType() {
		return assertion.getAcquisitionType().name();
	}
	
	public Map<String, ContextEntity> getEntities() {
		return assertion.getEntities();
	}
	
	
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((assertion == null) ? 0 : assertion.hashCode());
	    return result;
    }

	
    @Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    
	    ContextAssertionContent other = (ContextAssertionContent) obj;
	    
	    if (assertion == null) {
		    if (other.assertion != null)
			    return false;
	    }
	    else 
	    	if (!assertion.equals(other.assertion))
	    		return false;
	    
	    return true;
    }
	
	
	
}
