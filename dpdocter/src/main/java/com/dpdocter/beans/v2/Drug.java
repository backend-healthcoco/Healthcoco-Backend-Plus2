package com.dpdocter.beans.v2;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.DrugTypePlacement;
import com.dpdocter.response.PrescriptionInventoryBatchResponse;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Drug {

	private String id;

	private DrugType drugType;

	private String drTy;

	private String drugName;

	private String explanation;

	private Boolean discarded = false;

	private String doctorId;

	private String hospitalId;

	private String locationId;

	private Duration duration;

	private String durationValue;

	private String durationUnit;

	private String dosage;

	private List<Long> dosageTime;

	private List<DrugDirection> direction;

	private List<String> categories;

	private long rankingCount = 0;

	private List<GenericCode> genericNames;

	private String drugCode;

	private long count;

	private Long inventoryQuantity;

	private Long totalStock;

	private Integer drugQuantity;

	private Double retailPrice;

	private String drugTypePlacement = DrugTypePlacement.PREFIX.getPlacement();

	private List<PrescriptionInventoryBatchResponse> inventoryBatchs;

	private String drugCompanyId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DrugType getDrugType() {
		return drugType;
	}

	public void setDrugType(DrugType drugType) {
		this.drugType = drugType;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
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

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
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

	public Integer getDrugQuantity() {
		return drugQuantity;
	}

	public void setDrugQuantity(Integer drugQuantity) {
		this.drugQuantity = drugQuantity;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getDrugTypePlacement() {
		return drugTypePlacement;
	}

	public void setDrugTypePlacement(String drugTypePlacement) {
		this.drugTypePlacement = drugTypePlacement;
	}

	public String getDrTy() {
		return drTy;
	}

	public void setDrTy(String drTy) {
		this.drTy = drTy;
	}

	public String getDurationValue() {
		return durationValue;
	}

	public void setDurationValue(String durationValue) {
		this.durationValue = durationValue;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public List<PrescriptionInventoryBatchResponse> getInventoryBatchs() {
		return inventoryBatchs;
	}

	public void setInventoryBatchs(List<PrescriptionInventoryBatchResponse> inventoryBatchs) {
		this.inventoryBatchs = inventoryBatchs;
	}

	public String getDrugCompanyId() {
		return drugCompanyId;
	}

	public void setDrugCompanyId(String drugCompanyId) {
		this.drugCompanyId = drugCompanyId;
	}

	@Override
	public String toString() {
		return "Drug [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation=" + explanation
				+ ", discarded=" + discarded + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId="
				+ locationId + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime=" + dosageTime
				+ ", direction=" + direction + ", categories=" + categories + ", rankingCount=" + rankingCount
				+ ", genericNames=" + genericNames + ", drugCode=" + drugCode + ", count=" + count
				+ ", inventoryQuantity=" + inventoryQuantity + ", totalStock=" + totalStock + ", drugQuantity="
				+ drugQuantity + ", retailPrice=" + retailPrice + "]";
	}
}
