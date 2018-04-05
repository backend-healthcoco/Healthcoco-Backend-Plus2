package com.dpdocter.response;

import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.collections.GenericCollection;

public class InventoryStockLookupResponse extends GenericCollection {

	private String id;
	private String itemId;
	private Long quantity;
	private String batchId;
	private InventoryBatch inventoryBatch;
	private Double costPrice;
	private Double retailPrice;
	private String stockType;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;
	private InventoryItem inventoryItem;

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

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	@Override
	public String toString() {
		return "InventoryStockLookupResponse [id=" + id + ", itemId=" + itemId + ", quantity=" + quantity + ", batchId="
				+ batchId + ", inventoryBatch=" + inventoryBatch + ", costPrice=" + costPrice + ", retailPrice="
				+ retailPrice + ", stockType=" + stockType + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", inventoryItem=" + inventoryItem + "]";
	}

}
