package com.dpdocter.request;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.enums.OrderStatus;

public class UpdateOrderStatusRequest {

	private String orderId;
	private OrderStatus status;
	private TrackingOrder trackingOrder;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public TrackingOrder getTrackingOrder() {
		return trackingOrder;
	}

	public void setTrackingOrder(TrackingOrder trackingOrder) {
		this.trackingOrder = trackingOrder;
	}

	@Override
	public String toString() {
		return "UpdateOrderStatusRequest [orderId=" + orderId + ", status=" + status + ", trackingOrder="
				+ trackingOrder + "]";
	}

}
