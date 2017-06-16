package org.aimas.consert.model;

import java.util.Calendar;


public class ValidityInterval {
	
	private Calendar start;
	private Calendar end;
	
	public ValidityInterval(Calendar start, Calendar end) {
		this.start = start;
		this.end = end;
	}

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
		this.end = end;
	}
}
