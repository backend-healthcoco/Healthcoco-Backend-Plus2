package com.dpdocter.elasticsearch.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.enums.DrugTypePlacement;

public class DrugDocument {

	private String id;

	private String drugName;

	private String explanation;

	private String drugCode;

	private DrugType drugType;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private Date updatedTime = new Date();

	private String companyName;

	private String packSize;

	private String MRP;

	private List<String> genericCodes;

	private Duration duration;

	private String dosage;

	private List<Long> dosageTime;

	private List<DrugDirection> direction;

	private List<String> categories;

	private List<GenericCode> genericNames;

	private Long inventoryQuantity;

	private Long totalStock;

	private Double retailPrice;

	private String stockingUnit;

	private String drugTypePlacement = DrugTypePlacement.PREFIX.getPlacement();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public DrugType getDrugType() {
		return drugType;
	}

	public void setDrugType(DrugType drugType) {
		this.drugType = drugType;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPackSize() {
		return packSize;
	}

	public void setPackSize(String packSize) {
		this.packSize = packSize;
	}

	public String getMRP() {
		return MRP;
	}

	public void setMRP(String mRP) {
		MRP = mRP;
	}

	public List<String> getGenericCodes() {
		return genericCodes;
	}

	public void setGenericCodes(List<String> genericCodes) {
		this.genericCodes = genericCodes;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
	}

	public List<DrugDirection> getDirection() {
		return direction;
	}

	public void setDirection(List<DrugDirection> direction) {
		this.direction = direction;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	public Long getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(Long inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
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

	public String getStockingUnit() {
		return stockingUnit;
	}

	public void setStockingUnit(String stockingUnit) {
		this.stockingUnit = stockingUnit;
	}

	public String getDrugTypePlacement() {
		return drugTypePlacement;
	}

	public void setDrugTypePlacement(String drugTypePlacement) {
		this.drugTypePlacement = drugTypePlacement;
	}

	@Override
	public String toString() {
		return "ESDrugDocument [id=" + id + ", drugName=" + drugName + ", explanation=" + explanation + ", drugCode="
				+ drugCode + ", drugType=" + drugType + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", updatedTime=" + updatedTime
				+ ", companyName=" + companyName + ", packSize=" + packSize + ", MRP=" + MRP + ", genericCodes="
				+ genericCodes + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime=" + dosageTime
				+ ", direction=" + direction + ", categories=" + categories + ", genericNames=" + genericNames + "]";
	}

}
