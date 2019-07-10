package com.dpdocter.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.collections.GenericCollection;

@Document(indexName = "dental_works_in", type = "dental_works")
public class ESDentalWorksDocument extends GenericCollection {

	@Id
	private String id;
	
	@Field(type = FieldType.Text)
	private String doctorId;
	
	@Field(type = FieldType.Text)
	private String locationId;
	
	@Field(type = FieldType.Text)
	private String hospitalId;
	
	@Field(type = FieldType.Text)
	private String workName;
	
	@Field(type = FieldType.Boolean)
	private Boolean isShadeRequired = false;
	
	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Boolean getIsShadeRequired() {
		return isShadeRequired;
	}

	public void setIsShadeRequired(Boolean isShadeRequired) {
		this.isShadeRequired = isShadeRequired;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
