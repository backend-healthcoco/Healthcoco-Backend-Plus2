package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DrugTypePlacement;

public class InventoryItemLookupResposne extends GenericCollection {

	private String id;
	private String name;
	private String type;
	private String code;
	private String stockingUnit;
	private Long reOrderLevel = 0l;
	private Long totalStock = 0l;
	private String manufacturer;
	private String resourceId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;
	private Double retailPrice;
	private List<InventoryBatch> inventoryBatchs;
	private String drugType;
	private String drugTypePlacement = DrugTypePlacement.PREFIX.getPlacement();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStockingUnit() {
		return stockingUnit;
	}

	public void setStockingUnit(String stockingUnit) {
		this.stockingUnit = stockingUnit;
	}

	public Long getReOrderLevel() {
		return reOrderLevel;
	}

	public void setReOrderLevel(Long reOrderLevel) {
		this.reOrderLevel = reOrderLevel;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
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

	public List<InventoryBatch> getInventoryBatchs() {
		return inventoryBatchs;
	}

	public void setInventoryBatchs(List<InventoryBatch> inventoryBatchs) {
		this.inventoryBatchs = inventoryBatchs;
	}

	public Long getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Long totalStock) {
		this.totalStock = totalStock;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getDrugTypePlacement() {
		return drugTypePlacement;
	}

	public void setDrugTypePlacement(String drugTypePlacement) {
		this.drugTypePlacement = drugTypePlacement;
	}

	@Override
	public String toString() {
		return "InventoryItemLookupResposne [id=" + id + ", name=" + name + ", type=" + type + ", code=" + code
				+ ", stockingUnit=" + stockingUnit + ", reOrderLevel=" + reOrderLevel + ", totalStock=" + totalStock
				+ ", manufacturer=" + manufacturer + ", resourceId=" + resourceId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", retailPrice=" + retailPrice
				+ ", inventoryBatchs=" + inventoryBatchs + "]";
	}

}
