package com.dpdocter.beans;

import java.util.List;

public class ClinicTiming {
	private String id;

	private List<WorkingSchedule> workingSchedules;

	private boolean isTwentyFourSevenOpen;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	}

	public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
	}

	public boolean isTwentyFourSevenOpen() {
		return isTwentyFourSevenOpen;
	}

	public void setTwentyFourSevenOpen(boolean isTwentyFourSevenOpen) {
		this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
	}

	@Override
	public String toString() {
		return "ClinicTiming [id=" + id + ", workingSchedules=" + workingSchedules + ", isTwentyFourSevenOpen=" + isTwentyFourSevenOpen + "]";
	}

}
