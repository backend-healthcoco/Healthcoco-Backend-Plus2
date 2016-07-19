package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DirectionEnum;

public class TemplateGetItem {
    private Drug drug;

    private Duration duration;

    private DosageWithTime dosage;

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

    public DosageWithTime getDosage() {
	return dosage;
    }

    public void setDosage(DosageWithTime dosage) {
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
	return "TemplateGetItem [drug=" + drug + ", duration=" + duration + ", dosage=" + dosage + ", direction=" + direction + ", instructions=" + instructions
		+ "]";
    }

}
