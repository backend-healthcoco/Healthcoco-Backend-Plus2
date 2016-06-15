package com.dpdocter.solr.document;

import java.util.Date;
import java.util.List;

import javax.ws.rs.DefaultValue;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "drugs")
public class SolrDrugDocument {
    @Id
    @Field
    private String id;

    @Field
    private String drugName;

    @Field
    private String explanation;

    @Field
    private String drugCode;

    @Field
    private String drugTypeId;

    @Field
    private String drugType;

    @Field
    private String doctorId = "";

    @Field
    private String locationId = "";

    @Field
    @DefaultValue(value = "")
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    @Field
    private Date updatedTime = new Date();

    @Field
    private String companyName;

    @Field
    private String packSize;

    @Field
    private String MRP;

    @Field
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
	if (doctorId == null) {
	    return "";
	}
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	if (doctorId == null) {
	    this.doctorId = "";
	} else {
	    this.doctorId = doctorId;
	}
    }

    public String getLocationId() {
	if (locationId == null) {
	    return "";
	}
	return locationId;
    }

    public void setLocationId(String locationId) {
	if (locationId == null) {
	    this.locationId = "";
	} else {
	    this.locationId = locationId;
	}
    }

    public String getHospitalId() {
	if (hospitalId == null) {
	    return "";
	}
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	if (hospitalId == null) {
	    this.hospitalId = "";
	} else {
	    this.hospitalId = hospitalId;
	}
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
		return "SolrDrugDocument [id=" + id + ", drugName=" + drugName + ", explanation=" + explanation + ", drugCode="
				+ drugCode + ", drugTypeId=" + drugTypeId + ", drugType=" + drugType + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", updatedTime=" + updatedTime + ", companyName=" + companyName + ", packSize=" + packSize + ", MRP="
				+ MRP + ", genericIds=" + genericIds + "]";
	}
}
