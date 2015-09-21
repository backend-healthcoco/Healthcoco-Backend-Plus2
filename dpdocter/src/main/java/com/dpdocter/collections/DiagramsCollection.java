package com.dpdocter.collections;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "diagrams_cl")
public class DiagramsCollection extends GenericCollection {

    @Field
    private String id;

    @Field
    private String diagramUrl;

    @Field
    private String tags;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String fileExtension;

    @Field
    private Boolean discarded = false;

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

    public String getTags() {
	return tags;
    }

    public void setTags(String tags) {
	this.tags = tags;
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

    @Override
    public String toString() {
	return "DiagramsCollection [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + ", doctorId=" + doctorId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", fileExtension=" + fileExtension + ", discarded=" + discarded + "]";
    }
}
