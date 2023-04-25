package com.dpdocter.request;

public class DrugRequest {

	private String drugId;

	private String drugName;

	private Integer quantity = 0;

	public String getDrugId() {
		return drugId;
	}

	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	@Override
	public String toString() {
		return "DrugRequest [drugId=" + drugId + ", drugName=" + drugName + "]";
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
