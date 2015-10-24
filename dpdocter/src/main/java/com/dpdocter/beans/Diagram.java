package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Diagram extends GenericCollection {
    private String id;

    private String diagramUrl;

    private String tags;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private FileDetails diagram;

    private String fileExtension;

    private Boolean discarded = false;

    private String speciality;

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

    public FileDetails getDiagram() {
	return diagram;
    }

    public void setDiagram(FileDetails diagram) {
	this.diagram = diagram;
    }

    public String getFileExtension() {
	return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
	this.fileExtension = fileExtension;
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

    @Override
    public String toString() {
	return "Diagram [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + ", doctorId=" + doctorId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", diagram=" + diagram + ", fileExtension=" + fileExtension + ", discarded=" + discarded + ", speciality="
		+ speciality + "]";
    }
}
