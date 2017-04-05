package org.aimas.consert.eventmodel;
import java.util.Calendar;

/*
 * Class for modeling context information and metadata
 * when an atomic event arrives.
 */
public class ContextInfo
{
    double lastUpdated; /* last Updated time*/
    double confidence;  /* confidence for the event */
    Calendar startTime; /* start time of the event */
    Calendar endTime; /* end time of the event */

    public double getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(double lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString()
    {
        return lastUpdated + ", " + confidence + ", " + startTime.getTimeInMillis() + ", "
                + endTime.getTimeInMillis();
    }

}
