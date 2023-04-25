package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.VisitedFor;

@Document(collection = "patient_visit_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class PatientVisitCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;

	@Indexed
	private ObjectId patientId;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private Date visitedTime;

	@Field
	private List<VisitedFor> visitedFor;

	private long total;

	@Field
	private List<ObjectId> prescriptionId;

	@Field
	private List<ObjectId> clinicalNotesId;

	@Field
	private List<ObjectId> treatmentId;

	@Field
	private List<ObjectId> recordId;

	@Field
	private ObjectId eyePrescriptionId;

	@Field
	private String appointmentId;

	@Field
	private WorkingHours time;

	@Field
	private Date fromDate;

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

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<ObjectId> getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(List<ObjectId> prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public List<ObjectId> getClinicalNotesId() {
		return clinicalNotesId;
	}

	public void setClinicalNotesId(List<ObjectId> clinicalNotesId) {
		this.clinicalNotesId = clinicalNotesId;
	}

	public List<ObjectId> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<ObjectId> recordId) {
		this.recordId = recordId;
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

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

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

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public ObjectId getEyePrescriptionId() {
		return eyePrescriptionId;
	}

	public void setEyePrescriptionId(ObjectId eyePrescriptionId) {
		this.eyePrescriptionId = eyePrescriptionId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PatientVisitCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + ", total=" + total
				+ ", prescriptionId=" + prescriptionId + ", clinicalNotesId=" + clinicalNotesId + ", treatmentId="
				+ treatmentId + ", recordId=" + recordId + ", eyePrescriptionId=" + eyePrescriptionId
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate + ", discarded="
				+ discarded + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
