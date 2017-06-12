package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.JacksonUtil;

public class InventoryBatch extends GenericCollection {

	
	private String id;
	private Long batchName;
	private Long noOfItems;
	private Long noOfItemsLeft;
	private Long retailPrice;
	private Long costPrice;
	private String locationId;
	private String hospitalId;
	private boolean discarded = false;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getBatchName() {
		return batchName;
	}
	public void setBatchName(Long batchName) {
		this.batchName = batchName;
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
	public Long getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(Long retailPrice) {
		this.retailPrice = retailPrice;
	}
	public Long getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(Long costPrice) {
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
	public boolean isDiscarded() {
		return discarded;
	}
	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}
	@Override
	public String toString() {
		return "InventoryBatch [id=" + id + ", batchName=" + batchName + ", noOfItems=" + noOfItems + ", noOfItemsLeft="
				+ noOfItemsLeft + ", retailPrice=" + retailPrice + ", costPrice=" + costPrice + "]";
	}
	
}
