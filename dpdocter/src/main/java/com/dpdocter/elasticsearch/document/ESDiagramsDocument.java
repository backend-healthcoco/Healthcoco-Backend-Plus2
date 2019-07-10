package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "diagrams_in", type = "diagrams")
public class ESDiagramsDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String diagramUrl;

    @Field(type = FieldType.Text)
    private String tags;

    @Field(type = FieldType.Text)
    private String speciality;

    @Field(type = FieldType.Text)
    private String doctorId;

    @Field(type = FieldType.Text)
    private String locationId;

    @Field(type = FieldType.Text)
    private String hospitalId;

    @Field(type = FieldType.Boolean)
    private Boolean discarded = false;

    @Field(type = FieldType.Date)
    private Date updatedTime = new Date();

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

    public String getSpeciality() {
	return speciality;
    }

    public void setSpeciality(String speciality) {
	this.speciality = speciality;
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

    @Override
    public String toString() {
	return "ESDiagramsDocument [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + ", speciality=" + speciality + ", doctorId=" + doctorId
		+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", updatedTime=" + updatedTime + "]";
    }

}
