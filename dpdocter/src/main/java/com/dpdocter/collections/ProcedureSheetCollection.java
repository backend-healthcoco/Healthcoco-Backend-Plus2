package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureConsentFormFields;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "procedure_sheet_cl")
public class ProcedureSheetCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId doctorId;
	private ObjectId locationId;
	private ObjectId hospitalId;
	private ObjectId patientId;
	private ProcedureConsentForm procedureConsentForm;
	private List<ImageURLResponse> diagrams;
	private Map<String, ProcedureConsentFormFields> procedureSheetFields;
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
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
		return "ProcedureSheetCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", procedureConsentForm="
				+ procedureConsentForm + ", diagrams=" + diagrams + ", procedureSheetFields=" + procedureSheetFields
				+ ", discarded=" + discarded + "]";
	}

}
