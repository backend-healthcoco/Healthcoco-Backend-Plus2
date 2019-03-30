package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.beans.MedicineOrderImages;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.beans.Vendor;
import com.dpdocter.enums.DeliveryPreferences;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.PaymentMode;

public class MedicineOrderResponse {

	private String id;
	private String patientId;
	private String vendorId;
	private UserAddress shippingAddress;
	private UserAddress billingAddress;
	private Vendor vendor;
	private String uniqueOrderId;
	private List<MedicineOrderAddEditItems> items;
	private List<MedicineOrderImages> rxImage;
	private Float totalAmount;
	private Float discountedAmount;
	private Float discountedPercentage;
	private Float finalAmount;
	private Float deliveryCharges;
	private OrderStatus orderStatus = OrderStatus.PENDING;
	private PaymentMode paymentMode = PaymentMode.COD;
	private DeliveryPreferences deliveryPreference = DeliveryPreferences.ONE_TIME;
	private Long nextDeliveryDate;
	private Long deliveredByDate;
	private String callingPreference;
	private Boolean discarded = false;

	
}
