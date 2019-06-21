package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class UserCart extends GenericCollection {

	private String id;
	private String userId;
	private String type = "CURRENT"; // Current cart or save later cart
	private Integer quantity;
	private Long dateOfAction;
	private String textOfWish;
	private Long startDate;
	private Long expiryDate;
	private List<MedicineOrderAddEditItems> items;
	private List<MedicineOrderImages> rxImage;
	private Float totalAmount;
	private Float discountedAmount;
	private Float discountedPercentage;
	private Float finalAmount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<MedicineOrderAddEditItems> getItems() {
		return items;
	}

	public void setItems(List<MedicineOrderAddEditItems> items) {
		this.items = items;
	}

	public List<MedicineOrderImages> getRxImage() {
		return rxImage;
	}

	public void setRxImage(List<MedicineOrderImages> rxImage) {
		this.rxImage = rxImage;
	}

	public Float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Float getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Float discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public Float getDiscountedPercentage() {
		return discountedPercentage;
	}

	public void setDiscountedPercentage(Float discountedPercentage) {
		this.discountedPercentage = discountedPercentage;
	}

	public Float getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(Float finalAmount) {
		this.finalAmount = finalAmount;
	}

}
