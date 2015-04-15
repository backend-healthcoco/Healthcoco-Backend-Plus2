package com.dpdocter.beans;

import com.dpdocter.enums.DirectionEnum;

public class TemplateItem {
	private String drugId;
	private Duration duration;
	private String dosage;
	private DirectionEnum direction;
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

	public DirectionEnum getDirection() {
		return direction;
	}

	public void setDirection(DirectionEnum direction) {
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
		return "TemplateItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", direction=" + direction + ", instructions="
				+ instructions + "]";
	}

}
