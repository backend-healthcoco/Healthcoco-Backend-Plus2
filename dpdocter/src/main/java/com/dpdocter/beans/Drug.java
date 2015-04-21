package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DrugTypeEnum;

public class Drug {
	
	private String id;

	private DrugTypeEnum drugType;

	private String drugName;

	private String description;

	private Strength strength;

	private List<String> genericNames;
	
	private String drugCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DrugTypeEnum getDrugType() {
		return drugType;
	}

	public void setDrugType(DrugTypeEnum drugType) {
		this.drugType = drugType;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Strength getStrength() {
		return strength;
	}

	public void setStrength(Strength strength) {
		this.strength = strength;
	}

	public List<String> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<String> genericNames) {
		this.genericNames = genericNames;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	@Override
	public String toString() {
		return "Drug [id=" + id + ", drugType=" + drugType + ", drugName="
				+ drugName + ", description=" + description + ", strength="
				+ strength + ", genericNames=" + genericNames + ", drugCode="
				+ drugCode + "]";
	}
	
	
	
}
