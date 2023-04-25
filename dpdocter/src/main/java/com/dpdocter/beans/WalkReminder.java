package com.dpdocter.beans;

public class WalkReminder {

	private Boolean remind = false;

	private long remindAt;

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

	@Override
	public String toString() {
		return "WalkReminder [remind=" + remind + ", remindAt=" + remindAt + "]";
	}

}
