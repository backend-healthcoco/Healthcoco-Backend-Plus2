package com.dpdocter.beans;

public class LocaleWorkingHours {
	private Long fromTime;

	private Long toTime;

	public Long getFromTime() {
		return fromTime;
	}

	public void setFromTime(Long fromTime) {
		this.fromTime = fromTime;
	}

	public Long getToTime() {
		return toTime;
	}

	public void setToTime(Long toTime) {
		this.toTime = toTime;
	}

	@Override
	public String toString() {
		return "WorkingHours [fromTime=" + fromTime + ", toTime=" + toTime + "]";
	}

}
