package com.dpdocter.beans;

public class DentalStage {

	private String stage;
	private Long pickupTime;
	private Long deliveryTime;
	private String authorisedPerson;

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Long getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Long pickupTime) {
		this.pickupTime = pickupTime;
	}

	public Long getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getAuthorisedPerson() {
		return authorisedPerson;
	}

	public void setAuthorisedPerson(String authorisedPerson) {
		this.authorisedPerson = authorisedPerson;
	}

	@Override
	public String toString() {
		return "DentalStage [stage=" + stage + ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime
				+ ", authorisedPerson=" + authorisedPerson + "]";
	}

}
