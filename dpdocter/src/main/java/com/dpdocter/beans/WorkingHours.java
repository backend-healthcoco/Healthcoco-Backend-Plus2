package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class WorkingHours {
	private Integer fromTime;

	private Integer toTime;

	public Integer getFromTime() {
		return fromTime;
	}

	public void setFromTime(Integer fromTime) {
		this.fromTime = fromTime;
	}

	public Integer getToTime() {
		return toTime;
	}

	public void setToTime(Integer toTime) {
		this.toTime = toTime;
	}

	@Override
	public String toString() {
		return "WorkingHours [fromTime=" + fromTime + ", toTime=" + toTime + "]";
	}

}
