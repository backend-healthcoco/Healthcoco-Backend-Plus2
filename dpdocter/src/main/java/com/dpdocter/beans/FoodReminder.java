package com.dpdocter.beans;

import java.util.List;

public class FoodReminder {

	private Boolean remind = false;
	
	private long remindAt;
	
	private List<Reminder> reminders;

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

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	@Override
	public String toString() {
		return "FoodReminder [remind=" + remind + ", remindAt=" + remindAt + ", reminders=" + reminders + "]";
	}
}
