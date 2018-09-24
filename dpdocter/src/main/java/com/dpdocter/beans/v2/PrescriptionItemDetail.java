package com.dpdocter.beans.v2;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.response.PrescriptionInventoryBatchResponse;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PrescriptionItemDetail {
	private Drug drug;

	/*
	 * private Duration duration;
	 * 
	 * private String dosage;
	 * 
	 * private DrugType drugType;
	 * 
	 * private String drugName;
	 * 
	 * private Integer drugQuantity;
	 * 
	 * 
	 */

	private Long totalStock;
	private List<PrescriptionInventoryBatchResponse> inventoryBatchs;

	/*
	 * private String explanation;
	 * 
	 * private List<Long> dosageTime;
	 * 
	 * private List<DrugDirection> direction;
	 */
	private String instructions;

	private Long inventoryQuantity;

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	public List<PrescriptionInventoryBatchResponse> getInventoryBatchs() {
		return inventoryBatchs;
	}

	public void setInventoryBatchs(List<PrescriptionInventoryBatchResponse> inventoryBatchs) {
		this.inventoryBatchs = inventoryBatchs;
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

	@Override
	public String toString() {
		return "PrescriptionItemDetail [drug=" + drug + ", inventoryBatchs=" + inventoryBatchs + ", inventoryQuantity="
				+ inventoryQuantity + "]";
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

}
