package com.dpdocter.webservices;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Locale;
import com.dpdocter.elasticsearch.services.ESLocaleService;
import com.dpdocter.enums.RecommendationType;
import com.dpdocter.enums.WayOfOrder;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.OrderDrugsRequest;
import com.dpdocter.request.PrescriptionRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.OrderDrugsResponse;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;
import com.dpdocter.response.UserFakeRequestDetailResponse;
import com.dpdocter.services.LocaleService;
import com.dpdocter.services.PharmacyService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.LOCALE_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.LOCALE_BASE_URL, description = "Endpoint for Locale API's")
public class LocaleApi {

	private static final Logger LOGGER = LogManager.getLogger(LocaleApi.class.getName());

	@Autowired
	LocaleService localeService;

	@Autowired
	ESLocaleService esLocaleService;

	@Autowired
	PharmacyService pharmacyService;

	@Autowired
	ServletContext context;

	@Autowired
	private TransactionalManagementService transnationalService;

	@GetMapping
	(PathProxy.LocaleUrls.GET_LOCALE_DETAILS)
	public Response<Locale> getLocaleDetails(@RequestParam("id") String id,
			@RequestParam("contactNumber") String contactNumber, @RequestParam("patientId") String patientId) {
		Response<Locale> response = null;
		Locale locale = null;

		if (DPDoctorUtils.allStringsEmpty(id, contactNumber)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Please provide id or contact number. Both cannot be null");
		}
		if (id != null && !id.isEmpty()) {
			locale = localeService.getLocaleDetails(id, patientId);
			response = new Response<Locale>();
			response.setData(locale);
		} else {
			locale = localeService.getLocaleDetailsByContactDetails(contactNumber, patientId);
			response = new Response<Locale>();
			response.setData(locale);
		}
		return response;
	}

	@GetMapping
	(PathProxy.LocaleUrls.GET_LOCALE_BY_SLUGURL)
	public Response<Locale> getLocaleDetails(@PathVariable("slugUrl") String slugUrl) {
		Response<Locale> response = null;
		Locale locale = null;

		if (DPDoctorUtils.allStringsEmpty(slugUrl)) {
			throw new BusinessException(ServiceError.InvalidInput, "Please provide slugUrl cannot be null");
		}
		if (slugUrl != null && !slugUrl.isEmpty()) {
			locale = localeService.getLocaleDetailBySlugUrl(slugUrl);
			response = new Response<Locale>();
			response.setData(locale);
		}
		return response;
	}

	@PostMapping(PathProxy.LocaleUrls.ADD_USER_REQUEST)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	public Response<UserSearchRequest> addUserRequestInQueue(@RequestParam("file") MultipartFile file,
			@RequestBody UserSearchRequest request) {
		Response<UserSearchRequest> response = null;
		UserSearchRequest status = null;
		ImageURLResponse imageURLResponse = null;
		if (file != null) {
			imageURLResponse = localeService.addRXImageMultipart(file);
			if (request != null) {
				PrescriptionRequest prescriptionRequest = new PrescriptionRequest();
				prescriptionRequest.setPrescriptionURL(imageURLResponse);
				request.setPrescriptionRequest(prescriptionRequest);
			}
		}

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		status = pharmacyService.addSearchRequest(request);
		response = new Response<UserSearchRequest>();
		response.setData(status);

		return response;
	}

	@GetMapping
	(PathProxy.LocaleUrls.GET_PATIENT_ORDER_HISTORY)
	public Response<SearchRequestFromUserResponse> getPatientOrderHistory(@PathVariable("userId") String userId,
			@RequestParam("page") long page, @RequestParam("size") int size) {
		Response<SearchRequestFromUserResponse> response = null;
		List<SearchRequestFromUserResponse> list = null;
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		list = pharmacyService.getPatientOrderHistoryList(userId, page, size);

		response = new Response<SearchRequestFromUserResponse>();
		response.setDataList(list);
		return response;

	}

	@GetMapping
	(PathProxy.LocaleUrls.GET_PHARMCIES_FOR_ORDER)
	public Response<SearchRequestToPharmacyResponse> getPharmaciesForOrder(@RequestParam("userId") String userId,
			@RequestParam("uniqueRequestId") String uniqueRequestId, @RequestParam("replyType") String replyType,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam("latitude") Double latitude,
			@RequestParam("longitude") Double longitude) {
		Response<SearchRequestToPharmacyResponse> response = null;
		List<SearchRequestToPharmacyResponse> list = null;
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		list = pharmacyService.getPharmacyListbyOrderHistory(userId, uniqueRequestId, replyType, page, size, latitude,
				longitude);

		response = new Response<SearchRequestToPharmacyResponse>();
		response.setDataList(list);
		return response;

	}

