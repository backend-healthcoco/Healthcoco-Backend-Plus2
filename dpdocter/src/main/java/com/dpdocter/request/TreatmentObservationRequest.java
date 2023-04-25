package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Observation;

public class TreatmentObservationRequest {

	private PatientTreatmentAddEditRequest Treatments;

	private List<Observation> observations;

	public PatientTreatmentAddEditRequest getTreatments() {
		return Treatments;
	}

	public void setTreatments(PatientTreatmentAddEditRequest treatments) {
		Treatments = treatments;
	}

	public List<Observation> getObservations() {
		return observations;
	}

	public void setObservations(List<Observation> observations) {
		this.observations = observations;
	}

}
