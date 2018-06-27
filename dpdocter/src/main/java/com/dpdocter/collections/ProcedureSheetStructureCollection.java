package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.ProcedureConsentFormStructure;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "procedure_sheet_structure_cl")
public class ProcedureSheetStructureCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId doctorId;
	private ObjectId locationId;
	private ObjectId hospitalId;
	private ProcedureConsentFormStructure procedureConsentFormStructure;
	private List<ImageURLResponse> diagrams;
	private List<String> procedureSheetFields;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
