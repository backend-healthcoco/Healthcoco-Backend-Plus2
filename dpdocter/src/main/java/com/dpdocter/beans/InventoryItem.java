 package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DrugTypePlacement;

public class InventoryItem extends GenericCollection {

	private String id;
	private String name;
	private String type;
	private String code;
	private String stockingUnit;
	private Long reOrderLevel;
	private String manufacturer;
	private Boolean saveManufacturer;
	private String doctorId;
	private String resourceId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;
	private String itemType;
	private Double retailPrice;
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

	public Long getReOrderLevel() {
		return reOrderLevel;
	}

	public void setReOrderLevel(Long reOrderLevel) {
		this.reOrderLevel = reOrderLevel;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
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
		return "InventoryItem [id=" + id + ", name=" + name + ", type=" + type + ", code=" + code + ", stockingUnit="
				+ stockingUnit + ", reOrderLevel=" + reOrderLevel + ", manufacturer=" + manufacturer
				+ ", saveManufacturer=" + saveManufacturer + ", doctorId=" + doctorId + ", resourceId=" + resourceId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", retailPrice=" + retailPrice + ", itemType=" + itemType + "]";
	}

}
