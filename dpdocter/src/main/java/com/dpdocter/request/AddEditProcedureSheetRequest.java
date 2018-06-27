package com.dpdocter.request;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureConsentFormFields;
import com.dpdocter.response.ImageURLResponse;

public class AddEditProcedureSheetRequest {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private ProcedureConsentForm procedureConsentForm;
	private List<ImageURLResponse> diagrams;
	private Map<String, ProcedureConsentFormFields> procedureSheetFields;
	private Boolean discarded = false;

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

	public Map<String, ProcedureConsentFormFields> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	public void setProcedureSheetFields(Map<String, ProcedureConsentFormFields> procedureSheetFields) {
		this.procedureSheetFields = procedureSheetFields;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "AddEditProcedureSheetRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", procedureConsentForm="
				+ procedureConsentForm + ", diagrams=" + diagrams + ", procedureSheetFields=" + procedureSheetFields
				+ ", discarded=" + discarded + "]";
	}

}
