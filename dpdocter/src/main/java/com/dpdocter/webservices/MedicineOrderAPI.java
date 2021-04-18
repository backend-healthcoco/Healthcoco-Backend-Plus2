package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.beans.UserCart;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DrugCodeListRequest;
import com.dpdocter.request.MedicineOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;
import com.dpdocter.request.MedicineOrderRxImageRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.MedicineOrderService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
(PathProxy.ORDER_MEDICINE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.ORDER_MEDICINE_BASE_URL, description = "Endpoint for records")
public class MedicineOrderAPI {
	
	@Autowired
	private MedicineOrderService medicineOrderService;

	@PostMapping(value = PathProxy.OrderMedicineUrls.UPLOAD_PRESCRIPTION)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.OrderMedicineUrls.UPLOAD_PRESCRIPTION, notes = PathProxy.OrderMedicineUrls.UPLOAD_PRESCRIPTION)
	public Response<ImageURLResponse> saveRecordsImage(@RequestParam("file") MultipartFile file, @PathVariable("patientId")  String patientId) {
		ImageURLResponse imageURL = medicineOrderService.saveRXMedicineOrderImage(file, patientId);
		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(imageURL);
		return response;
	}
	
	@PostMapping
	(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX)
	public Response<MedicineOrder> addEditRX(MedicineOrderRXAddEditRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditRx(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping
	(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX_IMAGE)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX_IMAGE, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_RX_IMAGE)
	public Response<MedicineOrder> addEditRXImage(MedicineOrderRxImageRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditRxImage(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	
	@PostMapping
	(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_ADDRESS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_ADDRESS, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_ADDRESS)
	public Response<MedicineOrder> addEditAddress(MedicineOrderAddEditAddressRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditAddress(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping
	(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PREFERENCE)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PREFERENCE, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PREFERENCE)
	public Response<MedicineOrder> addEditPreference(MedicineOrderPreferenceAddEditRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditPreferences(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@PostMapping
	(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PAYMENT)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PAYMENT, notes = PathProxy.OrderMedicineUrls.MEDICINE_ORDER_ADD_EDIT_PAYMENT)
	public Response<MedicineOrder> addEditPayment(MedicineOrderPaymentAddEditRequest request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.addeditPayment(request);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.GET_BY_ID)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_BY_ID, notes = PathProxy.OrderMedicineUrls.GET_BY_ID)
	public Response<MedicineOrder> getOrderById(@PathVariable("id") String id) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.getOrderById(id);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.UPDATE_STATUS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.UPDATE_STATUS, notes = PathProxy.OrderMedicineUrls.UPDATE_STATUS)
	public Response<MedicineOrder> updateStatus(@PathVariable("id") String id , @RequestParam("status") OrderStatus status ) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		MedicineOrder medicineOrder = medicineOrderService.updateStatus(id, status);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setData(medicineOrder);
		return response;
	}
	
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.DISCARD_MEDICINE_ORDER)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.DISCARD_MEDICINE_ORDER, notes = PathProxy.OrderMedicineUrls.DISCARD_MEDICINE_ORDER)
	public Response<Boolean> discardOrder(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded ) {

		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		Boolean status = medicineOrderService.discardOrder(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}
	
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.PATIENT_GET_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.PATIENT_GET_LIST, notes = PathProxy.OrderMedicineUrls.PATIENT_GET_LIST)
	public Response<MedicineOrder> getPatientOrderList(@PathVariable(value = "patientId") String patientId ,@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime ,  @RequestParam(value = "searchTerm") String searchTerm , 
			@RequestParam(value = "page") int page ,  @RequestParam(value = "size") int size , @MatrixParam(value = "status") List<String> status ) {

		if(DPDoctorUtils.allStringsEmpty(patientId))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}

		List<MedicineOrder> medicineOrders = medicineOrderService.getOrderList(patientId, updatedTime, searchTerm, page, size, status);
		Response<MedicineOrder> response = new Response<MedicineOrder>();
		response.setDataList(medicineOrders);
		return response;
	}
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.GET_DRUG_INFO_LIST)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_DRUG_INFO_LIST, notes = PathProxy.OrderMedicineUrls.GET_DRUG_INFO_LIST)
	public Response<DrugInfo> getGetDrugInfoList(@RequestParam("page") int page , @RequestParam("size") int size , @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,@RequestParam("searchTerm") String searchTerm,@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		
		List<DrugInfo> drugInfos = medicineOrderService.getDrugInfo(page, size, updatedTime, searchTerm, discarded);
		Response<DrugInfo> response = new Response<DrugInfo>();
		response.setDataList(drugInfos);
		return response;
	}
	
	@PostMapping
	(value = PathProxy.OrderMedicineUrls.ADD_EDIT_USER_CART)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ADD_EDIT_USER_CART, notes = PathProxy.OrderMedicineUrls.ADD_EDIT_USER_CART)
	public Response<UserCart> addEditUserCart(UserCart request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		UserCart userCart = medicineOrderService.addeditUserCart(request);
		Response<UserCart> response = new Response<UserCart>();
		response.setData(userCart);
		return response;
	}
	
	
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.GET_CART_BY_ID)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_CART_BY_ID, notes = PathProxy.OrderMedicineUrls.GET_CART_BY_ID)
	public Response<UserCart> getUserCartById(@PathVariable("id") String id) {

		if(id == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		UserCart userCart = medicineOrderService.getUserCartById(id);
		Response<UserCart> response = new Response<UserCart>();
		response.setData(userCart);
		return response;
	}
	
	@GetMapping
	(value = PathProxy.OrderMedicineUrls.GET_CART_BY_USER_ID)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_CART_BY_USER_ID, notes = PathProxy.OrderMedicineUrls.GET_CART_BY_USER_ID)
	public Response<UserCart> getUserCartByUserId(@PathVariable("id") String id) {

		if(id == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		UserCart userCart = medicineOrderService.getUserCartByuserId(id);
		Response<UserCart> response = new Response<UserCart>();
		response.setData(userCart);
		return response;
	}
	

	@PostMapping
	(value = PathProxy.OrderMedicineUrls.ADD_EDIT_TRACKING_DETAILS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.ADD_EDIT_TRACKING_DETAILS, notes = PathProxy.OrderMedicineUrls.ADD_EDIT_TRACKING_DETAILS)
	public Response<TrackingOrder> addEditTrackingDetails(TrackingOrder request) {

		if(request == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		TrackingOrder trackingOrder = medicineOrderService.addeditTrackingDetails(request);
		Response<TrackingOrder> response = new Response<TrackingOrder>();
		response.setData(trackingOrder);
		return response;
	}
	
	

	@GetMapping
	(value = PathProxy.OrderMedicineUrls.GET_TRACKING_DETAILS)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_TRACKING_DETAILS, notes = PathProxy.OrderMedicineUrls.GET_TRACKING_DETAILS)
	public Response<TrackingOrder> getTrackingDetailsByOrder(@PathVariable("orderId") String orderId , @RequestParam("page") int page , @RequestParam("size") int size , @DefaultValue("0") @RequestParam("updatedTime") String updatedTime,@RequestParam("searchTerm") String searchTerm) {

		if(orderId == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		List<TrackingOrder> trackingOrders = medicineOrderService.getTrackingList(orderId, updatedTime, searchTerm, page, size);
		Response<TrackingOrder> response = new Response<TrackingOrder>();
		response.setDataList(trackingOrders);
		return response;
	}
	
	@DeleteMapping
	(value = PathProxy.OrderMedicineUrls.CLEAR_CART)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.CLEAR_CART, notes = PathProxy.OrderMedicineUrls.CLEAR_CART)
	public Response<UserCart> clearCart(@PathVariable("id") String id) {

		if(id == null)
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		
		UserCart userCart = medicineOrderService.clearCart(id);
		Response<UserCart> response = new Response<UserCart>();
		response.setData(userCart);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.OrderMedicineUrls.GET_DRUGS_BY_CODE)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_DRUGS_BY_CODE, notes = PathProxy.OrderMedicineUrls.GET_DRUGS_BY_CODE)
	public Response<DrugInfo> getDrugDetails(@PathVariable("drugCode") String drugCode) {
		if (drugCode == null) {
			//logger.error("DrugId Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Drug code Is NULL");
		}
		DrugInfo drugAddEditResponse = medicineOrderService.getDrugByDrugCode(drugCode.trim());
		Response<DrugInfo> response = new Response<DrugInfo>();
		response.setData(drugAddEditResponse);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.OrderMedicineUrls.GET_DRUGS_BY_CODES)
	@ApiOperation(value = PathProxy.OrderMedicineUrls.GET_DRUGS_BY_CODES, notes = PathProxy.OrderMedicineUrls.GET_DRUGS_BY_CODES)
	public Response<DrugInfo> getDrugDetails(DrugCodeListRequest request) {
		if (request == null || request.getDrugCodes() == null) {
			//logger.error("DrugId Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Drug codes are NULL");
		}
		List<DrugInfo> drugAddEditResponse = medicineOrderService.getDrugByDrugCodes(request);
		Response<DrugInfo> response = new Response<DrugInfo>();
		response.setDataList(drugAddEditResponse);
		return response;
	}
}
