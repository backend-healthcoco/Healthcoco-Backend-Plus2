package com.dpdocter.request;

import java.util.List;

import com.dpdocter.response.ImageURLResponse;

public class PrescriptionRequest {

	private String prescriptionId;

	private ImageURLResponse prescriptionURL;

	private List<DrugRequest> drugs;

	private Integer quantity = 0;

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public ImageURLResponse getPrescriptionURL() {
		return prescriptionURL;
	}

	public void setPrescriptionURL(ImageURLResponse prescriptionURL) {
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
