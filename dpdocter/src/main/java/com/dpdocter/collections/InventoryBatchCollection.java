package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "inventory_batch_cl")
public class InventoryBatchCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String batchName;
	@Field
	private Long noOfItems = 0l;
	@Field
	private Long noOfItemsLeft = 0l;
	@Field
	private Long retailPrice;
	@Field
	private Long costPrice;
	@Field
	private ObjectId itemId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded;
	@Field
	private Long expiryDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
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

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getItemId() {
		return itemId;
	}

	public void setItemId(ObjectId itemId) {
		this.itemId = itemId;
	}

	public Long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Long expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "InventoryBatchCollection [id=" + id + ", batchName=" + batchName + ", noOfItems=" + noOfItems
				+ ", noOfItemsLeft=" + noOfItemsLeft + ", retailPrice=" + retailPrice + ", costPrice=" + costPrice
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
