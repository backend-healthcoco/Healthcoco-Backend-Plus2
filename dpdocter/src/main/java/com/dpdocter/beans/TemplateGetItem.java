package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DirectionEnum;

public class TemplateGetItem {
	private TemplateDrug drug;

	private Duration duration;

	private String dosage;

	private List<DirectionEnum> direction;

	private String instructions;

	public TemplateDrug getDrug() {
		return drug;
	}

	public void setDrug(TemplateDrug drug) {
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
		return "TemplateGetItem [drug=" + drug + ", duration=" + duration + ", dosage=" + dosage + ", direction=" + direction + ", instructions="
				+ instructions + "]";
	}

}
