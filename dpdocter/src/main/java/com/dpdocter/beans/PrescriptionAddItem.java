package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PrescriptionAddItem {
    private String drugId;

    private Duration duration;

    private String dosage;

    private List<Long> dosageTime;
    
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

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
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
		return "PrescriptionItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", instructions=" + instructions + "]";
	}
}
