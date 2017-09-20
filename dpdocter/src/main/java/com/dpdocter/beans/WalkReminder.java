package com.dpdocter.beans;

public class WalkReminder {

	private Boolean remindAtNine = false;

	public Boolean getRemindAtNine() {
		return remindAtNine;
	}

	public void setRemindAtNine(Boolean remindAtNine) {
		this.remindAtNine = remindAtNine;
	}

	@Override
	public String toString() {
		return "WalkReminder [remindAtNine=" + remindAtNine + "]";
	}
}
