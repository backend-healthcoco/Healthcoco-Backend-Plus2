package com.dpdocter.beans;

public class TimeDuration {

	private Integer seconds;
	private Integer minutes;
	private Integer hours;
	private Integer days;

	public Integer getSeconds() {
		return seconds;
	}

	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "TimeDuration [seconds=" + seconds + ", minutes=" + minutes + ", hours=" + hours + ", days=" + days
				+ "]";
	}

}
