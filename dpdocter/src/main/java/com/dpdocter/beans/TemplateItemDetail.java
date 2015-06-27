package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DirectionEnum;

public class TemplateItemDetail {
	private Drug drug;

	private Duration duration;

	private String dosage;

	private List<DirectionEnum> direction;

	private String instructions;

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
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

	public List<DirectionEnum> getDirection() {
		return direction;
	}

	public void setDirection(List<DirectionEnum> direction) {
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
		return "TemplateItemHandheld [drug=" + drug + ", duration=" + duration + ", dosage=" + dosage + ", direction=" + direction + ", instructions="
				+ instructions + "]";
	}

}
