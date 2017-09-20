package com.dpdocter.beans;

public class WaterReminder {

	private Boolean remindAtNine = false;
	
	private long startTime;
	
	private long endTime;
	
	private Integer repeatValue = 0;
	
	private String repeatType;

	public Boolean getRemindAtNine() {
		return remindAtNine;
	}

	public void setRemindAtNine(Boolean remindAtNine) {
		this.remindAtNine = remindAtNine;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public Integer getRepeatValue() {
		return repeatValue;
	}

	public void setRepeatValue(Integer repeatValue) {
		this.repeatValue = repeatValue;
	}

	public String getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(String repeatType) {
		this.repeatType = repeatType;
	}

	@Override
	public String toString() {
		return "WaterReminder [remindAtNine=" + remindAtNine + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", repeatValue=" + repeatValue + ", repeatType=" + repeatType + "]";
	}
}
