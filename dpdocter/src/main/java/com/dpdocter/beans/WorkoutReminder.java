package com.dpdocter.beans;

public class WorkoutReminder {

	private Boolean remindAtNine = false;

	public Boolean getRemindAtNine() {
		return remindAtNine;
	}

	public void setRemindAtNine(Boolean remindAtNine) {
		this.remindAtNine = remindAtNine;
	}

	@Override
	public String toString() {
		return "WorkoutReminder [remindAtNine=" + remindAtNine + "]";
	}
}
