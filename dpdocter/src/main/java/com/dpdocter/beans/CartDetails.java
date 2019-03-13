package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class CartDetails extends GenericCollection {

	private String cartId;
	private String userId;
	private String type; // Current cart or save later cart
	private Integer quantity;
	private Long dateOfAction;
	private String textOfWish;
	private Long startDate;
	private Long expiryDate;
	private Float amount;
	private String currency;
	private List<CartProduct> cartProducts;

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Long getDateOfAction() {
		return dateOfAction;
	}

	public void setDateOfAction(Long dateOfAction) {
		this.dateOfAction = dateOfAction;
	}

	public String getTextOfWish() {
		return textOfWish;
	}

	public void setTextOfWish(String textOfWish) {
		this.textOfWish = textOfWish;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Long expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<CartProduct> getCartProducts() {
		return cartProducts;
	}

	public void setCartProducts(List<CartProduct> cartProducts) {
		this.cartProducts = cartProducts;
	}

}
