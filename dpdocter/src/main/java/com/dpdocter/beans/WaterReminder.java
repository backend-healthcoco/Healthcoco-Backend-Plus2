package com.dpdocter.beans;

public class WaterReminder {

	private Boolean remind = false;
	
	private long remindAt;
	
	private long startTime;
	
	private long endTime;
	
	private Integer repeatValue = 0;
	
	private String repeatType;

	public Boolean getRemind() {
		return remind;
	}

	public void setRemind(Boolean remind) {
		this.remind = remind;
	}

	public long getRemindAt() {
		return remindAt;
	}

	public void setRemindAt(long remindAt) {
		this.remindAt = remindAt;
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
		return "WaterReminder [remind=" + remind + ", remindAt=" + remindAt + ", startTime=" + startTime + ", endTime="
				+ endTime + ", repeatValue=" + repeatValue + ", repeatType=" + repeatType + "]";
	}

}
