package com.dpdocter.beans;

import java.util.List;

public class PrescriptionItemAndAdviceAdd {

	private String id;

	private List<PrescriptionAddItem> items;

	private String advice;

	public List<PrescriptionAddItem> getItems() {
		return items;
	}

	public void setItems(List<PrescriptionAddItem> items) {
		this.items = items;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
