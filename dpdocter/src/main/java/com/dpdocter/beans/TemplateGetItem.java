package com.dpdocter.beans;

import com.dpdocter.enums.DirectionEnum;
import com.dpdocter.response.DrugAddEditResponse;

public class TemplateGetItem {
	private DrugAddEditResponse drug;
	private Duration duration;
	private String dosage;
	private DirectionEnum direction;
	private String instructions;

	public DrugAddEditResponse getDrug() {
		return drug;
	}

	public void setDrug(DrugAddEditResponse drug) {
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
		return "TemplateGetItem [drug=" + drug + ", duration=" + duration + ", dosage=" + dosage + ", direction=" + direction + ", instructions="
				+ instructions + "]";
	}

}
