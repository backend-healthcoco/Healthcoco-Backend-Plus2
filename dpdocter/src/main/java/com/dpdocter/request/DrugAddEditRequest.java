package com.dpdocter.request;

import java.util.List;
import java.util.UUID;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.Strength;

public class DrugAddEditRequest {
    private String id;

    private DrugType drugType;

    private String drugName;

    private String explanation;

    private Strength strength;
    
    private String doctorId;

    private String hospitalId;

    private String locationId;

    private UUID drugCode;

    private String companyName;

    private String packSize;

    private String MRP;

    private Duration duration;

    private String dosage;

    private List<Long> dosageTime;
    
    private List<DrugDirection> direction;

    private List<String> categories;
    
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

    public UUID getDrugCode() {
	return drugCode;
    }

    public void setDrugCode(UUID drugCode) {
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

	public Strength getStrength() {
		return strength;
	}

	public void setStrength(Strength strength) {
		this.strength = strength;
	}

	@Override
	public String toString() {
		return "DrugAddEditRequest [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation="
				+ explanation + ", strength=" + strength + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + ", drugCode=" + drugCode + ", companyName=" + companyName
				+ ", packSize=" + packSize + ", MRP=" + MRP + ", duration=" + duration + ", dosage=" + dosage
				+ ", dosageTime=" + dosageTime + ", direction=" + direction + ", categories=" + categories + "]";
	}

}
