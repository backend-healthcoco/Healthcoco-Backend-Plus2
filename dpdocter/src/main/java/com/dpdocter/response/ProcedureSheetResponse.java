package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureConsentFormFields;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.collections.GenericCollection;

public class ProcedureSheetResponse extends GenericCollection {

	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String procedureName;
	private ProcedureConsentForm procedureConsentForm;
	private List<ImageURLResponse> diagrams;
	private Map<String, String> procedureSheetFields;
	private Boolean discarded = false;
	private PatientShortCard patient;

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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public ProcedureConsentForm getProcedureConsentForm() {
		return procedureConsentForm;
	}

	public void setProcedureConsentForm(ProcedureConsentForm procedureConsentForm) {
		this.procedureConsentForm = procedureConsentForm;
	}

	public List<ImageURLResponse> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<ImageURLResponse> diagrams) {
		this.diagrams = diagrams;
	}

	public Map<String, String> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	public void setProcedureSheetFields(Map<String, String> procedureSheetFields) {
		this.procedureSheetFields = procedureSheetFields;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public PatientShortCard getPatient() {
		return patient;
	}

	public void setPatient(PatientShortCard patient) {
		this.patient = patient;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

}
