package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.Day;

public class DiagnosticTestSamplePickUpSlot {

	private Day day;
	
	private long slotDate;

    private List<PickUpSlot> slot;

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public long getSlotDate() {
		return slotDate;
	}

	public void setSlotDate(long slotDate) {
		this.slotDate = slotDate;
	}

	public List<PickUpSlot> getSlot() {
		return slot;
	}

	public void setSlot(List<PickUpSlot> slot) {
		this.slot = slot;
	}

	@Override
	public String toString() {
		return "DiagnosticTestSamplePickUpSlot [day=" + day + ", slotDate=" + slotDate + ", slot=" + slot + "]";
	}
}
