package com.dpdocter.beans;

import java.util.List;

public class PrescriptionItemAndAdvice {

	private String prescriptionId;

	private String visitId;

	private List<PrescriptionItemDetail> items;

	private String advice;

	public List<PrescriptionItemDetail> getItems() {
		return items;
	}

	public void setItems(List<PrescriptionItemDetail> items) {
		this.items = items;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

}
