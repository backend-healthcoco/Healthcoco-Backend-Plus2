package com.dpdocter.beans;

import com.dpdocter.enums.TimeUnit;

public class SleepPattern {
	private WorkingHours hours;
	
	private TimeUnit timeType;
	
	private Integer noOfminute;

	public WorkingHours getHours() {
		return hours;
	}

	public void setHours(WorkingHours hours) {
		this.hours = hours;
	}

	public TimeUnit getTimeType() {
		return timeType;
	}

	public void setTimeType(TimeUnit timeType) {
		this.timeType = timeType;
	}

	public Integer getNoOfminute() {
		return noOfminute;
	}

	public void setNoOfminute(Integer noOfminute) {
		this.noOfminute = noOfminute;
	}
	
	
	
	
}
