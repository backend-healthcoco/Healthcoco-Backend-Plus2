package com.dpdocter.beans;

import java.util.List;

public class NDHMRecordDataCode {

	private String text;
	List<NDHMRecordDataCoding> coding;

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<NDHMRecordDataCoding> getCoding() {
		return coding;
	}

	public void setCoding(List<NDHMRecordDataCoding> coding) {
		this.coding = coding;
	}

	@Override
	public String toString() {
		return "NDHMRecordDataMedicationCodeableConcept [text=" + text +",coding=" + coding + "]";
	}
}
