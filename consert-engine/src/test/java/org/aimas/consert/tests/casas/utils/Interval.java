package org.aimas.consert.tests.casas.utils;

/**
 * Copyright (c) 2004 Domain Language, Inc. (http://domainlanguage.com)
 * This free software is distributed under the "MIT" licence. See file licence.txt. 
 * For more information, see http://timeandmoney.sourceforge.net.
 */



import java.util.ArrayList;
import java.util.List;

/**
 * The rules of this class are consistent with the common mathematical
 * definition of "interval". For a simple explanation, see
 * http://en.wikipedia.org/wiki/Interval_(mathematics)
 * 
 * Interval (and its "ConcreteInterval" subclass) can be used for any objects
 * that have a natural ordering reflected by implementing the Comparable
 * interface. For example, Integer implements Comparable, so if you want to
 * check if an Integer is within a range, make an Interval. Any class of yours
 * which implements Comparable can have intervals defined this way.
 */
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>> {
	private IntervalLimit<T> lowerLimitObject;
    private IntervalLimit<T> upperLimitObject;
    
    public static <T extends Comparable<T>> Interval<T> closed(T lower, T upper) {
        return new Interval<T>(lower, true, upper, true);
    }

    public static <T extends Comparable<T>> Interval<T> open(T lower, T upper) {
        return new Interval<T>(lower, false, upper, false);
    }

    public static <T extends Comparable<T>> Interval<T> over(T lower, boolean lowerIncluded, T upper, boolean upperIncluded) {
        return new Interval<T>(lower, lowerIncluded, upper, upperIncluded);
    }

    Interval(IntervalLimit<T> lower, IntervalLimit<T> upper) {
        assertLowerIsLessThanOrEqualUpper(lower, upper);
        this.lowerLimitObject=lower;
        this.upperLimitObject=upper;
    }

    public Interval(T lower, boolean isLowerClosed, T upper, boolean isUpperClosed) {
        this(IntervalLimit.lower(isLowerClosed, lower), IntervalLimit.upper(isUpperClosed, upper));
    }
    
    //Warning: This method should generally be used for display
    //purposes and interactions with closely coupled classes.
    //Look for (or add) other methods to do computations.
    public T upperLimit() {
        return upperLimitObject.getValue();
    }

    //Warning: This method should generally be used for display
    //purposes and interactions with closely coupled classes.
    //Look for (or add) other methods to do computations.

    public boolean includesUpperLimit() {
        return upperLimitObject.isClosed();
    }

   //Warning: This method should generally be used for display
   //purposes and interactions with closely coupled classes.
   //Look for (or add) other methods to do computations.
    
    public boolean hasUpperLimit() {
        return upperLimit() != null;
    }
      
    //Warning: This method should generally be used for display
    //purposes and interactions with closely coupled classes.
    //Look for (or add) other methods to do computations.
    public T lowerLimit() {
        return lowerLimitObject.getValue();
    }
    
    //Warning: This method should generally be used for display
    //purposes and interactions with closely coupled classes.
    //Look for (or add) other methods to do computations.
    public boolean includesLowerLimit() {
        return lowerLimitObject.isClosed();
    }
    
    //Warning: This method should generally be used for display
    //purposes and interactions with closely coupled classes.
    //Look for (or add) other methods to do computations.
    public boolean hasLowerLimit() {
        return lowerLimit() != null;
    }
    

    public Interval<T> newOfSameType(T lower, boolean isLowerClosed, T upper, boolean isUpperClosed) {
        return new Interval<T>(lower,isLowerClosed,upper,isUpperClosed);
    }

    public Interval<T> emptyOfSameType() {
        return newOfSameType(lowerLimit(), false, lowerLimit(), false);
    }

    public boolean includes(T value) {
        return !this.isBelow(value) && !this.isAbove(value);
    }

    public boolean covers(Interval<T> other) {
    	boolean lowerPass = false;
    	boolean upperPass = false;
    	
    	if (hasLowerLimit() && !other.hasLowerLimit()) 
    		return false;
    	
    	if (hasUpperLimit() && !other.hasUpperLimit()) 
    		return false;
    	
    	if (!hasLowerLimit()) { 
    		lowerPass = true;
    	}
    	else {
    		int lowerComparison = lowerLimit().compareTo(other.lowerLimit());
            lowerPass = this.includes(other.lowerLimit()) || (lowerComparison == 0 && !other.includesLowerLimit());
    	}
    	
    	if (!hasUpperLimit()) {
    		upperPass = true;
    	}
    	else {
    		int upperComparison = upperLimit().compareTo(other.upperLimit());
            upperPass = this.includes(other.upperLimit()) || (upperComparison == 0 && !other.includesUpperLimit());
    	}
        
        return lowerPass && upperPass;
    }
    
    public boolean isAfter(Interval<T> other) {
    	if (!hasLowerLimit() || !other.hasUpperLimit()) return false;
    	
    	return lowerLimit().compareTo(other.upperLimit()) > 0;
    }
    
    
    public boolean isBefore(Interval<T> other) {
    	if (!hasUpperLimit() || !other.hasLowerLimit()) return false;
    	
    	return upperLimit().compareTo(other.lowerLimit()) < 0;
    }
    
    
    public boolean isOpen() {
        return !includesLowerLimit() && !includesUpperLimit();
    }

    public boolean isClosed() {
        return includesLowerLimit() && includesUpperLimit();
    }

    public boolean isEmpty() {
        //TODO: Consider explicit empty interval
        //A 'degenerate' interval is an empty set, {}.
    	if (upperLimit() != null && lowerLimit() != null) {
    		return isOpen() && upperLimit().equals(lowerLimit());
    	}
    	
    	return false;
    }

    public boolean isSingleElement() {
        if (!hasUpperLimit()) return false;
        if (!hasLowerLimit()) return false;
        //An interval containing a single element, {a}.
        return upperLimit().equals(lowerLimit()) && !isEmpty();
    }

    public boolean isBelow(T value) {
        if (!hasUpperLimit()) return false;
        
        if (value == null) return true;
        
        int comparison = upperLimit().compareTo(value);
        return comparison < 0 || (comparison == 0 && !includesUpperLimit());
    }

    public boolean isAbove(T value) {
        if (!hasLowerLimit()) return false;
        
        if (value == null) return true;
        
        int comparison = ((Comparable<T>) lowerLimit()).compareTo(value);
        return comparison > 0 || (comparison == 0 && !includesLowerLimit());
    }

    @Override
    public int compareTo(Interval<T> other) {
    	T lowerLimit = lowerLimit();
    	T upperLimit = upperLimit();
    	
    	T otherLowerLimit = other.lowerLimit();
    	T otherUpperLimit = other.upperLimit();
    	
    	if (lowerLimit != null && otherLowerLimit != null) {
    		if (!lowerLimit.equals(otherLowerLimit)) {
    			return lowerLimit.compareTo(otherLowerLimit);
    		}
    		else {
    			if (includesLowerLimit() && !other.includesLowerLimit())
    	            return -1;
    	        if (!includesLowerLimit() && other.includesLowerLimit())
    	            return 1;
    		}
    	}
    	
    	if (lowerLimit != null && otherLowerLimit == null) {
    		return 1;
    	}
    	
    	if (lowerLimit == null && otherLowerLimit != null) {
    		return -1;
    	}
    	
    	// we're done comparing based on the lower bounds (they are now both -inf); now we start with the upper ones
    	if (upperLimit == null && otherUpperLimit == null) {
    		return 0;
    	}
    	
    	if (upperLimit != null && otherUpperLimit == null) {
    		return -1;
    	}
    	
    	if (upperLimit == null && otherUpperLimit != null) {
    		return 1;
    	}
    	
    	if (upperLimit.equals(otherUpperLimit)) {
    		if (includesUpperLimit() && !other.includesUpperLimit())
	            return -1;
	        if (!includesUpperLimit() && other.includesUpperLimit())
	            return 1;
		}
    	
    	return upperLimit.compareTo(otherUpperLimit);
    }
    
    @Override
    public String toString() {
        if (isEmpty())
            return "{}";
        if (isSingleElement())
            return "{" + lowerLimit().toString() + "}";
        StringBuffer buffer = new StringBuffer();
        buffer.append(includesLowerLimit() ? "[" : "(");
        buffer.append(hasLowerLimit() ? lowerLimit().toString() : "Infinity");
        buffer.append(", ");
        buffer.append(hasUpperLimit() ? upperLimit().toString() : "Infinity");
        buffer.append(includesUpperLimit() ? "]" : ")");
        return buffer.toString();
    }

    private T lesserOfLowerLimits(Interval<T> other) {
        if (lowerLimit() == null || other.lowerLimit() == null) {
            return null;
        }
        
        int lowerComparison = lowerLimit().compareTo(other.lowerLimit());
        if (lowerComparison <= 0)
            return this.lowerLimit();
        return other.lowerLimit();
    }

    private T greaterOfLowerLimits(Interval<T> other) {
        if (lowerLimit() == null) {
            return other.lowerLimit();
        }
        
        if (other.lowerLimit() == null) {
        	return lowerLimit();
        }
        
        int lowerComparison = lowerLimit().compareTo(other.lowerLimit());
        if (lowerComparison >= 0)
            return this.lowerLimit();
        return other.lowerLimit();
    }

    private T lesserOfUpperLimits(Interval<T> other) {
        if (upperLimit() == null) {
            return other.upperLimit();
        }
        
        if (other.upperLimit() == null) {
        	return upperLimit();
        }
        
        int upperComparison = upperLimit().compareTo(other.upperLimit());
        if (upperComparison <= 0)
            return this.upperLimit();
        return other.upperLimit();
    }

    private T greaterOfUpperLimits(Interval<T> other) {
        if (upperLimit() == null || other.upperLimit() == null) {
            return null;
        }
        
        int upperComparison = upperLimit().compareTo(other.upperLimit());
        if (upperComparison >= 0)
            return this.upperLimit();
        return other.upperLimit();
    }

    private boolean greaterOfLowerIncludedInIntersection(Interval<T> other) {
        T limit = greaterOfLowerLimits(other);
        return this.includes(limit) && other.includes(limit);
    }

    private boolean lesserOfUpperIncludedInIntersection(Interval<T> other) {
        T limit = lesserOfUpperLimits(other);
        return this.includes(limit) && other.includes(limit);
    }

    private boolean greaterOfLowerIncludedInUnion(Interval<T> other) {
        T limit = greaterOfLowerLimits(other);
        return this.includes(limit) || other.includes(limit);
    }

    private boolean lesserOfUpperIncludedInUnion(Interval<T> other) {
        T limit = lesserOfUpperLimits(other);
        return this.includes(limit) || other.includes(limit);
    }
    
    
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Interval<?>)) return false;
        Interval<T> other = (Interval<T>)obj;
        
        boolean thisEmpty = this.isEmpty();
        boolean otherEmpty = other.isEmpty();
        if (thisEmpty & otherEmpty)
            return true;
        
        if (thisEmpty ^ otherEmpty)
            return false;

        boolean thisSingle = this.isSingleElement();
        boolean otherSingle = other.isSingleElement();
        
        if (thisSingle & otherSingle)
            return this.lowerLimit().equals(other.lowerLimit());
        
        if (thisSingle ^ otherSingle)
            return false;

        return compareTo(other) == 0;
    }

    public int hashCode() {
        return lowerLimit().hashCode() ^ upperLimit().hashCode();
    }

    public boolean intersects(Interval<T> other) {
    	T greaterOfLowerLimits = greaterOfLowerLimits(other);
    	T lesserOfUpperLimits = lesserOfUpperLimits(other);
    	
    	if (greaterOfLowerLimits != null && lesserOfUpperLimits != null) {
	        int comparison = greaterOfLowerLimits.compareTo(lesserOfUpperLimits);
	        if (comparison < 0)
	            return true;
	        if (comparison > 0)
	            return false;
	        return greaterOfLowerIncludedInIntersection(other) && lesserOfUpperIncludedInIntersection(other);
    	}
    	else {
    		if (greaterOfLowerLimits != null)
    			return greaterOfLowerIncludedInIntersection(other);
    		
    		if (lesserOfUpperLimits != null)
    			return lesserOfUpperIncludedInIntersection(other);
    	}
    	
    	return false;
    }

    public Interval<T> intersect(Interval<T> other) {
        T intersectLowerBound = greaterOfLowerLimits(other);
        T intersectUpperBound = lesserOfUpperLimits(other);
        
        if (intersectLowerBound != null && intersectUpperBound != null) {
	        if (intersectLowerBound.compareTo(intersectUpperBound) > 0)
	            return emptyOfSameType();
        }
	    
        return newOfSameType(intersectLowerBound, greaterOfLowerIncludedInIntersection(other), 
        		intersectUpperBound, lesserOfUpperIncludedInIntersection(other));
    }

    public Interval<T> gap(Interval<T> other) {
        if (this.intersects(other))
            return this.emptyOfSameType();

        return newOfSameType(lesserOfUpperLimits(other), !lesserOfUpperIncludedInUnion(other), greaterOfLowerLimits(other), !greaterOfLowerIncludedInUnion(other));
    }

    
    public List<Interval<T>> complementRelativeTo(Interval<T> other) {
        List<Interval<T>> intervalSequence = new ArrayList<Interval<T>>();
        if (!this.intersects(other)) {
            intervalSequence.add(other);
            return intervalSequence;
        }
        
        Interval<T> left = leftComplementRelativeTo(other);
        if (left != null)
            intervalSequence.add(left);
        
        Interval<T> right = rightComplementRelativeTo(other);
        if (right != null)
            intervalSequence.add(right);
        return intervalSequence;
    }
    
    
    public Interval<T> extend(Interval<T> other) {
    	T newLowerLimit = lesserOfLowerLimits(other);
    	T newUpperLimit = greaterOfUpperLimits(other);
    	
    	boolean lowerClosed = false;
    	if (newLowerLimit == lowerLimitObject.getValue() && lowerLimitObject.isClosed()) {
    		lowerClosed = true;
    	}
    	else if (newLowerLimit == other.lowerLimit() && other.includesLowerLimit()) {
    		lowerClosed = true;
    	}
    	
    	boolean upperClosed = false;
    	if (newUpperLimit == upperLimitObject.getValue() && upperLimitObject.isClosed()) {
    		upperClosed = true;
    	}
    	else if (newUpperLimit == other.upperLimit() && other.includesUpperLimit()) {
    		upperClosed = true;
    	}
    	
    	return newOfSameType(newLowerLimit, lowerClosed, newUpperLimit, upperClosed);
    }
    
    
    public Interval<T> leftComplementRelativeTo(Interval<T> other) {
    	
    	//if (this.includes(lesserOfLowerLimits(other)))
        //    return null;
    	if (lowerLimit().equals(lesserOfLowerLimits(other)))
    		return null;
        if (lowerLimit().equals(other.lowerLimit()) && !other.includesLowerLimit())
            return null;
        return newOfSameType(other.lowerLimit(), other.includesLowerLimit(), this.lowerLimit(), !this.includesLowerLimit());
    }

    public Interval<T> rightComplementRelativeTo(Interval<T> other) {
        //if (this.includes(greaterOfUpperLimits(other)))
        //    return null;
        if (upperLimit().equals(greaterOfUpperLimits(other)))
        	return null;
    	if (upperLimit().equals(other.upperLimit()) && !other.includesUpperLimit())
            return null;
        return newOfSameType(this.upperLimit(), !this.includesUpperLimit(), other.upperLimit(), other.includesUpperLimit());
    }
    
    private void assertLowerIsLessThanOrEqualUpper(IntervalLimit<T> lower, IntervalLimit<T> upper) {
        if (!(lower.isLower() && upper.isUpper() && lower.compareTo(upper) <= 0)) {
            throw new IllegalArgumentException(lower + " is not before or equal to " + upper);
        }
    }
    
}