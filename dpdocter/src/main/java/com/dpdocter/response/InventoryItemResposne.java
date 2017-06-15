package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class InventoryItemResposne extends GenericCollection {

	private String id;
	private String name;
	private String type;
	private String code;
	private String stockingUnit;
	private Long reOrderLevel;
	private String manufacturer;
	private String resourceId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;
	private Long totalStock = 0l;

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

	public Long getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Long totalStock) {
		this.totalStock = totalStock;
	}
	

}
