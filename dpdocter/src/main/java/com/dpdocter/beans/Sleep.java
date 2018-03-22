package com.bean;

import java.util.Date;

import com.Enum.SleepWhen;

public class Sleep {
	
	private Duration2 duration;
	private Date SleepFrom;
	private Date SleepTo;
	private SleepWhen sleepWhen;
	
	
	public Duration2 getDuration() {
		return duration;
	}
	
	public void setDuration(Duration2 duration) {
		 long diff = SleepTo.getTime() - SleepFrom.getTime();
	     
	     long diffMinutes = diff / (60 * 1000) % 60;
	     long diffHours = diff / (60 * 60 * 1000);
		 duration.setHours((int)diffHours);
		 duration.setMinutes((int)diffMinutes);
	}
	
	
	public SleepWhen getsleepWhen() {
		return sleepWhen;
	}
	
	public void setsleepWhen(SleepWhen sleepwhen) {
		this.sleepWhen = sleepwhen;
	}
	
	public Date getSleepFrom() {
		return SleepFrom;
	}
	
	public void setSleepFrom(Date sleepFrom) {
		SleepFrom = sleepFrom;
	}
	
	public Date getSleepTo() {
		return SleepTo;
	}
	
	public void setSleepTo(Date sleepTo) {
		SleepTo = sleepTo;
	}

	@Override
	public String toString() {
		return "Sleep [duration=" + duration + ", SleepFrom=" + SleepFrom + ", SleepTo=" + SleepTo + ", sleepWhen="
				+ sleepWhen + "]";
	}
	
	

}
