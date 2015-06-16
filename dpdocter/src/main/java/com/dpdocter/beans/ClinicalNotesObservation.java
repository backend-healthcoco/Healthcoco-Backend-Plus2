package com.dpdocter.beans;

public class ClinicalNotesObservation {
	private String id;

	private String observation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	@Override
	public String toString() {
		return "ClinicalNotesObservation [id=" + id + ", observation=" + observation + "]";
	}

}