	@GetMapping
	(PathProxy.LocaleUrls.GET_PHARMCIES_COUNT_FOR_ORDER)
	public Response<Integer> getPharmaciesCountForOrder(@RequestParam("uniqueRequestId") String uniqueRequestId,
			@RequestParam("replyType") String replyType) {
		Response<Integer> response = null;
		Integer count = 0;
		if (DPDoctorUtils.anyStringEmpty(uniqueRequestId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Unique Request id is null");
		}

		count = pharmacyService.getPharmacyListCountbyOrderHistory(uniqueRequestId, replyType);

		response = new Response<Integer>();
		response.setData(count);
		return response;

	}

	@PostMapping(value = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE, notes = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE)
	public Response<ImageURLResponse> addLocaleImageMultipart(@RequestParam("file") MultipartFile file) {
		ImageURLResponse imageURLResponse = null;
		Response<ImageURLResponse> response = null;
		if (file == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		imageURLResponse = localeService.addRXImageMultipart(file);
		response = new Response<ImageURLResponse>();
		response.setData(imageURLResponse);
		return response;
	}

	@GetMapping
	(value = PathProxy.LocaleUrls.ADD_EDIT_RECOMMENDATION)
	@ApiOperation(value = PathProxy.LocaleUrls.ADD_EDIT_RECOMMENDATION, notes = PathProxy.LocaleUrls.ADD_EDIT_RECOMMENDATION)
	public Response<Locale> addEditRecommedation(@RequestParam("localeId") String localeId,
			@RequestParam("patientId") String patientId, @RequestParam("type") RecommendationType type) {
		Locale locale = null;
		Response<Locale> response = null;
		if (DPDoctorUtils.anyStringEmpty(localeId, patientId) || type == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		locale = localeService.addEditRecommedation(localeId, patientId, type);
		transnationalService.checkPharmacy(new ObjectId(locale.getId()));
		response = new Response<Locale>();
		response.setData(locale);
		return response;
	}

	@PostMapping
	(PathProxy.LocaleUrls.ORDER_DRUG)
	@ApiOperation(value = PathProxy.LocaleUrls.ORDER_DRUG, notes = PathProxy.LocaleUrls.ORDER_DRUG)
	public Response<OrderDrugsRequest> orderDrug(OrderDrugsRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocaleId(), request.getUserId())
				|| DPDoctorUtils.anyStringEmpty(request.getUniqueRequestId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");
		} else if (request.getWayOfOrder() != null) {
			if (request.getWayOfOrder().name().equalsIgnoreCase(WayOfOrder.PICK_UP.name())) {
				if (request.getPickUpTime() == null || request.getPickUpDate() == null)
					throw new BusinessException(ServiceError.InvalidInput, "PickUp Time or Date cannot be null");
			} else {
				if (request.getPickUpAddress() == null)
					throw new BusinessException(ServiceError.InvalidInput, "PickUp Address cannot be null");
			}
		}

		OrderDrugsRequest status = pharmacyService.orderDrugs(request);
		Response<OrderDrugsRequest> response = new Response<OrderDrugsRequest>();
		response.setData(status);

		return response;
	}

	@GetMapping
	(value = PathProxy.LocaleUrls.GET_USER_FAKE_REQUEST_COUNT)
	@ApiOperation(value = PathProxy.LocaleUrls.GET_USER_FAKE_REQUEST_COUNT, notes = PathProxy.LocaleUrls.GET_USER_FAKE_REQUEST_COUNT)
	public Response<UserFakeRequestDetailResponse> getUserFakeRequestCount(@PathVariable("patientId") String patientId) {
		Response<UserFakeRequestDetailResponse> response = null;
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		UserFakeRequestDetailResponse detailResponse = pharmacyService.getUserFakeRequestCount(patientId);
		response = new Response<UserFakeRequestDetailResponse>();
		response.setData(detailResponse);
		return response;
	}

	@GetMapping
	(PathProxy.LocaleUrls.GET_PATIENT_ORDERS)
	@ApiOperation(value = PathProxy.LocaleUrls.GET_PATIENT_ORDERS, notes = PathProxy.LocaleUrls.GET_PATIENT_ORDERS)
	public Response<OrderDrugsResponse> getPatientOrders(@PathVariable("userId") String userId,
			@RequestParam("page") long page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {

		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		List<OrderDrugsResponse> list = pharmacyService.getPatientOrders(userId, page, size, updatedTime);

		Response<OrderDrugsResponse> response = new Response<OrderDrugsResponse>();
		response.setDataList(list);
		return response;

	}

	@GetMapping
	(PathProxy.LocaleUrls.GET_PATIENT_REQUEST)
	@ApiOperation(value = PathProxy.LocaleUrls.GET_PATIENT_REQUEST, notes = PathProxy.LocaleUrls.GET_PATIENT_REQUEST)
	public Response<SearchRequestFromUserResponse> getPatientRequests(@PathVariable("userId") String userId,
			@RequestParam("page") long page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		List<SearchRequestFromUserResponse> list = pharmacyService.getPatientRequests(userId, page, size, updatedTime);

		Response<SearchRequestFromUserResponse> response = new Response<SearchRequestFromUserResponse>();
		response.setDataList(list);
		return response;

	}

	@GetMapping
	(PathProxy.LocaleUrls.CANCEL_ORDER_DRUG)
	@ApiOperation(value = PathProxy.LocaleUrls.CANCEL_ORDER_DRUG, notes = PathProxy.LocaleUrls.CANCEL_ORDER_DRUG)
	public Response<OrderDrugsRequest> cancelOrderDrug(@PathVariable("orderId") String orderId,
			@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(orderId, userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "OderId or UserId cannot be null");
		}

		OrderDrugsRequest status = pharmacyService.cancelOrderDrug(orderId, userId);
		Response<OrderDrugsRequest> response = new Response<OrderDrugsRequest>();
		response.setData(status);

		return response;
	}

}
