package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DirectionEnum;

public class PrescriptionItem {
	private String drugId;

	private Duration duration;

	private String dosage;

	private List<DrugDirection> direction;

	private String instructions;

	public String getDrugId() {
		return drugId;
	}

	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public List<DrugDirection> getDirection() {
		return direction;
	}

	public void setDirection(List<DrugDirection> direction) {
		this.direction = direction;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	@Override
	public String toString() {
		return "PrescriptionItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", direction=" + direction + ", instructions="
				+ instructions + "]";
	}

}
