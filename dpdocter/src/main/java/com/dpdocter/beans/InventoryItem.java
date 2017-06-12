package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.util.web.JacksonUtil;

public class InventoryItem extends GenericCollection {

	private String id;
	private String name;
	private String type;
	private String code;
	private Long stockingUnit;
	private String manufacturer;
	private Boolean saveManufacturer;
	private String resourceId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;

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

	public Long getStockingUnit() {
		return stockingUnit;
	}

	public void setStockingUnit(Long stockingUnit) {
		this.stockingUnit = stockingUnit;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Boolean getSaveManufacturer() {
		return saveManufacturer;
	}

	public void setSaveManufacturer(Boolean saveManufacturer) {
		this.saveManufacturer = saveManufacturer;
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

	@Override
	public String toString() {
		return "InventoryItem [id=" + id + ", name=" + name + ", type=" + type + ", code=" + code + ", stockingUnit="
				+ stockingUnit + ", manufacturer=" + manufacturer + ", saveManufacturer=" + saveManufacturer
				+ ", resourceId=" + resourceId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + "]";
	}

}
