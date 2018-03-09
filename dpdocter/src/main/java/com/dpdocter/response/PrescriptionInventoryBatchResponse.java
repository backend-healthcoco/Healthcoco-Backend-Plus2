package com.dpdocter.response;

public class PrescriptionInventoryBatchResponse {

	private String id;
	private String batchName;
	private Long noOfItemsLeft;
	private Long retailPrice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Long getNoOfItemsLeft() {
		return noOfItemsLeft;
	}

	public void setNoOfItemsLeft(Long noOfItemsLeft) {
		this.noOfItemsLeft = noOfItemsLeft;
	}

	public Long getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Long retailPrice) {
		this.retailPrice = retailPrice;
	}

	@Override
	public String toString() {
		return "PrescriptionInventoryBatchResponse [id=" + id + ", batchName=" + batchName + ", noOfItemsLeft="
				+ noOfItemsLeft + ", retailPrice=" + retailPrice + "]";
	}

}
