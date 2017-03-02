package com.dpdocter.webservices;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Locale;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.enums.RecommendationType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;
import com.dpdocter.services.LocaleService;
import com.dpdocter.services.PharmacyService;
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
	PharmacyService pharmacyService;

	@Autowired
	ServletContext context;

	/*
	 * @POST
	 * 
	 * @Path(value = PathProxy.LocaleUrls.UPLOAD)
	 * 
	 * @Consumes({ MediaType.MULTIPART_FORM_DATA })
	 * 
	 * @ApiOperation(value = PathProxy.LocaleUrls.UPLOAD, notes =
	 * PathProxy.LocaleUrls.UPLOAD) public Response<LocaleImage>
	 * addRecordsMultipart(@FormDataParam("file") FormDataBodyPart file,
	 * 
	 * @FormDataParam("data") FormDataBodyPart data) {
	 * data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	 * LocaleImageAddEditRequest request =
	 * data.getValueAs(LocaleImageAddEditRequest.class);
	 * 
	 * if (request == null) { throw new
	 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); }
	 * 
	 * return null; }
	 */

	@GET
	@Path(PathProxy.LocaleUrls.GET_LOCALE_DETAILS)
	public Response<Locale> getLocaleDetails(@QueryParam("id") String id,
			@QueryParam("contactNumber") String contactNumber) {
		Response<Locale> response = null;
		Locale locale = null;

		if (DPDoctorUtils.allStringsEmpty(id, contactNumber)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Please provide id or contact number. Both cannot be null");
		}
		if (id != null && !id.isEmpty()) {
			locale = localeService.getLocaleDetails(id);
			response = new Response<Locale>();
			response.setData(locale);
		} else {
			locale = localeService.getLocaleDetailsByContactDetails(contactNumber);
			response = new Response<Locale>();
			response.setData(locale);
		}
		return response;
	}

	/*
	 * @POST
	 * 
	 * @Path(PathProxy.LocaleUrls.EDIT_LOCALE_CONTACT_DETAILS ) public
	 * Response<Locale> updateLocaleContact( AddEditLocaleContactDetailsRequest
	 * request) { Response<Locale> response = null; Locale locale = null; try {
	 * if ( request ==null || request.getId() == null) { throw new
	 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } locale =
	 * localeService.updateLocaleContact(request); response = new
	 * Response<Locale>(); response.setData(locale);
	 * 
	 * } catch (Exception e) { LOGGER.warn(e); e.printStackTrace(); } return
	 * response;
	 * 
	 * }
	 * 
	 * @POST
	 * 
	 * @Path(PathProxy.LocaleUrls.EDIT_LOCALE_ADDRESS_DETAILS) public
	 * Response<Locale> updateLocaleAddress( AddEditLocaleAddressDetailsRequest
	 * request) { Response<Locale> response = null; Locale locale = null; try {
	 * if (request == null|| request.getId() == null) { throw new
	 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } locale =
	 * localeService.updateLocaleAddress( request); response = new
	 * Response<Locale>(); response.setData(locale);
	 * 
	 * } catch (Exception e) { // TODO: handle exception LOGGER.warn(e);
	 * e.printStackTrace(); } return response;
	 * 
	 * }
	 * 
	 * @POST
	 * 
	 * @Path(PathProxy.LocaleUrls.EDIT_LOCALE_VISIT_DETAILS) public
	 * Response<Locale>
	 * updateLocaleVisitDetails(AddEditLocaleVisitDetailsRequest request) {
	 * Response<Locale> response = null; Locale locale = null; try { if (request
	 * == null|| request.getId() == null) { throw new
	 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } locale =
	 * localeService.updateLocaleVisitDetails(request); response = new
	 * Response<Locale>(); response.setData(locale);
	 * 
	 * } catch (Exception e) { // TODO: handle exception LOGGER.warn(e);
	 * e.printStackTrace(); } return response;
	 * 
	 * }
	 * 
	 * @POST
	 * 
	 * @Path(PathProxy.LocaleUrls.EDIT_LOCALE_OTHER_DETAILS) public
	 * Response<Locale>
	 * updateLocaleOtherDetails(AddEditLocaleOtherDetailsRequest request) {
	 * Response<Locale> response = null; Locale locale = null; try { if (request
	 * == null|| request.getId() == null) { throw new
	 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } locale =
	 * localeService.updateLocaleOtherDetails(request); response = new
	 * Response<Locale>(); response.setData(locale);
	 * 
	 * } catch (Exception e) { // TODO: handle exception LOGGER.warn(e);
	 * e.printStackTrace(); } return response; }
	 * 
	 * @POST
	 * 
	 * @Path(PathProxy.LocaleUrls.EDIT_LOCALE_IMAGES) public Response<Locale>
	 * updateLocaleImages(@PathParam("id") String localeId,
	 * AddEditLocaleImagesRequest request) { Response<Locale> response = null;
	 * Locale locale = null; try { if (localeId == null || localeId.isEmpty()) {
	 * throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	 * } locale = localeService.updateLocaleImages(localeId, request); response
	 * = new Response<Locale>(); response.setData(locale);
	 * 
	 * } catch (Exception e) { // TODO: handle exception LOGGER.warn(e);
	 * e.printStackTrace(); } return response; }
	 */

	@POST
	@Path(PathProxy.LocaleUrls.ADD_USER_REQUEST)
	public Response<Boolean> addUserRequestInQueue(UserSearchRequest request) {
		Response<Boolean> response = null;
		Boolean status = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			status = pharmacyService.addSearchRequest(request);
			response = new Response<Boolean>();
			response.setData(status);

		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * @POST
	 * 
	 * @Path(PathProxy.LocaleUrls.ADD_PHARMACY_RESPONSE) public
	 * Response<Boolean> addPharmacyResponseInQueue(PharmacyResponse
	 * pharmacyResponse) { Response<Boolean> response = null; Boolean status =
	 * null; try { if (pharmacyResponse == null) { throw new
	 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); } status =
	 * pharmacyService.addResponseInQueue(pharmacyResponse); response = new
	 * Response<Boolean>(); response.setData(status);
	 * 
	 * } catch (Exception e) { // TODO: handle exception LOGGER.warn(e);
	 * e.printStackTrace(); } return response; }
	 */

	@GET
	@Path(PathProxy.LocaleUrls.GET_PATIENT_ORDER_HISTORY)
	public Response<SearchRequestFromUserResponse> getPatientOrderHistory(@PathParam("userId") String userId,
			@QueryParam("page") int page, @QueryParam("size") int size) {
		Response<SearchRequestFromUserResponse> response = null;
		List<SearchRequestFromUserResponse> list = null;
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		list = pharmacyService.getPatientOrderHistoryList(userId, page, size);

		if (list != null && !list.isEmpty()) {
			response = new Response<SearchRequestFromUserResponse>();
			response.setDataList(list);
		}
		return response;

	}
	
	@GET
	@Path(PathProxy.LocaleUrls.GET_PHARMCIES_FOR_ORDER)
	public Response<SearchRequestToPharmacyResponse> getPharmaciesForOrder(@QueryParam("userId") String userId, @QueryParam("uniqueRequestId") String uniqueRequestId,
			@QueryParam("replyType") String replyType,@QueryParam("page") int page, @QueryParam("size") int size) {
		Response<SearchRequestToPharmacyResponse> response = null;
		List<SearchRequestToPharmacyResponse> list = null;
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			throw new BusinessException(ServiceError.InvalidInput, "User id is null");
		}

		list = pharmacyService.getPharmacyListbyOrderHistory(userId, uniqueRequestId, replyType, page, size);

		if (list != null && !list.isEmpty()) {
			response = new Response<SearchRequestToPharmacyResponse>();
			response.setDataList(list);
		}
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

		if (count != null) {
			response = new Response<Integer>();
			response.setData(count);
		}
		return response;

	}
	
	@POST
	@Path(value = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE, notes = PathProxy.LocaleUrls.UPLOAD_RX_IMAGE)
	public Response<ImageURLResponse> addLocaleImageMultipart(@FormDataParam("file") FormDataBodyPart file) {
		/*data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		LocaleImageAddEditRequest request = data.getValueAs(LocaleImageAddEditRequest.class);*/
		ImageURLResponse imageURLResponse = null;
		Response<ImageURLResponse> response = null;
		if (file == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		imageURLResponse = localeService.addRXImageMultipart(file);
		if (imageURLResponse != null) {
			response = new Response<ImageURLResponse>();
			response.setData(imageURLResponse);
		}

		return response;
	}		

	public Response<Locale> addEditRecommedation(String localeId, String patientId,RecommendationType type){
		Locale locale = null;
		Response<Locale> response =  null;
		if (DPDoctorUtils.anyStringEmpty(localeId,patientId) || type == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		locale = localeService.addEditRecommedation(localeId, patientId, type);
		if(locale != null)
		{
			response = new Response<>();
			response.setData(locale);
		}
		return null;
	}
	
	
}
