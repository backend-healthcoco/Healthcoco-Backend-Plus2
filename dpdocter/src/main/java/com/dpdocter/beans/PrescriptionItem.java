package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import com.dpdocter.beans.v2.GenericCode;

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

	private Integer drugQuantity;
	
	private Integer analyticsDrugQuantity = 1;

	private Long inventoryQuantity;

	@Transient
	private Long arrayIndex1;
	
	private List<GenericCode> genericNames;

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

	public Integer getDrugQuantity() {
		return drugQuantity;
	}

	public void setDrugQuantity(Integer drugQuantity) {
		this.drugQuantity = drugQuantity;
	}

	public Integer getAnalyticsDrugQuantity() {
		return analyticsDrugQuantity;
	}

	public void setAnalyticsDrugQuantity(Integer analyticsDrugQuantity) {
		this.analyticsDrugQuantity = analyticsDrugQuantity;
	}
	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	@Override
	public String toString() {
		return "PrescriptionItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", drugType="
				+ drugType + ", drugName=" + drugName + ", explanation=" + explanation + ", dosageTime=" + dosageTime
				+ ", direction=" + direction + ", instructions=" + instructions + ", drugQuantity=" + drugQuantity
				+ ", inventoryQuantity=" + inventoryQuantity + ", arrayIndex1=" + arrayIndex1 + "]";
	}

}
