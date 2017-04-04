package com.dpdocter.response;

public class StartEndTimeinMillis {

	private Long startTime;
	private Long endTime;

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "StartEndTimeinMillis [startTime=" + startTime + ", endTime=" + endTime + "]";
	}

}
