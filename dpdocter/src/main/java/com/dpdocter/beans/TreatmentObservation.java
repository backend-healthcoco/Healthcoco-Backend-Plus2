package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.request.TreatmentRequest;
import com.dpdocter.response.TreatmentResponse;

public class TreatmentObservation {

	private List<Observation> observations;
	
	private List<ClinicalNotesTreatment> treatments;

	public List<Observation> getObservations() {
		return observations;
	}

	public void setObservations(List<Observation> observations) {
		this.observations = observations;
	}

	public List<ClinicalNotesTreatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<ClinicalNotesTreatment> treatments) {
		this.treatments = treatments;
	}
	
	

	

	
	
	
	
}
