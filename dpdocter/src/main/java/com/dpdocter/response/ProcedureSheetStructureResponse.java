package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.ProcedureConsentFormFields;
import com.dpdocter.beans.ProcedureConsentFormStructure;

public class ProcedureSheetStructureResponse {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String procedureName;
	private ProcedureConsentFormStructure procedureConsentFormStructure;
	private List<ImageURLResponse> diagrams;
	private List<Map<String, ProcedureConsentFormFields>> procedureSheetFields;
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

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public ProcedureConsentFormStructure getProcedureConsentFormStructure() {
		return procedureConsentFormStructure;
	}

	public void setProcedureConsentFormStructure(ProcedureConsentFormStructure procedureConsentFormStructure) {
		this.procedureConsentFormStructure = procedureConsentFormStructure;
	}

	public List<ImageURLResponse> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<ImageURLResponse> diagrams) {
		this.diagrams = diagrams;
	}

	public List<Map<String, ProcedureConsentFormFields>> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	public void setProcedureSheetFields(List<Map<String, ProcedureConsentFormFields>> procedureSheetFields) {
		this.procedureSheetFields = procedureSheetFields;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
