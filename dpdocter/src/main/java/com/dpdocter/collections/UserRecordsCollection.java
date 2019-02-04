package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RecordsFile;
import com.dpdocter.enums.RoleEnum;

@Document(collection = "user_records_cl")
public class UserRecordsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;

	@Field
	private List<RecordsFile> recordsFiles;

	@Field
	private String recordsLabel;

	@Field
	private String explanation;

	@Field
	private ObjectId patientId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private Boolean isVisible = true;

	@Field
	private Boolean discarded = false;

	@Field
	private RoleEnum uploadedBy = RoleEnum.PATIENT;
	
	@Field
	private ObjectId shareWith;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getShareWith() {
		return shareWith;
	}

	public void setShareWith(ObjectId shareWith) {
		this.shareWith = shareWith;
	}

	public RoleEnum getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(RoleEnum uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
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

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<RecordsFile> getRecordsFiles() {
		return recordsFiles;
	}

	public void setRecordsFiles(List<RecordsFile> recordsFiles) {
		this.recordsFiles = recordsFiles;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "UserRecordsCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", recordsFiles=" + recordsFiles
				+ ", recordsLabel=" + recordsLabel + ", explanation=" + explanation + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", isVisible=" + isVisible + ", discarded=" + discarded + ", uploadedBy=" + uploadedBy
				+ ", shareWith=" + shareWith + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
