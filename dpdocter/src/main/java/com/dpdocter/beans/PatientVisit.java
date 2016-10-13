package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.VisitedFor;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatientVisit {
	private String id;

	private String uniqueEmrId;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Date visitedTime;

	private List<VisitedFor> visitedFor;

	private List<String> prescriptionId;

	private List<String> clinicalNotesId;

	private List<String> recordId;

	private List<ObjectId> treatmentId;

	private WorkingHours time;

	private Date fromDate;

	private Date toDate;

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public Date getVisitedTime() {
		return visitedTime;
	}

	public void setVisitedTime(Date visitedTime) {
		this.visitedTime = visitedTime;
	}

	public List<VisitedFor> getVisitedFor() {
		return visitedFor;
	}

	public void setVisitedFor(List<VisitedFor> visitedFor) {
		this.visitedFor = visitedFor;
	}

	public List<String> getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(List<String> prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public List<String> getClinicalNotesId() {
		return clinicalNotesId;
	}

	public void setClinicalNotesId(List<String> clinicalNotesId) {
		this.clinicalNotesId = clinicalNotesId;
	}

	public List<String> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<String> recordId) {
		this.recordId = recordId;
	}

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	@Override
	public String toString() {
		return "PatientVisit [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", patientId=" + patientId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", visitedTime="
				+ visitedTime + ", visitedFor=" + visitedFor + ", prescriptionId=" + prescriptionId
				+ ", clinicalNotesId=" + clinicalNotesId + ", recordId=" + recordId + ", discarded=" + discarded + "]";
	}
}
