package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Diagram extends GenericCollection {
    private String id;

    private String diagramUrl;

    private String tags;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Boolean discarded = false;

    private String speciality;

    private String clinicalNotesId;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDiagramUrl() {
	return diagramUrl;
    }

    public void setDiagramUrl(String diagramUrl) {
	this.diagramUrl = diagramUrl;
    }

    public String getTags() {
	return tags;
    }

    public void setTags(String tags) {
	this.tags = tags;
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

    public String getSpeciality() {
	return speciality;
    }

    public void setSpeciality(String speciality) {
	this.speciality = speciality;
    }

	public String getClinicalNotesId() {
		return clinicalNotesId;
	}

	public void setClinicalNotesId(String clinicalNotesId) {
		this.clinicalNotesId = clinicalNotesId;
	}

	@Override
	public String toString() {
		return "Diagram [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", speciality=" + speciality
				+ ", clinicalNotesId=" + clinicalNotesId + "]";
	}

}
