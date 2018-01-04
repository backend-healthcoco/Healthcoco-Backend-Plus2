package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public class PrescriptionItem {
	private ObjectId drugId;

	private Duration duration;

	private String dosage;
	
	private DrugType drugType;

	private String drugName;

	private String explanation;

	private List<Long> dosageTime;

	private List<DrugDirection> direction;

	private String instructions;

	private Long inventoryQuantity;
	 
	public PrescriptionItem() {
		super();
	}

	public PrescriptionItem(ObjectId drugId, Duration duration, String dosage, DrugType drugType, String drugName,
			String explanation, List<Long> dosageTime, List<DrugDirection> direction, String instructions) {
		super();
		this.drugId = drugId;
		this.duration = duration;
		this.dosage = dosage;
		this.drugType = drugType;
		this.drugName = drugName;
		this.explanation = explanation;
		this.dosageTime = dosageTime;
		this.direction = direction;
		this.instructions = instructions;
	}

	@Transient
	private Long arrayIndex1;
	
	public DrugType getDrugType() {
		return drugType;
	}

	public void setDrugType(DrugType drugType) {
		this.drugType = drugType;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

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

	public Long getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(Long inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}

	@Override
	public String toString() {
		return "PrescriptionItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", instructions=" + instructions + ", arrayIndex1="
				+ arrayIndex1 + "]";
	}

}
