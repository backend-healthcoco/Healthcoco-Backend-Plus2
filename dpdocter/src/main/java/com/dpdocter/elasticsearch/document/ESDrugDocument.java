package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.enums.DrugTypePlacement;

@Document(indexName = "drugs_in", type = "drugs")
public class ESDrugDocument {
	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String drugName;

	@Field(type = FieldType.Text)
	private String explanation;

	@Field(type = FieldType.Text)
	private String drugCode;

	@Field(type = FieldType.Text)
	private String drugTypeId;

	@Field(type = FieldType.Text)
	private String drugType;

	@Field(type = FieldType.Text)
	private String doctorId;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String hospitalId;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.Text)
	private String packForm;

	@Field(type = FieldType.Text)
	private String prizePerPack;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> specialities;

	@Field(type = FieldType.Long)
	private long companyRankingCount = 0;

	@Field(type = FieldType.Text)
	private String rxRequired;

	@Field(type = FieldType.Text)
	private String unsafeWith;

	@Field(type = FieldType.Text)
	private String companyName;

	@Field(type = FieldType.Text)
	private String packSize;

	@Field(type = FieldType.Text)
	private String MRP;

	@Field(type = FieldType.Nested)
	private Duration duration;

	@Field(type = FieldType.Text)
	private String dosage;

	@MultiField(mainField = @Field(type = FieldType.Long))
	private List<Long> dosageTime;

	@Field(type = FieldType.Nested)
	private List<DrugDirection> direction;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> categories;

	@Field(type = FieldType.Nested)
	private List<GenericCode> genericNames;

	@Field(type = FieldType.Long)
	private long rankingCount = 0;

	@Field(type = FieldType.Long)
	private Long inventoryQuantity;

	@Field(type = FieldType.Long)
	private Long totalStock;

	@Field(type = FieldType.Long)
	private Double retailPrice;

	@Field(type = FieldType.Text)
	private String stockingUnit;

	@Field(type = FieldType.Text)
	private String drugTypePlacement = DrugTypePlacement.PREFIX.getPlacement();
	
	@Override
	public int hashCode() {
		return this.drugCode.hashCode();
	}

	public boolean equals(Object obj) {
		ESDrugDocument drugDocument = (ESDrugDocument) obj;
		return this.drugCode.equalsIgnoreCase(drugDocument.drugCode);
	}

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

	public String getPackForm() {
		return packForm;
	}

	public void setPackForm(String packForm) {
		this.packForm = packForm;
	}

	public String getPrizePerPack() {
		return prizePerPack;
	}

	public void setPrizePerPack(String prizePerPack) {
		this.prizePerPack = prizePerPack;
	}

	public String getMRP() {
		return MRP;
	}

	public void setMRP(String mRP) {
		MRP = mRP;
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

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public String getDrugTypeId() {
		return drugTypeId;
	}

	public void setDrugTypeId(String drugTypeId) {
		this.drugTypeId = drugTypeId;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
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

	public long getCompanyRankingCount() {
		return companyRankingCount;
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

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public String getRxRequired() {
		return rxRequired;
	}

	public void setRxRequired(String rxRequired) {
		this.rxRequired = rxRequired;
	}

	public String getUnsafeWith() {
		return unsafeWith;
	}

	public void setUnsafeWith(String unsafeWith) {
		this.unsafeWith = unsafeWith;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public void setCompanyRankingCount(long companyRankingCount) {
		this.companyRankingCount = companyRankingCount;
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
				+ drugCode + ", drugTypeId=" + drugTypeId + ", drugType=" + drugType + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", updatedTime=" + updatedTime + ", packForm=" + packForm + ", prizePerPack=" + prizePerPack
				+ ", companyRankingCount=" + companyRankingCount + ", specialities=" + specialities + ", rxRequired="
				+ rxRequired + ", unsafeWith=" + unsafeWith + ", companyName=" + companyName + ", packSize=" + packSize
				+ ", MRP=" + MRP + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime=" + dosageTime
				+ ", direction=" + direction + ", categories=" + categories + ", genericNames=" + genericNames
				+ ", rankingCount=" + rankingCount + ", inventoryQuantity=" + inventoryQuantity + ", totalStock="
				+ totalStock + ", retailPrice=" + retailPrice + ", stockingUnit=" + stockingUnit + "]";
	}
}
