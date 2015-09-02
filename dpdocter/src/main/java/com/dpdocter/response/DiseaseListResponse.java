package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class DiseaseListResponse extends GenericCollection{
    private String id;

    private String disease;

    private String description;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Boolean discarded = false;


    public DiseaseListResponse() {
	// TODO Auto-generated constructor stub
    }

    public DiseaseListResponse(String id, String disease, String description, String doctorId, String locationId,String hospitalId, Boolean discarded, Date createdTime, Date updatedTime) {
		this.id = id;
		this.disease = disease;
		this.description = description;
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		this.discarded = discarded;
		super.setCreatedTime(createdTime);
		super.setUpdatedTime(updatedTime);
	}

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDisease() {
	return disease;
    }

    public void setDisease(String disease) {
	this.disease = disease;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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

	@Override
	public String toString() {
		return "DiseaseListResponse [id=" + id + ", disease=" + disease + ", description=" + description + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ "]";
	}


}
