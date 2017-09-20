package com.dpdocter.beans;

public class Reminder {

	private String reminderType;
	
	private long reminderTime;

	public String getReminderType() {
		return reminderType;
	}

	public void setReminderType(String reminderType) {
		this.reminderType = reminderType;
	}

	public long getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(long reminderTime) {
		this.reminderTime = reminderTime;
	}

	@Override
	public String toString() {
		return "PatientReminder [reminderType=" + reminderType + ", reminderTime=" + reminderTime + "]";
	}
}
