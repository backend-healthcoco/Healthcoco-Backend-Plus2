package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.Strength;

@Document(collection = "drug_cl")
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
    private List<String> genericCodes;

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

    @Indexed(unique = true)
    @Field
    private String drugCode;

    @Field
    private String companyName;

    @Field
    private String packSize;

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

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	@Override
	public String toString() {
		return "DrugCollection [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation="
				+ explanation + ", strength=" + strength + ", genericCodes=" + genericCodes + ", genericNames="
				+ genericNames + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId
				+ ", discarded=" + discarded + ", drugCode=" + drugCode + ", companyName=" + companyName + ", packSize="
				+ packSize + ", MRP=" + MRP + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", categories=" + categories + "]";
	}

}
