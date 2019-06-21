package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.response.PrescriptionInventoryBatchResponse;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PrescriptionItemDetail {
	private Drug drug;

	private Duration duration;

	private String dosage;

	private DrugType drugType; 

	private String drugName;

	private Integer drugQuantity;

	private Long totalStock;

	private List<PrescriptionInventoryBatchResponse> inventoryBatchs;

	private String explanation;

	private List<Long> dosageTime;

	private List<DrugDirection> direction;

	private String instructions;

	private Long inventoryQuantity;

	private List<GenericCode> genericNames;

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

	public Long getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Long totalStock) {
		this.totalStock = totalStock;
	}

	public List<PrescriptionInventoryBatchResponse> getInventoryBatchs() {
		return inventoryBatchs;
	}

	public void setInventoryBatchs(List<PrescriptionInventoryBatchResponse> inventoryBatchs) {
		this.inventoryBatchs = inventoryBatchs;
	}

	public Integer getDrugQuantity() {
		return drugQuantity;
	}

	public void setDrugQuantity(Integer drugQuantity) {
		this.drugQuantity = drugQuantity;
	}

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	@Override
	public String toString() {
		return "PrescriptionItemDetail [drug=" + drug + ", duration=" + duration + ", dosage=" + dosage + ", drugType="
				+ drugType + ", drugName=" + drugName + ", drugQuantity=" + drugQuantity + ", totalStock=" + totalStock
				+ ", inventoryBatchs=" + inventoryBatchs + ", explanation=" + explanation + ", dosageTime=" + dosageTime
				+ ", direction=" + direction + ", instructions=" + instructions + ", inventoryQuantity="
				+ inventoryQuantity + "]";
	}

}
