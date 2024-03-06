package com.dpdocter.beans;

public class NDHMRecordDataSubject {

	private String reference;
	private String display;
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	@Override
	public String toString() {
		return "NDHMRecordDataSubject [reference=" + reference + ", display=" + display + "]";
	}
}
