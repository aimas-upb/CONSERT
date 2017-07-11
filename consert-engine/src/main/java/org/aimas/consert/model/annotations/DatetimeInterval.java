package org.aimas.consert.model.annotations;

import java.util.Date;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces("annotation = " + Constants.ANNOTATION_NS)
@RDFBean("annotation:DatetimeInterval")
public class DatetimeInterval {
	private static long instanceCt = 1;
	private String instanceId;
	
	private Date start;
	private Date end;
	
	public DatetimeInterval() {
		// TODO Auto-generated constructor stub
	}

	public DatetimeInterval(Date start, Date end) {
	    this.start = start;
	    this.end = end;
    }
	
	@RDFSubject
	public String getInstanceId() {
		if (instanceId == null) {
			instanceId = Constants.ANNOTATION_BASE_URI + "DatetimeInterval#Interval" + (instanceCt++); 
		}
		
		return instanceId;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	@RDF("annotation:startTime")
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}
	
	@RDF("annotation:endTime")
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {		
		this.end = end;
	}
}
