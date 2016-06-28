package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Document(indexName = "drugs_in", type ="drugs")
public class ESDrugDocument {
    @Id
    private String id;

    @Field(type = FieldType.String)
    private String drugName;

    @Field(type = FieldType.String)
    private String explanation;

    @Field(type = FieldType.String)
    private String drugCode;

    @Field(type = FieldType.String)
    private String drugTypeId;

    @Field(type = FieldType.String)
    private String drugType;

    @Field(type = FieldType.String)
    private String doctorId;

    @Field(type = FieldType.String)
    private String locationId;

    @Field(type = FieldType.String)
    private String hospitalId;

    @Field(type = FieldType.Boolean)
    private Boolean discarded = false;

    @Field(type = FieldType.Date)
    private Date updatedTime = new Date();

    @Field(type = FieldType.String)
    private String companyName;

    @Field(type = FieldType.String)
    private String packSize;

    @Field(type = FieldType.String)
    private String MRP;

    @MultiField(mainField = @Field(type = FieldType.String))
    private List<String> genericIds;

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

	
	public List<String> getGenericIds() {
		return genericIds;
	}

	public void setGenericIds(List<String> genericIds) {
		this.genericIds = genericIds;
	}

	@Override
	public String toString() {
		return "ESDrugDocument [id=" + id + ", drugName=" + drugName + ", explanation=" + explanation + ", drugCode="
				+ drugCode + ", drugTypeId=" + drugTypeId + ", drugType=" + drugType + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", updatedTime=" + updatedTime + ", companyName=" + companyName + ", packSize=" + packSize + ", MRP="
				+ MRP + ", genericIds=" + genericIds + "]";
	}
}
