package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RecordsFile;
@Document(collection="nutrition_record_cl")
public class NutritionRecordCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String uniqueRecordId;
	@Field
	private List<RecordsFile> recordsFiles;
	@Field
	private String recordsLabel;
	@Field
	private String explanation;
	@Field
	private Boolean shareWithPatient = false;
	@Field
	private String patientId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUniqueRecordId() {
		return uniqueRecordId;
	}

	public void setUniqueRecordId(String uniqueRecordId) {
		this.uniqueRecordId = uniqueRecordId;
	}

	public List<RecordsFile> getRecordsFiles() {
		return recordsFiles;
	}

	public void setRecordsFiles(List<RecordsFile> recordsFiles) {
		this.recordsFiles = recordsFiles;
	}

	public String getRecordsLabel() {
		return recordsLabel;
	}

	public void setRecordsLabel(String recordsLabel) {
		this.recordsLabel = recordsLabel;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Boolean getShareWithPatient() {
		return shareWithPatient;
	}

	public void setShareWithPatient(Boolean shareWithPatient) {
		this.shareWithPatient = shareWithPatient;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "NutritionRecordCollection [id=" + id + ", uniqueRecordId=" + uniqueRecordId + ", recordsFiles="
				+ recordsFiles + ", recordsLabel=" + recordsLabel + ", explanation=" + explanation
				+ ", shareWithPatient=" + shareWithPatient + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded
				+ ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
