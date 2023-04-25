package com.dpdocter.webservices;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.LOCALE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LOCALE_BASE_URL, description = "Endpoint for Locale API's")
public class LocaleApi {

	private static final Logger LOGGER = Logger.getLogger(LocaleApi.class.getName());

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

	@GET
	@Path(PathProxy.LocaleUrls.GET_LOCALE_DETAILS)
	public Response<Locale> getLocaleDetails(@QueryParam("id") String id,
			@QueryParam("contactNumber") String contactNumber, @QueryParam("patientId") String patientId) {
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

	@GET
	@Path(PathProxy.LocaleUrls.GET_LOCALE_BY_SLUGURL)
	public Response<Locale> getLocaleDetails(@PathParam("slugUrl") String slugUrl) {
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

	@POST
	@Path(PathProxy.LocaleUrls.ADD_USER_REQUEST)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response<UserSearchRequest> addUserRequestInQueue(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {
		Response<UserSearchRequest> response = null;
		UserSearchRequest status = null;
		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		UserSearchRequest request = data.getValueAs(UserSearchRequest.class);
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

	@GET
	@Path(PathProxy.LocaleUrls.GET_PATIENT_ORDER_HISTORY)
	public Response<SearchRequestFromUserResponse> getPatientOrderHistory(@PathParam("userId") String userId,
			@QueryParam("page") long page, @QueryParam("size") int size) {
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

	@GET
	@Path(PathProxy.LocaleUrls.GET_PHARMCIES_FOR_ORDER)
	public Response<SearchRequestToPharmacyResponse> getPharmaciesForOrder(@QueryParam("userId") String userId,
			@QueryParam("uniqueRequestId") String uniqueRequestId, @QueryParam("replyType") String replyType,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("latitude") Double latitude,
			@QueryParam("longitude") Double longitude) {
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

	@GET
	@Path(PathProxy.LocaleUrls.GET_PHARMCIES_COUNT_FOR_ORDER)
	public Response<Integer> getPharmaciesCountForOrder(@QueryParam("uniqueRequestId") String uniqueRequestId,
			@QueryParam("replyType") String replyType) {
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

	@POST
	@Path(value = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE, notes = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE)
	public Response<ImageURLResponse> addLocaleImageMultipart(@FormDataParam("file") FormDataBodyPart file) {
		/*
		 * data.setMediaType(MediaType.APPLICATION_JSON_TYPE); LocaleImageAddEditRequest
		 * request = data.getValueAs(LocaleImageAddEditRequest.class);
		 */
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

	@GET
	@Path(value = PathProxy.LocaleUrls.ADD_EDIT_RECOMMENDATION)
	@ApiOperation(value = PathProxy.LocaleUrls.ADD_EDIT_RECOMMENDATION, notes = PathProxy.LocaleUrls.ADD_EDIT_RECOMMENDATION)
	public Response<Locale> addEditRecommedation(@QueryParam("localeId") String localeId,
			@QueryParam("patientId") String patientId, @QueryParam("type") RecommendationType type) {
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

	@POST
	@Path(PathProxy.LocaleUrls.ORDER_DRUG)
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

	@GET
	@Path(value = PathProxy.LocaleUrls.GET_USER_FAKE_REQUEST_COUNT)
	@ApiOperation(value = PathProxy.LocaleUrls.GET_USER_FAKE_REQUEST_COUNT, notes = PathProxy.LocaleUrls.GET_USER_FAKE_REQUEST_COUNT)
	public Response<UserFakeRequestDetailResponse> getUserFakeRequestCount(@PathParam("patientId") String patientId) {
		Response<UserFakeRequestDetailResponse> response = null;
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		UserFakeRequestDetailResponse detailResponse = pharmacyService.getUserFakeRequestCount(patientId);
		response = new Response<UserFakeRequestDetailResponse>();
		response.setData(detailResponse);
		return response;
	}

	@GET
	@Path(PathProxy.LocaleUrls.GET_PATIENT_ORDERS)
	@ApiOperation(value = PathProxy.LocaleUrls.GET_PATIENT_ORDERS, notes = PathProxy.LocaleUrls.GET_PATIENT_ORDERS)
	public Response<OrderDrugsResponse> getPatientOrders(@PathParam("userId") String userId,
			@QueryParam("page") long page, @QueryParam("size") int size,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {

		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		List<OrderDrugsResponse> list = pharmacyService.getPatientOrders(userId, page, size, updatedTime);

		Response<OrderDrugsResponse> response = new Response<OrderDrugsResponse>();
		response.setDataList(list);
		return response;

	}

	@GET
	@Path(PathProxy.LocaleUrls.GET_PATIENT_REQUEST)
	@ApiOperation(value = PathProxy.LocaleUrls.GET_PATIENT_REQUEST, notes = PathProxy.LocaleUrls.GET_PATIENT_REQUEST)
	public Response<SearchRequestFromUserResponse> getPatientRequests(@PathParam("userId") String userId,
			@QueryParam("page") long page, @QueryParam("size") int size,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		List<SearchRequestFromUserResponse> list = pharmacyService.getPatientRequests(userId, page, size, updatedTime);

		Response<SearchRequestFromUserResponse> response = new Response<SearchRequestFromUserResponse>();
		response.setDataList(list);
		return response;

	}

	@GET
	@Path(PathProxy.LocaleUrls.CANCEL_ORDER_DRUG)
	@ApiOperation(value = PathProxy.LocaleUrls.CANCEL_ORDER_DRUG, notes = PathProxy.LocaleUrls.CANCEL_ORDER_DRUG)
	public Response<OrderDrugsRequest> cancelOrderDrug(@PathParam("orderId") String orderId,
			@PathParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(orderId, userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "OderId or UserId cannot be null");
		}

		OrderDrugsRequest status = pharmacyService.cancelOrderDrug(orderId, userId);
		Response<OrderDrugsRequest> response = new Response<OrderDrugsRequest>();
		response.setData(status);

		return response;
	}

}
