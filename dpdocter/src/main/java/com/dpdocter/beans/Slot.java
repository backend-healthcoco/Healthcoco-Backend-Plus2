package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Slot {

	private String time;

	private Boolean isAvailable = true;

	private Integer minutesOfDay;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Integer getMinutesOfDay() {
		return minutesOfDay;
	}

	public void setMinutesOfDay(Integer minutesOfDay) {
		this.minutesOfDay = minutesOfDay;
	}

	@Override
	public boolean equals(Object object) {
		return (this.time.equals(((Slot) object).time));
	}

	@Override
	public String toString() {
		return "Slot [time=" + time + ", isAvailable=" + isAvailable + ", minutesOfDay=" + minutesOfDay + "]";
	}

//	@Override
//	public int hashCode() {
//		// TODO Auto-generated method stub
//		return super.hashCode();
//	}
}
