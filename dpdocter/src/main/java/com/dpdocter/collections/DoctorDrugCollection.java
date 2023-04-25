package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.GenericCode;

@Document(collection = "doctor_drug_cl")
public class DoctorDrugCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId drugId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId locationId;

	@Field
	private long rankingCount = 0;

	@Field
	private Boolean discarded = false;

	@Field
	private List<String> genericCodes;

	@Field
	private Duration duration;

	@Field
	private String dosage;

	@Field
	private List<Long> dosageTime;

	@Field
	private List<DrugDirection> direction;

	@Field
	private List<GenericCode> genericNames;

	public DoctorDrugCollection() {
	}

	public DoctorDrugCollection(ObjectId drugId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			long rankingCount, Boolean discarded, Duration duration, String dosage, List<Long> dosageTime,
			List<DrugDirection> direction, List<GenericCode> genericNames, String createdBy) {
		this.drugId = drugId;
		this.doctorId = doctorId;
		this.hospitalId = hospitalId;
		this.locationId = locationId;
		this.rankingCount = rankingCount;
		this.discarded = discarded;
		this.duration = duration;
		this.dosage = dosage;
		this.dosageTime = dosageTime;
		this.direction = direction;
		this.genericNames = genericNames;
		super.setCreatedBy(createdBy);
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDrugId() {
		return drugId;
	}

	public void setDrugId(ObjectId drugId) {
		this.drugId = drugId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getGenericCodes() {
		return genericCodes;
	}

	public void setGenericCodes(List<String> genericCodes) {
		this.genericCodes = genericCodes;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
	}

	public List<DrugDirection> getDirection() {
		return direction;
	}

	public void setDirection(List<DrugDirection> direction) {
		this.direction = direction;
	}

	public List<GenericCode> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}

	@Override
	public String toString() {
		return "DoctorDrugCollection [id=" + id + ", drugId=" + drugId + ", doctorId=" + doctorId + ", hospitalId="
				+ hospitalId + ", locationId=" + locationId + ", rankingCount=" + rankingCount + ", discarded="
				+ discarded + ", genericCodes=" + genericCodes + ", duration=" + duration + ", dosage=" + dosage
				+ ", dosageTime=" + dosageTime + ", direction=" + direction + ", genericNames=" + genericNames + "]";
	}

}
