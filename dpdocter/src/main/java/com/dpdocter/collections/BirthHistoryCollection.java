package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.BirthType;
import com.dpdocter.enums.GestationType;

@Document(collection = "birth_history_cl")
public class BirthHistoryCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String birthPlace;
	@Field
	private String obstetricianName;
	@Field
	private GestationType gestationType;
	@Field
	private Integer prematureWeeks;
	@Field
	private BirthType birthType;
	@Field
	private Integer height;
	@Field
	private Double weight;
	@Field
	private Integer birthOrder;
	@Field
	private String birthProblem;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getObstetricianName() {
		return obstetricianName;
	}

	public void setObstetricianName(String obstetricianName) {
		this.obstetricianName = obstetricianName;
	}

	public GestationType getGestationType() {
		return gestationType;
	}

	public void setGestationType(GestationType gestationType) {
		this.gestationType = gestationType;
	}

	public Integer getPrematureWeeks() {
		return prematureWeeks;
	}

	public void setPrematureWeeks(Integer prematureWeeks) {
		this.prematureWeeks = prematureWeeks;
	}

	public BirthType getBirthType() {
		return birthType;
	}

	public void setBirthType(BirthType birthType) {
		this.birthType = birthType;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getBirthOrder() {
		return birthOrder;
	}

	public void setBirthOrder(Integer birthOrder) {
		this.birthOrder = birthOrder;
	}

	public String getBirthProblem() {
		return birthProblem;
	}

	public void setBirthProblem(String birthProblem) {
		this.birthProblem = birthProblem;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "BirthHistoryCollection [id=" + id + ", birthPlace=" + birthPlace + ", obstetricianName="
				+ obstetricianName + ", gestationType=" + gestationType + ", prematureWeeks=" + prematureWeeks
				+ ", birthType=" + birthType + ", height=" + height + ", weight=" + weight + ", birthOrder="
				+ birthOrder + ", birthProblem=" + birthProblem + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}
