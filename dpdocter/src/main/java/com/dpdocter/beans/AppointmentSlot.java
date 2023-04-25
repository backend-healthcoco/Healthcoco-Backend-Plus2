package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.TimeUnit;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AppointmentSlot {
	private float time = 15;

	private TimeUnit timeUnit = TimeUnit.MINS;

	public AppointmentSlot(float time, TimeUnit timeUnit) {
		this.time = time;
		this.timeUnit = timeUnit;
	}

	public AppointmentSlot() {
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	@Override
	public String toString() {
		return "AppointmentSlot [time=" + time + ", timeUnit=" + timeUnit + "]";
	}

}
