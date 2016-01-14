package com.dpdocter.beans;

public class WorkingHours {
    private Integer from;

    private Integer to;

	public Integer getFrom() {
	return from;
    }

    public void setFrom(Integer from) {
	this.from = from;
    }

    public Integer getTo() {
	return to;
    }

    public void setTo(Integer to) {
	this.to = to;
    }

    @Override
    public String toString() {
	return "{from=" + from + ", to=" + to + "}";
    }
}
