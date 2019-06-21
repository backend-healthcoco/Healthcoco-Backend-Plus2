package com.dpdocter.response;

import java.util.List;

public class OPDPrescriptionItemResponse {

	private String id;

	private DurationResponse duration;

	private String dosage;

	private String drugType;

	private String drugName;

	private String explanation;

	private List<Long> dosageTime;

	private List<String> direction;

	private String instructions;

	private Integer drugQuantity;

	private List<GenericCodeResponse> genericNames;

	private List<String> categories;

	private String drugCode;

	

	
	@Override
	public String toString() {
		return "OPDPrescriptionItemResponse [id=" + id + ", duration=" + duration + ", dosage=" + dosage + ", drugType="
				+ drugType + ", drugName=" + drugName + ", explanation=" + explanation + ", dosageTime=" + dosageTime
				+ ", direction=" + direction + ", instructions=" + instructions + ", drugQuantity=" + drugQuantity
				+ ", genericNames=" + genericNames + ", categories=" + categories + ", drugCode=" + drugCode + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DurationResponse getDuration() {
		return duration;
	}

	public void setDuration(DurationResponse duration) {
		this.duration = duration;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
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

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
	}

	public List<String> getDirection() {
		return direction;
	}

	public void setDirection(List<String> direction) {
		this.direction = direction;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public Integer getDrugQuantity() {
		return drugQuantity;
	}

	public void setDrugQuantity(Integer drugQuantity) {
		this.drugQuantity = drugQuantity;
	}

	public List<GenericCodeResponse> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCodeResponse> genericNames) {
		this.genericNames = genericNames;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}
}
