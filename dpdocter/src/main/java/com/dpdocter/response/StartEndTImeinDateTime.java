package com.dpdocter.response;

import org.joda.time.DateTime;

public class StartEndTImeinDateTime {
	private DateTime startTime;
	private DateTime endTime;

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "StartEndTImeinDateTime [startTime=" + startTime + ", endTime=" + endTime + "]";
	}

}
