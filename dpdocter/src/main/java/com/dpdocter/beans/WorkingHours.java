package com.dpdocter.beans;

public class WorkingHours {
    private Timing from;

    private Timing to;

    public Timing getFrom() {
	return from;
    }

    public void setFrom(Timing from) {
	this.from = from;
    }

    public Timing getTo() {
	return to;
    }

    public void setTo(Timing to) {
	this.to = to;
    }

    @Override
    public String toString() {
	return "WorkingHours [from=" + from + ", to=" + to + "]";
    }

}
