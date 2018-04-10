package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.enums.SleepWhen;

public class Sleep {
	
	private Duration2 duration;
	private Date sleepFrom;
	private Date sleepTo;
	private SleepWhen sleepWhen;
	
	public Duration2 getDuration() {
		return duration;
	}
	public void setDuration(Duration2 duration) {
		this.duration = duration;
	}
	public Date getSleepFrom() {
		return sleepFrom;
	}
	public void setSleepFrom(Date sleepFrom) {
		this.sleepFrom = sleepFrom;
	}
	public Date getSleepTo() {
		return sleepTo;
	}
	public void setSleepTo(Date sleepTo) {
		this.sleepTo = sleepTo;
	}
	public SleepWhen getSleepWhen() {
		return sleepWhen;
	}
	public void setSleepWhen(SleepWhen sleepWhen) {
		this.sleepWhen = sleepWhen;
	}
	
	@Override
	public String toString() {
		return "Sleep [duration=" + duration + ", sleepFrom=" + sleepFrom + ", sleepTo=" + sleepTo + ", sleepWhen="
				+ sleepWhen + "]";
	}
	

	
	
	

}
