package com.dpdocter.beans;

import java.util.List;

public class DosageWithTime {

	private String dosage;
	
	private List<Long> dosageTime;

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
	}

	@Override
	public String toString() {
		return "DosageWithTime [dosage=" + dosage + ", dosageTime=" + dosageTime + "]";
	}
}
