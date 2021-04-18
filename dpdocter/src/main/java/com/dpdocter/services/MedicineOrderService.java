package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.beans.UserCart;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.request.DrugCodeListRequest;
import com.dpdocter.request.MedicineOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;
import com.dpdocter.request.MedicineOrderRxImageRequest;
import com.dpdocter.request.UpdateOrderStatusRequest;
import com.dpdocter.response.ImageURLResponse;

public interface MedicineOrderService {

	ImageURLResponse saveRXMedicineOrderImage(MultipartFile file, String patientIdString);

	MedicineOrder addeditRx(MedicineOrderRXAddEditRequest request);

	MedicineOrder addeditAddress(MedicineOrderAddEditAddressRequest request);

	MedicineOrder addeditPayment(MedicineOrderPaymentAddEditRequest request);

	MedicineOrder addeditPreferences(MedicineOrderPreferenceAddEditRequest request);

	MedicineOrder updateStatus(String id, OrderStatus status);

	Boolean discardOrder(String id, Boolean discarded);

	MedicineOrder getOrderById(String id);

	//List<MedicineOrder> getOrderList(String patientId, String updatedTime, String searchTerm, int page, int size);

	UserCart addeditUserCart(UserCart request);

	UserCart getUserCartById(String id);

	UserCart getUserCartByuserId(String id);

	UserCart clearCart(String id);

	TrackingOrder addeditTrackingDetails(TrackingOrder request);

	List<TrackingOrder> getTrackingList(String orderId, String updatedTime, String searchTerm, int page, int size);

	List<DrugInfo> getDrugInfo(int page, int size, String updatedTime, String searchTerm, Boolean discarded);

	List<MedicineOrder> getOrderList(String patientId, String updatedTime, String searchTerm, int page, int size,
			List<String> status);

	MedicineOrder updateStatus(UpdateOrderStatusRequest request);

	MedicineOrder addeditRxImage(MedicineOrderRxImageRequest request);

	List<DrugInfo> getDrugByDrugCodes(DrugCodeListRequest request);

	DrugInfo getDrugByDrugCode(String drugCode);

}
