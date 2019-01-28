package org.aimas.consert.utils;

public class TimestampPair {
	
	private long start;
	private long end;
	
	public TimestampPair(long start, long end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(long end) {
		this.end = end;
	}
	
	@Override
	public String toString() {
		return "[" + start + ", " + end + "]";
	}
	
	public static TimestampPair parseString(String str) {
		String[] parts = str.substring(1, str.length() - 1).split(",");
		
		long start = Long.parseLong(parts[0].trim());
		long end = Long.parseLong(parts[1].trim());
		
		return new TimestampPair(start, end);
	}
}
