package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

import common.util.web.JacksonUtil;

public class ProcedureSheet extends GenericCollection {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String procedureName;
	private ProcedureConsentForm procedureConsentForm;
	private List<ImageURLResponse> diagrams;
	private List<Map<String, String>> procedureSheetFields;
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

	public List<Map<String, String>> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	public void setProcedureSheetFields(List<Map<String, String>> procedureSheetFields) {
		this.procedureSheetFields = procedureSheetFields;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

/*	public static void main(String[] args) {
		ProcedureSheet procedureSheet = new ProcedureSheet();
		ProcedureConsentForm procedureConsentForm = new ProcedureConsentForm();
		Map<String, String> hco = new HashMap<String, String>();

		// enter name/url pair
		hco.put("HCO2", "healthcoco.com");
		hco.put("Plus", "plus.healthcoco.com");
		procedureConsentForm.setHeaderFields(hco);
		procedureConsentForm.setFooterFields(hco);
		procedureConsentForm.setBody("Testing procedure sheet add");
		procedureSheet.setProcedureConsentForm(procedureConsentForm);
		procedureSheet.setProcedureName("Test Procedure");
		ArrayList<Map<String, String>> arrayList = new ArrayList<>();
		arrayList.add(hco);
		arrayList.add(hco);
		procedureSheet.setProcedureSheetFields(arrayList);
		System.out.println(JacksonUtil.obj2Json(procedureSheet));
	}*/

}
