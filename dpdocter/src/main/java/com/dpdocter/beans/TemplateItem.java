package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.v2.GenericCode;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TemplateItem {
	private ObjectId drugId;

	private Duration duration;

	private String dosage;

	private DrugType drugType;

	private String drugName;

	private List<Long> dosageTime;

	private List<DrugDirection> direction;

	private String instructions;

	private Long inventoryQuantity;

	private Integer quantity = 0;
	
	private List<GenericCode> genericNames;

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

	public Long getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(Long inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	@Override
	public String toString() {
		return "TemplateItem [drugId=" + drugId + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", instructions=" + instructions + "]";
	}
}
