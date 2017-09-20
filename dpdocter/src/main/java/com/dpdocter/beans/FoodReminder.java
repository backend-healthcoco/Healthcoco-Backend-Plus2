package com.dpdocter.beans;

import java.util.List;

public class FoodReminder {

	private Boolean remindAtNine = false;
	
	private List<Reminder> reminders;

	public Boolean getRemindAtNine() {
		return remindAtNine;
	}

	public void setRemindAtNine(Boolean remindAtNine) {
		this.remindAtNine = remindAtNine;
	}

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	@Override
	public String toString() {
		return "FoodReminder [remindAtNine=" + remindAtNine + ", reminders=" + reminders + "]";
	}
}
