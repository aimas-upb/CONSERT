package org.aimas.consert.utils;

/**
 * Copyright (c) 2005 Domain Language, Inc. (http://domainlanguage.com) This
 * free software is distributed under the "MIT" licence. See file licence.txt.
 * For more information, see http://timeandmoney.sourceforge.net.
 */



public class IntervalLimit<T extends Comparable<T>> implements Comparable<IntervalLimit<T>> {
    private boolean closed;
    private T value;
    private boolean lower;
    
    static <T extends Comparable<T>> IntervalLimit<T> upper(boolean closed, T value) {
        return new IntervalLimit<T>(closed, false, value);
    }
    
    static <T extends Comparable<T>> IntervalLimit<T> lower(boolean closed, T value) {
        return new IntervalLimit<T>(closed, true, value);
    }
    
    public IntervalLimit(boolean closed, boolean lower, T value) {
        super();
        
        this.closed = closed;
        this.lower = lower;
        this.value = value;
    }
    
    boolean isLower() {
        return lower;
    }
    boolean isUpper() {
        return !lower;
    }
    boolean isClosed() {
        return closed;
    }
    boolean isOpen() {
        return !closed;
    }
    
    T getValue() {
        return value;
    }
    
    public int compareTo(IntervalLimit<T> other) {
        T otherValue=other.value;
        
        if (otherValue == value) return 0;
        if (value == null) {
            return lower ? -1 : 1;
        }
        
        if (otherValue == null) {
            return other.lower ? 1 : -1;
        }
        
        return value.compareTo(otherValue);
    }
    
    @Override
    public String toString() {
    	String str = "";
    	if (lower) {
    		if (closed) 
    			str += "[" + value;
    		else 
    			str += "(" + value;
    	}
    	else {
    		if (closed)
    			str += value + "]";
    		else 
    			str += value + ")";
    	}
    	
    	return str;
    }
}
