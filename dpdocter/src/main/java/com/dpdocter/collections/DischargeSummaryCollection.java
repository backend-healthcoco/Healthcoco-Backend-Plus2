package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Treatment;

@Document(collection = "discharge_summary_cl")
public class DischargeSummaryCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private List<ClinicalNotes> clinicalNotes;
	@Field
	private List<Prescription> prescriptions;
	@Field
	private List<Treatment> treatments;
	@Field
	private Long admissionDate;
	@Field
	private Long dischargeDate;
	@Field
	private String labourNotes;
	@Field
	private String babyWeight;
	@Field
	private String babyNotes;
	@Field
	private String conditionsAtDischarge;
	@Field
	private String summary;

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

	public List<ClinicalNotes> getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(List<ClinicalNotes> clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public List<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}

	public Long getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Long admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Long getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Long dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public String getLabourNotes() {
		return labourNotes;
	}

	public void setLabourNotes(String labourNotes) {
		this.labourNotes = labourNotes;
	}

	public String getBabyWeight() {
		return babyWeight;
	}

	public void setBabyWeight(String babyWeight) {
		this.babyWeight = babyWeight;
	}

	public String getBabyNotes() {
		return babyNotes;
	}

	public void setBabyNotes(String babyNotes) {
		this.babyNotes = babyNotes;
	}

	public String getConditionsAtDischarge() {
		return conditionsAtDischarge;
	}

	public void setConditionsAtDischarge(String conditionsAtDischarge) {
		this.conditionsAtDischarge = conditionsAtDischarge;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		return "DischargeSummaryCollection [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", clinicalNotes=" + clinicalNotes
				+ ", prescriptions=" + prescriptions + ", treatments=" + treatments + ", admissionDate=" + admissionDate
				+ ", dischargeDate=" + dischargeDate + ", labourNotes=" + labourNotes + ", babyWeight=" + babyWeight
				+ ", babyNotes=" + babyNotes + ", conditionsAtDischarge=" + conditionsAtDischarge + ", summary="
				+ summary + "]";
	}

}
