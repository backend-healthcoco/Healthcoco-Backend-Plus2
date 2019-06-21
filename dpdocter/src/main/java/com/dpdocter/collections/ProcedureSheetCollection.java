package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureSheetField;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "procedure_sheet_cl")
public class ProcedureSheetCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId procedureSheetStructureId;
	@Field
	private String procedureName;
	@Field
	private ProcedureConsentForm procedureConsentForm;
	@Field
	private List<ImageURLResponse> diagrams;
	@Field
	private List<Map<String, ProcedureSheetField>> procedureSheetFields;
	@Field
	private Boolean discarded = false;
	@Field
	private String type;
	@Field
	private Boolean isPatientDiscarded = false;

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

	public ObjectId getProcedureSheetStructureId() {
		return procedureSheetStructureId;
	}

	public void setProcedureSheetStructureId(ObjectId procedureSheetStructureId) {
		this.procedureSheetStructureId = procedureSheetStructureId;
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "ProcedureSheetCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", procedureSheetStructureId="
				+ procedureSheetStructureId + ", procedureName=" + procedureName + ", procedureConsentForm="
				+ procedureConsentForm + ", diagrams=" + diagrams + ", procedureSheetFields=" + procedureSheetFields
				+ ", discarded=" + discarded + ", type=" + type + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
