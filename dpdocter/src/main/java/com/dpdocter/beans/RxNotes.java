package com.dpdocter.beans;

public class RxNotes {
	private String orderName;
	private String orderType;
	private OrderQuantity orderQty;
	private Double price;
	
	private Discount discount;
	
	private Double discountedPrice;

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public OrderQuantity getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(OrderQuantity orderQty) {
		this.orderQty = orderQty;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	
	
	
}
