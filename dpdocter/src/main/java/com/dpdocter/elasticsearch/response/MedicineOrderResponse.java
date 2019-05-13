package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.PaymentMode;

public class MedicineOrderResponse {

	private String id;
	private String patientId;
	//private String vendorId; 
	//private UserAddress shippingAddress; 
	//private UserAddress billingAddress;
	 
	// private Vendor vendor;
	private String uniqueOrderId;
	private List<MedicineOrderAddEditItems> items;
	// private List<MedicineOrderImages> rxImage;
	// private Float totalAmount;
	// private Float discountedAmount;
	// private Float discountedPercentage;
	// private Float finalAmount;
	// private Float deliveryCharges;
	private OrderStatus orderStatus = OrderStatus.PENDING;
	private PaymentMode paymentMode = PaymentMode.COD;
	// private DeliveryPreferences deliveryPreference =
	// DeliveryPreferences.ONE_TIME;
	// private Long nextDeliveryDate;
	// private Long deliveredByDate;
	// private String callingPreference;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getUniqueOrderId() {
		return uniqueOrderId;
	}

	public void setUniqueOrderId(String uniqueOrderId) {
		this.uniqueOrderId = uniqueOrderId;
	}

	public List<MedicineOrderAddEditItems> getItems() {
		return items;
	}

	public void setItems(List<MedicineOrderAddEditItems> items) {
		this.items = items;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
