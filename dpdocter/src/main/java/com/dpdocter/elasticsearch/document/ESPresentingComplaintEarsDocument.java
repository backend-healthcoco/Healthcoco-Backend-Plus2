package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName="presenting_complaint_ears_in" , type="presenting_complaint_ears")
public class ESPresentingComplaintEarsDocument {

	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String pcEars;

	@Field(type = FieldType.Text)
	private String doctorId;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String hospitalId;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Text)
	private String speciality;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPcEars() {
		return pcEars;
	}

	public void setPcEars(String pcEars) {
		this.pcEars = pcEars;
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "ESPresentingComplaintEars [id=" + id + ", pcEars=" + pcEars + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", speciality=" + speciality
				+ ", updatedTime=" + updatedTime + "]";
	}

}
