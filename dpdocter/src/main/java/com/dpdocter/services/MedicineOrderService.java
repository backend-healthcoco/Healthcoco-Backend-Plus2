package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.request.MedicinOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface MedicineOrderService {

	ImageURLResponse saveRXMedicineOrderImage(FormDataBodyPart file, String patientIdString);

	MedicineOrder addeditRx(MedicineOrderRXAddEditRequest request);

	MedicineOrder addeditAddress(MedicinOrderAddEditAddressRequest request);

	MedicineOrder addeditPayment(MedicineOrderPaymentAddEditRequest request);

	MedicineOrder addeditPreferences(MedicineOrderPreferenceAddEditRequest request);

	MedicineOrder updateStatus(String id, OrderStatus status);

	Boolean discardOrder(String id, Boolean discarded);

	MedicineOrder getOrderById(String id);

	List<MedicineOrder> getOrderList(String patientId, String updatedTime, String searchTerm, int page, int size);

}
