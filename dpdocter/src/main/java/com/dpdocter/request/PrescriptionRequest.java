package com.dpdocter.request;

import java.util.List;

public class PrescriptionRequest {

	private String prescriptionId;
	
	private String prescriptionURL;
	
	private List<DrugRequest> drugs;

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public String getPrescriptionURL() {
		return prescriptionURL;
	}

	public void setPrescriptionURL(String prescriptionURL) {
		this.prescriptionURL = prescriptionURL;
	}

	public List<DrugRequest> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<DrugRequest> drugs) {
		this.drugs = drugs;
	}

	@Override
	public String toString() {
		return "PrescriptionRequest [prescriptionId=" + prescriptionId + ", prescriptionURL=" + prescriptionURL
				+ ", drugs=" + drugs + "]";
	}
	
	
}
