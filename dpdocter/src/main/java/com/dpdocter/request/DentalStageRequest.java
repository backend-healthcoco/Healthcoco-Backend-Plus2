package com.dpdocter.request;

public class DentalStageRequest {

	private String stage;
	private Long pickupTime;
	private Long deliveryTime;
	private String authorisedPerson;
	private String staffId;

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

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	@Override
	public String toString() {
		return "DentalStageRequest [stage=" + stage + ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime
				+ ", authorisedPerson=" + authorisedPerson + ", staffId=" + staffId + "]";
	}

}
