package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.beans.MedicineOrderImages;

public class MedicineOrderRXAddEditRequest {

	private String id;
	private String patientId;
	private List<MedicineOrderAddEditItems> items;
	private List<MedicineOrderImages> rxImage;
	private Boolean isPrescriptionRequired;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public List<MedicineOrderAddEditItems> getItems() {
		return items;
	}

	public void setItems(List<MedicineOrderAddEditItems> items) {
		this.items = items;
	}

	public List<MedicineOrderImages> getRxImage() {
		return rxImage;
	}

	public void setRxImage(List<MedicineOrderImages> rxImage) {
		this.rxImage = rxImage;
	}

	public Boolean getIsPrescriptionRequired() {
		return isPrescriptionRequired;
	}

	public void setIsPrescriptionRequired(Boolean isPrescriptionRequired) {
		this.isPrescriptionRequired = isPrescriptionRequired;
	}


}
