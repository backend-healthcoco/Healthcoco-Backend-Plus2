package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FlowSheet;

@Document(collection = "flowsheet_cl")
public class FlowsheetCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId dischargeSummaryId;
	@Field
	private String uniqueId;
	@Field
	private List<FlowSheet> flowSheets;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean discarded = false;
	@Field
	private String dischargeSummaryUniqueEMRId;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDischargeSummaryId() {
		return dischargeSummaryId;
	}

	public void setDischargeSummaryId(ObjectId dischargeSummaryId) {
		this.dischargeSummaryId = dischargeSummaryId;
	}

	public List<FlowSheet> getFlowSheets() {
		return flowSheets;
	}

	public void setFlowSheets(List<FlowSheet> flowSheets) {
		this.flowSheets = flowSheets;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDischargeSummaryUniqueEMRId() {
		return dischargeSummaryUniqueEMRId;
	}

	public void setDischargeSummaryUniqueEMRId(String dischargeSummaryUniqueEMRId) {
		this.dischargeSummaryUniqueEMRId = dischargeSummaryUniqueEMRId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "FlowsheetCollection [id=" + id + ", dischargeSummaryId=" + dischargeSummaryId + ", uniqueId=" + uniqueId
				+ ", flowSheets=" + flowSheets + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", dischargeSummaryUniqueEMRId=" + dischargeSummaryUniqueEMRId + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}
