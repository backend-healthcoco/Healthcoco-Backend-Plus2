package com.dpdocter.beans;

import java.util.List;

public class PrescriptionItemAndAdvice {

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

}
