package com.dpdocter.beans;

import java.util.List;

public class PrescriptionAndAdvice {
	private List<PrescriptionItem> items;

	private String advice;

	public List<PrescriptionItem> getItems() {
		return items;
	}

	public void setItems(List<PrescriptionItem> items) {
		this.items = items;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}
	

}
