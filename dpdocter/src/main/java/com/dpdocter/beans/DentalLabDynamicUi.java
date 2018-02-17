package com.dpdocter.beans;

public class DentalLabDynamicUi {

	private String id;
	private DentalLabDynamicField dentalLabDynamicField;
	private String dentalLabId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DentalLabDynamicField getDentalLabDynamicField() {
		return dentalLabDynamicField;
	}

	public void setDentalLabDynamicField(DentalLabDynamicField dentalLabDynamicField) {
		this.dentalLabDynamicField = dentalLabDynamicField;
	}

	public String getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(String dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	@Override
	public String toString() {
		return "DentalLabDynamicUi [id=" + id + ", dentalLabDynamicField=" + dentalLabDynamicField + ", dentalLabId="
				+ dentalLabId + "]";
	}

}
