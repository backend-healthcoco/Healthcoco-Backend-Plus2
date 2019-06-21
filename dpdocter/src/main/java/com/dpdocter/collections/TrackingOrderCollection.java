package com.dpdocter.collections;

import org.bson.types.ObjectId;

import com.dpdocter.enums.OrderStatus;

public class TrackingOrderCollection extends GenericCollection{

	private ObjectId id;
	private ObjectId orderId;
	private ObjectId productId;
	private Long timestamp;
	private String city;
	private OrderStatus status;
	private String location;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getOrderId() {
		return orderId;
	}

	public void setOrderId(ObjectId orderId) {
		this.orderId = orderId;
	}

	public ObjectId getProductId() {
		return productId;
	}

	public void setProductId(ObjectId productId) {
		this.productId = productId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
