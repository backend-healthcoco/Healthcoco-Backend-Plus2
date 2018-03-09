package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;


public class InventoryStock extends GenericCollection {

	private String id;
	private String itemId;
	private Long quantity;
	private String batchId;
	private InventoryBatch inventoryBatch;
	private Long costPrice;
	private Long retailPrice;
	private String stockType;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;
	private Long totalPrice;
	private String resourceId;
	private String invoiceId;

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

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public InventoryBatch getInventoryBatch() {
		return inventoryBatch;
	}

	public void setInventoryBatch(InventoryBatch inventoryBatch) {
		this.inventoryBatch = inventoryBatch;
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

	public Long getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Long totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public String toString() {
		return "InventoryStock [id=" + id + ", itemId=" + itemId + ", quantity=" + quantity + ", batchId=" + batchId
				+ ", inventoryBatch=" + inventoryBatch + ", costPrice=" + costPrice + ", retailPrice=" + retailPrice
				+ ", stockType=" + stockType + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}

}
