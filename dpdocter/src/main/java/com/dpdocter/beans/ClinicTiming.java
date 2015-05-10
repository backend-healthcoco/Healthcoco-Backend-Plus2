package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.Day;

public class ClinicTiming {
	private String locationId;
	private List<Day> workingDays;
	private List<WorkingHours> workingSession;
	private boolean isTwentyFourSevenOpen;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<Day> getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(List<Day> workingDays) {
		this.workingDays = workingDays;
	}

	public List<WorkingHours> getWorkingSession() {
		return workingSession;
	}

	public void setWorkingSession(List<WorkingHours> workingSession) {
		this.workingSession = workingSession;
	}

	public boolean isTwentyFourSevenOpen() {
		return isTwentyFourSevenOpen;
	}

	public void setTwentyFourSevenOpen(boolean isTwentyFourSevenOpen) {
		this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
	}

	@Override
	public String toString() {
		return "ClinicTiming [workingDays=" + workingDays + ", workingSession=" + workingSession + ", isTwentyFourSevenOpen=" + isTwentyFourSevenOpen + "]";
	}

}
