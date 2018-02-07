package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class InventoryStockCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId itemId;
	@Field
	private Long quantity;
	@Field
	private ObjectId batchId;
	@Field
	private Long costPrice;
	@Field
	private Long retailPrice;
	@Field
	private String stockType;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private Long totalPrice;
	@Field
	private ObjectId resourceId;
	@Field
	private ObjectId invoiceId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getItemId() {
		return itemId;
	}

	public void setItemId(ObjectId itemId) {
		this.itemId = itemId;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public ObjectId getBatchId() {
		return batchId;
	}

	public void setBatchId(ObjectId batchId) {
		this.batchId = batchId;
	}

	public Long getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Long costPrice) {
		this.costPrice = costPrice;
	}

	public Long getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Long retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
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

	public Long getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Long totalPrice) {
		this.totalPrice = totalPrice;
	}

	public ObjectId getResourceId() {
		return resourceId;
	}

	public void setResourceId(ObjectId resourceId) {
		this.resourceId = resourceId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(ObjectId invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public String toString() {
		return "InventoryStockCollection [id=" + id + ", itemId=" + itemId + ", quantity=" + quantity + ", batchId="
				+ batchId + ", costPrice=" + costPrice + ", retailPrice=" + retailPrice + ", stockType=" + stockType
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", totalPrice=" + totalPrice + ", resourceId=" + resourceId + "]";
	}

}
