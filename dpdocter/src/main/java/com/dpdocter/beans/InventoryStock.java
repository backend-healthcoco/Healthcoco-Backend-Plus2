package com.dpdocter.beans;

import com.amazonaws.util.json.Jackson;
import com.dpdocter.collections.GenericCollection;

import common.util.web.JacksonUtil;

public class InventoryStock extends GenericCollection {

	private String id;
	private String itemId;
	private Long quantity;
	private String batchId;
	private InventoryBatch inventoryBatch;
	private Long costPrice;
	private Long retailPrice;
	private String stockType;
	private String locationId;
	private String hospitalId;

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

	@Override
	public String toString() {
		return "InventoryStock [id=" + id + ", itemId=" + itemId + ", quantity=" + quantity + ", batchId=" + batchId
				+ ", inventoryBatch=" + inventoryBatch + ", costPrice=" + costPrice + ", retailPrice=" + retailPrice
				+ ", stockType=" + stockType + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}

}
