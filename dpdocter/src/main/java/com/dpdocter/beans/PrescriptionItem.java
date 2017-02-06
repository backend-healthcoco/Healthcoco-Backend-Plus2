package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public class PrescriptionItem {
	private ObjectId drugId;

	private Duration duration;

	private String dosage;

	private List<Long> dosageTime;

	private List<DrugDirection> direction;

	private String instructions;

	@Transient
	private Long arrayIndex1;
	
	public ObjectId getDrugId() {
		return drugId;
	}

	public void setDrugId(ObjectId drugId) {
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

	public Long getArrayIndex1() {
		return arrayIndex1;
	}

	public void setArrayIndex1(Long arrayIndex1) {
		this.arrayIndex1 = arrayIndex1;
	}

	@Override
	public String toString() {
		return "PrescriptionItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", instructions=" + instructions + ", arrayIndex1="
				+ arrayIndex1 + "]";
	}

}
