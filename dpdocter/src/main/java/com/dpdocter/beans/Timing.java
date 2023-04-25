package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.Period;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Timing {
	private String hour;

	private Period period;

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "Timing [hour=" + hour + ", period=" + period + "]";
	}

}
