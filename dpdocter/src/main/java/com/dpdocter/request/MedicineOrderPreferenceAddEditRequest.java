package com.dpdocter.request;

import com.dpdocter.enums.DeliveryPreferences;

public class MedicineOrderPreferenceAddEditRequest {

	private String id;
	private DeliveryPreferences deliveryPreference = DeliveryPreferences.ONE_TIME;
	private Long nextDeliveryDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DeliveryPreferences getDeliveryPreference() {
		return deliveryPreference;
	}

	public void setDeliveryPreference(DeliveryPreferences deliveryPreference) {
		this.deliveryPreference = deliveryPreference;
	}

	public Long getNextDeliveryDate() {
		return nextDeliveryDate;
	}

	public void setNextDeliveryDate(Long nextDeliveryDate) {
		this.nextDeliveryDate = nextDeliveryDate;
	}

}
