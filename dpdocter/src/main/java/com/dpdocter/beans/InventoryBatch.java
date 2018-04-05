package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class InventoryBatch extends GenericCollection {

	private String id;
	private String itemId;
	private String batchName;
	private Long expiryDate;
	private Long noOfItems;
	private Long noOfItemsLeft;
	private Double retailPrice;
	private Double costPrice;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Long expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Long getNoOfItems() {
		return noOfItems;
	}

	public void setNoOfItems(Long noOfItems) {
		this.noOfItems = noOfItems;
	}

	public Long getNoOfItemsLeft() {
		return noOfItemsLeft;
	}

	public void setNoOfItemsLeft(Long noOfItemsLeft) {
		this.noOfItemsLeft = noOfItemsLeft;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public Double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "InventoryBatch [id=" + id + ", batchName=" + batchName + ", noOfItems=" + noOfItems + ", noOfItemsLeft="
				+ noOfItemsLeft + ", retailPrice=" + retailPrice + ", costPrice=" + costPrice + "]";
	}

}
