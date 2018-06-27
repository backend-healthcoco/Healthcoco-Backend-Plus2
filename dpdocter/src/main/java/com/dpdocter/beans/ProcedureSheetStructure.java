package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class ProcedureSheetStructure extends GenericCollection{

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private ProcedureConsentFormStructure procedureConsentFormStructure;
	private List<ImageURLResponse> diagrams;
	private List<String> procedureSheetFields;
	private Boolean discarded = false;

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

	public List<String> getProcedureSheetFields() {
		return procedureSheetFields;
	}

	public void setProcedureSheetFields(List<String> procedureSheetFields) {
		this.procedureSheetFields = procedureSheetFields;
	}

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "ProcudureSheetStructure [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", procedureConsentFormStructure=" + procedureConsentFormStructure
				+ ", diagrams=" + diagrams + ", procedureSheetFields=" + procedureSheetFields + ", discarded="
				+ discarded + "]";
	}

}
