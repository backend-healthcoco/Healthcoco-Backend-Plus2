package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Strength;

@Document(collection = "drug_cl")
public class DrugCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private DrugType drugType;

    @Field
    private String drugName;

    @Field
    private String explanation;

    @Field
    private Strength strength;

    @Field
    private List<String> genericCodes;

    @Field
    private String doctorId;

    @Field
    private String hospitalId;

    @Field
    private String locationId;

    @Field
    private Boolean discarded = false;

//    @Indexed(unique = true)
    @Field
    private String drugCode;

    @Field
    private String companyName;

    @Field
    private String packSize;

    @Field
    private String MRP;

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
    	if(drugName != null)this.drugName = drugName.toUpperCase();
    	else this.drugName = drugName;
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

	public List<String> getGenericCodes() {
		return genericCodes;
	}

	public void setGenericCodes(List<String> genericCodes) {
		this.genericCodes = genericCodes;
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

	@Override
	public String toString() {
		return "DrugCollection [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation="
				+ explanation + ", strength=" + strength + ", genericCodes=" + genericCodes + ", doctorId=" + doctorId
				+ ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", discarded=" + discarded
				+ ", drugCode=" + drugCode + ", companyName=" + companyName + ", packSize=" + packSize + ", MRP=" + MRP
				+ "]";
	}
}
