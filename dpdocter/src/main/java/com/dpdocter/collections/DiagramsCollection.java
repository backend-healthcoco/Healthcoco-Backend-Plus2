package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "diagrams_cl")
public class DiagramsCollection {

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
	private Date createdTime;

	@Field
	private boolean isDeleted = false;

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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "DiagramsCollection [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", fileExtension="
				+ fileExtension + ", isDeleted=" + isDeleted + "]";
	}

}
