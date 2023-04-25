package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.OrderStatus;

public class TrackingOrderCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId orderId;
	@Field
	private ObjectId productId;
	@Field
	private Long timestamp;
	@Field
	private String city;
	@Field
	private OrderStatus status;
	@Field
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
