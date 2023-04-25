package com.dpdocter.enums;

public enum OrderStatus {

	PENDING("PENDING"), PLACED("PLACED"), SCHEDULED("SCHEDULED"), PROCESSING("PROCESSING"), RESULTS("RESULTS"),
	PICKED_UP("PICKED_UP"), DISPATCHED("DISPATCHED"), PACKED("PACKED"), OUT_FOR_DELIVERY("OUT_FOR_DELIVERY"),
	DELIVERED("DELIVERED"), CONFIRMED("CONFIRMED");

	private String status;

	public String getStatus() {
		return status;
	}

	private OrderStatus(String status) {
		this.status = status;
	}

}
