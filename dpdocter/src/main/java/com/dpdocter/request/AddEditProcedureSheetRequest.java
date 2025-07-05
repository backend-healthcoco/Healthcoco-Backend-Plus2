package com.dpdocter.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureSheetField;
import com.dpdocter.response.ImageURLResponse;

import common.util.web.JacksonUtil;

public class AddEditProcedureSheetRequest {

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
	private String type;

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

	public String getProcedureSheetStructureId() {
		return procedureSheetStructureId;
	}

	public void setProcedureSheetStructureId(String procedureSheetStructureId) {
		this.procedureSheetStructureId = procedureSheetStructureId;
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

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Map<String, ProcedureSheetField>> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	@Override
	public String toString() {
		return "AddEditProcedureSheetRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", procedureConsentForm="
				+ procedureConsentForm + ", diagrams=" + diagrams + ", procedureSheetFields=" + procedureSheetFields
				+ ", discarded=" + discarded + "]";
	}
	
	public static void main(String[] args) {
		
		List<Map<String, ProcedureSheetField>> list = new ArrayList<Map<String, ProcedureSheetField>>();
		Map<String, ProcedureSheetField> map = new HashMap<String, ProcedureSheetField>();

		ProcedureSheetField field = new ProcedureSheetField();
		field.setSequenceNo(1);
		field.setValue("Test");
		
		map.put("abc", field);
		
		list.add(map);
		
		// it's wrong JSONObject json = new JSONObject(list);
		// if u use list to add data u must be use JSONArray

		
		
	}

}
