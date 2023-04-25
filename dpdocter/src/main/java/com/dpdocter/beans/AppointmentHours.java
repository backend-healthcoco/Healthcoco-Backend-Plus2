package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AppointmentHours {

	private float from;

	private float to;

	public AppointmentHours() {
	}

	public AppointmentHours(float from, float to) {
		this.from = from;
		this.to = to;
	}

	public float getFrom() {
		return from;
	}

	public void setFrom(float from) {
		this.from = from;
	}

	public float getTo() {
		return to;
	}

	public void setTo(float to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "AppointmentHours [from=" + from + ", to=" + to + "]";
	}

}
