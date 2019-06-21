package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

//@Document(collection = "inventory_stock_cl")
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
	private Double costPrice;
	@Field
	private Double retailPrice;
	@Field
	private String stockType;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private Double totalPrice;
	@Field
	private String resourceId;
	@Field
	private ObjectId invoiceId;
	@Field
	private Boolean isPatientDiscarded = false;
	
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

	public Double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public ObjectId getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(ObjectId invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "InventoryStockCollection [id=" + id + ", itemId=" + itemId + ", quantity=" + quantity + ", batchId="
				+ batchId + ", costPrice=" + costPrice + ", retailPrice=" + retailPrice + ", stockType=" + stockType
				+ ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", discarded=" + discarded + ", totalPrice=" + totalPrice + ", resourceId=" + resourceId
				+ ", invoiceId=" + invoiceId + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
