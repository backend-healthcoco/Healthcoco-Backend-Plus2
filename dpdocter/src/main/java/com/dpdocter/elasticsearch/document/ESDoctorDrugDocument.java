package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.GenericCode;

@Document(indexName = "doctor_drugs_in", type ="doctor_drugs")
public class ESDoctorDrugDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, index = FieldIndex.not_analyzed)
    private String drugName;

    @Field(type = FieldType.Text)
    private String explanation;

    @Field(type = FieldType.Text)
    private String drugCode;

    @Field(type = FieldType.Nested)
    private DrugType drugType;

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
    private String companyName;

    @Field(type = FieldType.Text)
    private String packSize;

    @Field(type = FieldType.Text)
    private String MRP;

    @MultiField(mainField = @Field(type = FieldType.Text))
    private List<String> genericCodes;

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
    
    @Field(type = FieldType.Long)
    private long rankingCount = 0;

    @Field(type = FieldType.Nested)
    private List<GenericCode> genericNames;
    
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

	@Override
	public String toString() {
		return "ESDoctorDrugDocument [id=" + id + ", drugName=" + drugName + ", explanation=" + explanation
				+ ", drugCode=" + drugCode + ", drugType=" + drugType + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", updatedTime="
				+ updatedTime + ", companyName=" + companyName + ", packSize=" + packSize + ", MRP=" + MRP
				+ ", genericCodes=" + genericCodes + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", categories=" + categories + ", rankingCount="
				+ rankingCount + ", genericNames=" + genericNames + "]";
	}
}
