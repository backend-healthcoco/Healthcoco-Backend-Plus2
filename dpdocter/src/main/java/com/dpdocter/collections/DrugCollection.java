package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.Strength;
import com.dpdocter.enums.DrugTypePlacement;

@Document(collection = "drug_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'drugCode' : 1, 'doctorId': 1}", unique = true) })
public class DrugCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private DrugType drugType;

	@Field
	private String drugName;

	@Field
	private String explanation;

	@Field
	private Strength strength;

	@Field
	private List<GenericCode> genericNames;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId locationId;

	@Field
	private Boolean discarded = false;

	@Field
	private String drugCode;

	@Field
	private String companyName;

	@Field
	private long companyRankingCount = 0;

	@Field
	private String packSize;

	@Field
	private String packForm;

	@Field
	private List<ObjectId> gcStrengthIds;

	@Field
	private Integer drugQuantity;

	@Field
	private String prizePerPack;

	@Field
	private String MRP;

	@Field
	private Duration duration;

	@Field
	private String dosage;

	@Field
	private List<Long> dosageTime;

	@Field
	private List<DrugDirection> direction;

	@Field
	private List<String> categories;

	@Field
	private long rankingCount = 0;

	@Field
	private Long inventoryQuantity;

	@Field
	private List<String> specialities;

	@Field
	private String rxRequired;

	@Field
	private String unsafeWith;

	@Field
	private Long totalStock;

	@Field
	private String drugTypePlacement = DrugTypePlacement.PREFIX.getPlacement();

	@Field
	private ObjectId drugCompanyId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
		if (drugName != null)
			this.drugName = drugName.toUpperCase();
		else
			this.drugName = drugName;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Strength getStrength() {
		return strength;
	}

	public void setStrength(Strength strength) {
		this.strength = strength;
	}

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
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

	public List<ObjectId> getGcStrengthIds() {
		return gcStrengthIds;
	}

	public void setGcStrengthIds(List<ObjectId> gcStrengthIds) {
		this.gcStrengthIds = gcStrengthIds;
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

	public long getCompanyRankingCount() {
		return companyRankingCount;
	}

	public void setCompanyRankingCount(long companyRankingCount) {
		this.companyRankingCount = companyRankingCount;
	}

	public Integer getDrugQuantity() {
		return drugQuantity;
	}

	public void setDrugQuantity(Integer drugQuantity) {
		this.drugQuantity = drugQuantity;
	}

	public String getDrugTypePlacement() {
		return drugTypePlacement;
	}

	public void setDrugTypePlacement(String drugTypePlacement) {
		this.drugTypePlacement = drugTypePlacement;
	}

	public ObjectId getDrugCompanyId() {
		return drugCompanyId;
	}

	public void setDrugCompanyId(ObjectId drugCompanyId) {
		this.drugCompanyId = drugCompanyId;
	}

	@Override
	public String toString() {
		return "DrugCollection [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation="
				+ explanation + ", strength=" + strength + ", genericNames=" + genericNames + ", doctorId=" + doctorId
				+ ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", discarded=" + discarded
				+ ", drugCode=" + drugCode + ", companyName=" + companyName + ", companyRankingCount="
				+ companyRankingCount + ", packSize=" + packSize + ", packForm=" + packForm + ", gcStrengthIds="
				+ gcStrengthIds + ", drugQuantity=" + drugQuantity + ", prizePerPack=" + prizePerPack + ", MRP=" + MRP
				+ ", duration=" + duration + ", dosage=" + dosage + ", dosageTime=" + dosageTime + ", direction="
				+ direction + ", categories=" + categories + ", rankingCount=" + rankingCount + ", inventoryQuantity="
				+ inventoryQuantity + ", specialities=" + specialities + ", rxRequired=" + rxRequired + ", unsafeWith="
				+ unsafeWith + ", totalStock=" + totalStock + "]";
	}
}
