package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PickUpSlot {

	private Boolean isAvailable = true;

	private long fromTime;

	private long toTime;

	private Integer noOfAppointmentsAllowed = 0;

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public long getFromTime() {
		return fromTime;
	}

	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}

	public Integer getNoOfAppointmentsAllowed() {
		return noOfAppointmentsAllowed;
	}

	public void setNoOfAppointmentsAllowed(Integer noOfAppointmentsAllowed) {
		this.noOfAppointmentsAllowed = noOfAppointmentsAllowed;
	}

	@Override
	public String toString() {
		return "PickUpSlot [isAvailable=" + isAvailable + ", fromTime=" + fromTime + ", toTime=" + toTime
				+ ", noOfAppointmentsAllowed=" + noOfAppointmentsAllowed + "]";
	}
}
