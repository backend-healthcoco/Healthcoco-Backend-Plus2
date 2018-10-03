package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureSheetField;
import com.dpdocter.collections.GenericCollection;

public class ProcedureSheetResponse extends GenericCollection {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String procedureSheetStructureId;
	private String procedureName;
	private ProcedureConsentForm procedureConsentForm;
	private List<ImageURLResponse> diagrams;
	private List<Map<String, ProcedureSheetField>> procedureSheetFields;
	private Boolean discarded = false;
	private PatientShortCard patient;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<Map<String, ProcedureSheetField>> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	public void setProcedureSheetFields(List<Map<String, ProcedureSheetField>> procedureSheetFields) {
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

	public String getProcedureSheetStructureId() {
		return procedureSheetStructureId;
	}

	public void setProcedureSheetStructureId(String procedureSheetStructureId) {
		this.procedureSheetStructureId = procedureSheetStructureId;
	}

}
