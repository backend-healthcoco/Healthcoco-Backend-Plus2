package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "treatment_services_in", type = "treatment_services")
public class ESTreatmentServiceDocument {

	@Id
	private String id;

	@Field(type = FieldType.String)
	private String name;

	@Field(type = FieldType.String)
	private String speciality;

	@Field(type = FieldType.String)
	private String locationId;

	@Field(type = FieldType.String)
	private String hospitalId;

	@Field(type = FieldType.String)
	private String doctorId;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.Double)
	private double cost = 0.0;

	@Field(type = FieldType.String)
	private String treatmentCode;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getTreatmentCode() {
		return treatmentCode;
	}

	public void setTreatmentCode(String treatmentCode) {
		this.treatmentCode = treatmentCode;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	@Field(type = FieldType.Long)
	private long rankingCount = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	@Override
	public String toString() {
		return "ESTreatmentServiceDocument [id=" + id + ", name=" + name + ", speciality=" + speciality
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
				+ ", discarded=" + discarded + ", updatedTime=" + updatedTime + ", cost=" + cost + ", treatmentCode="
				+ treatmentCode + ", rankingCount=" + rankingCount + "]";
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
	public boolean equals(Object obj) {
		ESTreatmentServiceDocument treatmentServiceDocument = (ESTreatmentServiceDocument) obj;
		return this.treatmentCode.equalsIgnoreCase(treatmentServiceDocument.treatmentCode);
	}

	@Override
	public int hashCode() {
		return this.treatmentCode.hashCode();
	}
}
