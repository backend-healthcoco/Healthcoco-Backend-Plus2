package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.beans.MedicineOrderImages;

@Document(collection = "user_cart_cl")
public class UserCartCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId userId;
	@Field
	private String type = "CURRENT"; // Current cart or save later cart
	@Field
	private Integer quantity;
	@Field
	private Long dateOfAction;
	@Field
	private String textOfWish;
	@Field
	private Long startDate;
	@Field
	private Long expiryDate;
	@Field
	private List<MedicineOrderAddEditItems> items;
	@Field
	private List<MedicineOrderImages> rxImage;
	@Field
	private Float totalAmount;
	@Field
	private Float discountedAmount;
	@Field
	private Float discountedPercentage;
	@Field
	private Float finalAmount;

	@Field
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
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

	@Override
	public String toString() {
		return "UserCartCollection [id=" + id + ", userId=" + userId + ", type=" + type + ", quantity=" + quantity
				+ ", dateOfAction=" + dateOfAction + ", textOfWish=" + textOfWish + ", startDate=" + startDate
				+ ", expiryDate=" + expiryDate + ", items=" + items + ", rxImage=" + rxImage + ", totalAmount="
				+ totalAmount + ", discountedAmount=" + discountedAmount + ", discountedPercentage="
				+ discountedPercentage + ", finalAmount=" + finalAmount + "]";
	}

}
