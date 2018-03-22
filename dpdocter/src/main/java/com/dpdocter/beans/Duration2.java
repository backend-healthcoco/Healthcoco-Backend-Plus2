package com.bean;

public class Duration2 {
	
	private  Integer hours;
	private  Integer minutes;
	
	public Integer getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public Integer getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	@Override
	public String toString() {
		return "Duration [hours=" + hours + ", minutes=" + minutes + "]";
	}
	
}
