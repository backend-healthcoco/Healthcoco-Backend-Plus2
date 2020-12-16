package com.dpdocter.beans;

import java.util.List;

public class ClinicalNotesTreatment {

	private TreatmentService treatmentService;
	
	private List<Fields> treatmentFields;

	public TreatmentService getTreatmentService() {
		return treatmentService;
	}

	public void setTreatmentService(TreatmentService treatmentService) {
		this.treatmentService = treatmentService;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}
	
	
}
