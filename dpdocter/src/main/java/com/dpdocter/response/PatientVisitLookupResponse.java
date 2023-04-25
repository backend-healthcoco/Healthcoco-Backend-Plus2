package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.VisitedFor;

public class PatientVisitLookupResponse extends GenericCollection {

	private ObjectId id;

	private String uniqueEmrId;

	private ObjectId patientId;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private Date visitedTime;

	private List<VisitedFor> visitedFor;

	private long total;

	private List<ObjectId> prescriptionId;

	private List<ObjectId> clinicalNotesId;

	private List<ObjectId> treatmentId;

	private List<ObjectId> recordId;

	private ObjectId eyePrescriptionId;

	private List<PrescriptionCollection> prescriptions;

	private List<ClinicalNotesCollection> clinicalNotes;

	private List<PatientTreatmentCollection> treatments;

	private List<RecordsCollection> records;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private UserCollection doctor;

	private LocationCollection location;

	private PatientCollection patient;

	private UserCollection patientUser;

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

	public List<PrescriptionCollection> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<PrescriptionCollection> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public List<ClinicalNotesCollection> getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(List<ClinicalNotesCollection> clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public List<PatientTreatmentCollection> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<PatientTreatmentCollection> treatments) {
		this.treatments = treatments;
	}

	public List<RecordsCollection> getRecords() {
		return records;
	}

	public void setRecords(List<RecordsCollection> records) {
		this.records = records;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
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

	public UserCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}

	public LocationCollection getLocation() {
		return location;
	}

	public void setLocation(LocationCollection location) {
		this.location = location;
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

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public List<ObjectId> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<ObjectId> recordId) {
		this.recordId = recordId;
	}

	public ObjectId getEyePrescriptionId() {
		return eyePrescriptionId;
	}

	public void setEyePrescriptionId(ObjectId eyePrescriptionId) {
		this.eyePrescriptionId = eyePrescriptionId;
	}

	public PatientCollection getPatient() {
		return patient;
	}

	public void setPatient(PatientCollection patient) {
		this.patient = patient;
	}

	public UserCollection getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(UserCollection patientUser) {
		this.patientUser = patientUser;
	}

	@Override
	public String toString() {
		return "PatientVisitLookupResponse [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + ", total=" + total
				+ ", prescriptionId=" + prescriptionId + ", clinicalNotesId=" + clinicalNotesId + ", treatmentId="
				+ treatmentId + ", recordId=" + recordId + ", eyePrescriptionId=" + eyePrescriptionId
				+ ", prescriptions=" + prescriptions + ", clinicalNotes=" + clinicalNotes + ", treatments=" + treatments
				+ ", records=" + records + ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate="
				+ fromDate + ", doctor=" + doctor + ", location=" + location + ", patient=" + patient + ", patientUser="
				+ patientUser + "]";
	}

}
