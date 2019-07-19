package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorksAmount;
import com.dpdocter.beans.DentalWorksInvoice;
import com.dpdocter.beans.DentalWorksReceipt;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.elasticsearch.services.impl.ESDentalLabServiceImpl;
import com.dpdocter.enums.LabType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.AddEditTaxRequest;
import com.dpdocter.request.DentalLabDoctorRegistrationRequest;
import com.dpdocter.request.DentalLabPickupChangeStatusRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.request.UpdateDentalStagingRequest;
import com.dpdocter.request.UpdateETARequest;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;
import com.dpdocter.response.DentalWorksInvoiceResponse;
import com.dpdocter.response.DentalWorksReceiptResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.TaxResponse;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.LocationServices;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DENTAL_LAB_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_LAB_BASE_URL, description = "Endpoint for dental lab")
public class DentalLabAPI {

	private static Logger logger = Logger.getLogger(DentalLabAPI.class.getName());

	@Autowired
	private DentalLabService dentalLabService;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private ESDentalLabServiceImpl esDentalLabServiceImpl;

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	public Response<DentalWork> addEditDEntalWorks(AddEditCustomWorkRequest request) {
		DentalWork dentalWork = null;
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalWork = new DentalWork();
		dentalWork = dentalLabService.addEditCustomWork(request);
		Response<DentalWork> response = new Response<DentalWork>();
		if (dentalWork != null) {
			response.setData(dentalWork);
			ESDentalWorksDocument dentalWorksDocument = new ESDentalWorksDocument();
			BeanUtil.map(dentalWork, dentalWorksDocument);
			esDentalLabServiceImpl.addDentalWorks(dentalWorksDocument);
		}
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	public Response<DentalWork> getDentalWorks(@QueryParam("locationId") String locationId,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		Response<DentalWork> response = new Response<DentalWork>();
		response.setDataList(dentalLabService.getCustomWorks(page, size, searchTerm));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	@DELETE
	@ApiOperation(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	public Response<DentalWork> deleteDentalWork(@QueryParam("id") String id,
			@QueryParam("discarded") boolean discarded) {

		DentalWork dentalWork = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		// dentalWork = new DentalWork();
		dentalWork = dentalLabService.deleteCustomWork(id, discarded);
		Response<DentalWork> response = new Response<DentalWork>();
		if (dentalWork != null) {
			response.setData(dentalWork);
			ESDentalWorksDocument dentalWorksDocument = new ESDentalWorksDocument();
			BeanUtil.map(dentalWork, dentalWorksDocument);
			esDentalLabServiceImpl.addDentalWorks(dentalWorksDocument);
		}
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE, notes = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	public Response<Boolean> changeLabType(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("labType") LabType labType) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.changeLabType(doctorId, locationId, labType));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociation> addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabDoctorAssociation> response = new Response<DentalLabDoctorAssociation>();
		response.setData(dentalLabService.addEditDentalLabDoctorAssociation(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociationLookupResponse> getDentalLabDoctorAssociationForLocation(
			@QueryParam("locationId") String locationId, @QueryParam("doctorId") String doctorId,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		Response<DentalLabDoctorAssociationLookupResponse> response = new Response<DentalLabDoctorAssociationLookupResponse>();
		response.setDataList(
				dentalLabService.getDentalLabDoctorAssociations(locationId, doctorId, page, size, searchTerm));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION_FOR_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION_FOR_DOCTOR, notes = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION_FOR_DOCTOR)
	public Response<Location> getDentalLabDoctorAssociationForDoctor(@QueryParam("doctorId") String doctorId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		Response<Location> response = new Response<Location>();
		response.setDataList(
				dentalLabService.getDentalLabDoctorAssociationsForDoctor(doctorId, page, size, searchTerm));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORK_PICKUP)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORK_PICKUP, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORK_PICKUP)
	public Response<DentalLabPickup> addEditPickupRequest(DentalLabPickupRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabPickup> response = new Response<DentalLabPickup>();
		response.setData(dentalLabService.addEditDentalLabPickupRequest(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_WORK_PICKUPS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_WORK_PICKUPS, notes = PathProxy.DentalLabUrls.GET_DENTAL_WORK_PICKUPS)
	public Response<DentalLabPickupResponse> getPickupRequests(@QueryParam("dentalLabId") String dentalLabId,
			@QueryParam("doctorId") String doctorId, @DefaultValue("0") @QueryParam("from") Long from,
			@QueryParam("to") Long to, @QueryParam("searchTerm") String searchTerm, @QueryParam("status") String status,
			@QueryParam("isAcceptedAtLab") Boolean isAcceptedAtLab, @QueryParam("isCompleted") Boolean isCompleted,
			@QueryParam("isCollectedAtDoctor") Boolean isCollectedAtDoctor, @QueryParam("size") int size,
			@QueryParam("page") long page, @QueryParam("fromETA") Long fromETA, @QueryParam("toETA") Long toETA,
			@QueryParam("isTrailsRequired") Boolean isTrailsRequired) {

		Response<DentalLabPickupResponse> response = new Response<DentalLabPickupResponse>();
		response.setDataList(dentalLabService.getRequests(dentalLabId, doctorId, from, to, searchTerm, status,
				isAcceptedAtLab, isCompleted, isCollectedAtDoctor, size, page, fromETA, toETA, isTrailsRequired));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION, notes = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION)
	public Response<Boolean> addEditRateCardWorkAssociation(List<RateCardDentalWorkAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.addEditRateCardDentalWorkAssociation(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS, notes = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS)
	public Response<RateCardDentalWorkAssociation> getRateCardWorks(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("dentalLabId") String dentalLabId, @QueryParam("doctorId") String doctorId,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (doctorId == null || dentalLabId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(
				dentalLabService.getRateCardWorks(page, size, searchTerm, dentalLabId, doctorId, discarded));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION, notes = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION)
	public Response<RateCardDoctorAssociation> addEditRateCardDoctorAssociation(RateCardDoctorAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDoctorAssociation> response = new Response<RateCardDoctorAssociation>();
		response.setData(dentalLabService.addEditRateCardDoctorAssociation(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_RATE_CARD_DOCTOR_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RATE_CARD_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_RATE_CARD_DOCTOR_ASSOCIATION)
	public Response<RateCardDentalWorkAssociation> getRateCards(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("doctorId") String doctorId, @QueryParam("dentalLabId") String dentalLabId,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(dentalLabService.getRateCards(page, size, searchTerm, doctorId, dentalLabId, discarded));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION, notes = PathProxy.DentalLabUrls.ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION)
	public Response<Boolean> addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.addEditCollectionBoyDoctorAssociation(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_COLLECTION_BOY_DOCTOR_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_COLLECTION_BOY_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_COLLECTION_BOY_DOCTOR_ASSOCIATION)
	public Response<RateCardDentalWorkAssociation> getCBDoctorAssociation(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("dentalLabId") String dentalLabId, @QueryParam("collectionBoyId") String collectionBoyId) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(
				dentalLabService.getCBAssociatedDoctors(doctorId, dentalLabId, collectionBoyId, size, page));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_CB_LIST_FOR_DENTAL_LAB)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_CB_LIST_FOR_DENTAL_LAB, notes = PathProxy.DentalLabUrls.GET_CB_LIST_FOR_DENTAL_LAB)
	public Response<Object> getCBListByParentLab(@QueryParam("dentalLabId") String locationId,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(
				locationServices.getCollectionBoyList(size, page, locationId, searchTerm, LabType.DENTAL.getType()));
		response.setData(locationServices.getCBCount(locationId, searchTerm, LabType.DENTAL.getType()));

		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.CHANGE_REQUEST_STATUS)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.CHANGE_REQUEST_STATUS, notes = PathProxy.DentalLabUrls.CHANGE_REQUEST_STATUS)
	public Response<Boolean> changeStatus(DentalLabPickupChangeStatusRequest request) {
		if (DPDoctorUtils.anyStringEmpty(request.getDentalLabPickupId(), request.getStatus())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.changeStatus(request));
		return response;
	}

	@POST
	@Path(value = PathProxy.DentalLabUrls.ADD_DENTAL_IMAGE_MULTIPART)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_DENTAL_IMAGE_MULTIPART, notes = PathProxy.DentalLabUrls.ADD_DENTAL_IMAGE_MULTIPART)
	public Response<ImageURLResponse> addDentalImageMultipart(@FormDataParam("file") FormDataBodyPart file) {

		if (file == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ImageURLResponse imageURLResponse = dentalLabService.addDentalImage(file);

		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(imageURLResponse);
		return response;
	}

	@POST
	@Path(value = PathProxy.DentalLabUrls.ADD_DENTAL_IMAGE_BASE_64)
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_DENTAL_IMAGE_BASE_64, notes = PathProxy.DentalLabUrls.ADD_DENTAL_IMAGE_BASE_64)
	public Response<ImageURLResponse> addDentalImageBase64(FileDetails fileDetails) {

		if (fileDetails == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ImageURLResponse imageURLResponse = dentalLabService.addDentalImageBase64(fileDetails);

		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(imageURLResponse);
		return response;
	}

	@POST
	@Path(value = PathProxy.DentalLabUrls.UPDATE_DENTAL_STAGES_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.DentalLabUrls.UPDATE_DENTAL_STAGES_FOR_DOCTOR, notes = PathProxy.DentalLabUrls.UPDATE_DENTAL_STAGES_FOR_DOCTOR)
	public Response<Boolean> updateDentalStagesForDoctor(UpdateDentalStagingRequest request) {

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.updateDentalStageForDoctor(request));
		return response;
	}

	@POST
	@Path(value = PathProxy.DentalLabUrls.UPDATE_DENTAL_STAGES_FOR_LAB)
	@ApiOperation(value = PathProxy.DentalLabUrls.UPDATE_DENTAL_STAGES_FOR_LAB, notes = PathProxy.DentalLabUrls.UPDATE_DENTAL_STAGES_FOR_LAB)
	public Response<Boolean> updateDentalStagesForLab(UpdateDentalStagingRequest request) {

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.updateDentalStageForLab(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS_BY_RATE_CARD)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS_BY_RATE_CARD, notes = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS)
	public Response<RateCardDentalWorkAssociation> getRateCardWorks(@QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("rateCardId") String rateCardId,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (rateCardId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(dentalLabService.getRateCardWorks(page, size, searchTerm, rateCardId, discarded));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.CANCEL_REQUEST)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.CANCEL_REQUEST, notes = PathProxy.DentalLabUrls.CANCEL_REQUEST)
	public Response<Boolean> cancelRequest(@QueryParam("requestId") String requestId,
			@QueryParam("reasonForCancel") String reasonForCancel, @QueryParam("cancelledBy") String cancelledBy) {
		if (requestId == null) {
			// logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.cancelRequest(requestId, reasonForCancel, cancelledBy));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DISCARD_REQUEST)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.DISCARD_REQUEST, notes = PathProxy.DentalLabUrls.DISCARD_REQUEST)
	public Response<Boolean> discardRequest(@PathParam("requestId") String requestId,
			@QueryParam("discarded") Boolean discarded) {
		if (requestId == null) {
			// logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.discardRequest(requestId, discarded));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.UPDATE_ETA)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.UPDATE_ETA, notes = PathProxy.DentalLabUrls.UPDATE_ETA)
	public Response<Boolean> updateETA(UpdateETARequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.updateETA(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_PICKUP_REQUEST_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_PICKUP_REQUEST_BY_ID, notes = PathProxy.DentalLabUrls.GET_PICKUP_REQUEST_BY_ID)
	public Response<DentalLabPickupResponse> getRequestById(@PathParam("requestId") String requestId) {
		if (requestId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabPickupResponse> response = new Response<DentalLabPickupResponse>();
		response.setData(dentalLabService.getRequestById(requestId));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_LAB_REPORT)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_LAB_REPORT, notes = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_LAB_REPORT)
	public Response<String> downloadReport(@PathParam("requestId") String requestId) {
		if (requestId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(dentalLabService.downloadDentalLabReportPrint(requestId, false));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_LAB_INSPECTION_REPORT)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_LAB_INSPECTION_REPORT, notes = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_LAB_INSPECTION_REPORT)
	public Response<String> downloadInspectionReport(@PathParam("requestId") String requestId) {
		if (requestId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(dentalLabService.downloadDentalLabReportPrint(requestId, true));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DOWNLOAD_MULTIPLE_DENTAL_LAB_INSPECTION_REPORT)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.DOWNLOAD_MULTIPLE_DENTAL_LAB_INSPECTION_REPORT, notes = PathProxy.DentalLabUrls.DOWNLOAD_MULTIPLE_DENTAL_LAB_INSPECTION_REPORT)
	public Response<String> downloadMultipleInspectionReport(@MatrixParam("requestId") List<String> requestId) {
		if (requestId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(dentalLabService.downloadMultipleInspectionReportPrint(requestId));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DOCTOR_REGISTRATION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.DOCTOR_REGISTRATION, notes = PathProxy.DentalLabUrls.DOCTOR_REGISTRATION)
	public Response<Boolean> doctorRegistration(DentalLabDoctorRegistrationRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.dentalLabDoctorRegistration(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_TAX)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_TAX, notes = PathProxy.DentalLabUrls.ADD_EDIT_TAX)
	public Response<TaxResponse> addEditTax(AddEditTaxRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<TaxResponse> response = new Response<TaxResponse>();
		response.setData(dentalLabService.addEditTax(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_INVOICE)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_INVOICE, notes = PathProxy.DentalLabUrls.ADD_EDIT_INVOICE)
	public Response<DentalWorksInvoice> addEditInvoice(DentalWorksInvoice request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksInvoice> response = new Response<DentalWorksInvoice>();
		response.setData(dentalLabService.addEditInvoice(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_RECEIPT)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_RECEIPT, notes = PathProxy.DentalLabUrls.ADD_EDIT_RECEIPT)
	public Response<DentalWorksReceipt> addEditReceipt(DentalWorksReceipt request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksReceipt> response = new Response<DentalWorksReceipt>();
		response.setData(dentalLabService.addEditReceipt(request));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_INVOICES)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_INVOICES, notes = PathProxy.DentalLabUrls.GET_INVOICES)

	public Response<DentalWorksInvoice> getInvoices(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("dentalLabLocationId") String dentalLabLocationId,
			@QueryParam("dentalLabHospitalId") String dentalLabHospitalId,
			@DefaultValue("0") @QueryParam("from") Long from, @QueryParam("to") Long to,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size, @QueryParam("page") long page) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId, dentalLabHospitalId, dentalLabLocationId)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksInvoice> response = new Response<DentalWorksInvoice>();
		response.setDataList(dentalLabService.getInvoices(doctorId, locationId, hospitalId, dentalLabLocationId,
				dentalLabHospitalId, from, to, searchTerm, size, page));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_RECEIPTS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RECEIPTS, notes = PathProxy.DentalLabUrls.GET_RECEIPTS)
	public Response<DentalWorksReceipt> getReceipts(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("dentalLabLocationId") String dentalLabLocationId,
			@QueryParam("dentalLabHospitalId") String dentalLabHospitalId,@DefaultValue("0") @QueryParam("from") Long from,
			@QueryParam("to") Long to, @QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId, dentalLabHospitalId, dentalLabLocationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksReceipt> response = new Response<DentalWorksReceipt>();
		response.setDataList(dentalLabService.getReceipts(doctorId, locationId, hospitalId, dentalLabLocationId,
				dentalLabHospitalId, from, to, searchTerm, size, page));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_INVOICE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_INVOICE_BY_ID, notes = PathProxy.DentalLabUrls.GET_INVOICE_BY_ID)
	public Response<DentalWorksInvoiceResponse> getInvoiceById(@QueryParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksInvoiceResponse> response = new Response<DentalWorksInvoiceResponse>();
		response.setData(dentalLabService.getInvoiceById(id));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_RECEIPT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RECEIPT_BY_ID, notes = PathProxy.DentalLabUrls.GET_RECEIPT_BY_ID)
	public Response<DentalWorksReceiptResponse> getReceiptById(@QueryParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksReceiptResponse> response = new Response<DentalWorksReceiptResponse>();
		response.setData(dentalLabService.getReceiptById(id));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DISCARD_INVOICE)
	@DELETE
	@ApiOperation(value = PathProxy.DentalLabUrls.DISCARD_INVOICE, notes = PathProxy.DentalLabUrls.DISCARD_INVOICE)
	public Response<DentalWorksInvoice> discardInvoice(@QueryParam("id") String id,
			@QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksInvoice> response = new Response<DentalWorksInvoice>();
		response.setData(dentalLabService.discardInvoice(id, discarded));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DISCARD_RECEIPT)
	@DELETE
	@ApiOperation(value = PathProxy.DentalLabUrls.DISCARD_RECEIPT, notes = PathProxy.DentalLabUrls.DISCARD_RECEIPT)
	public Response<DentalWorksReceipt> discardReceipt(@QueryParam("id") String id,
			@QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksReceipt> response = new Response<DentalWorksReceipt>();
		response.setData(dentalLabService.discardReceipt(id, discarded));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_AMOUNT)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_AMOUNT, notes = PathProxy.DentalLabUrls.GET_AMOUNT)
	public Response<DentalWorksAmount> getAmount(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("dentalLabLocationId") String dentalLabLocationId,
			@QueryParam("dentalLabHospitalId") String dentalLabHospitalId) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId, dentalLabLocationId, dentalLabHospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWorksAmount> response = new Response<DentalWorksAmount>();
		response.setData(
				dentalLabService.getAmount(doctorId, locationId, hospitalId, dentalLabLocationId, dentalLabHospitalId));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_WORK_INVOICE)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_WORK_INVOICE, notes = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_WORK_INVOICE)
	public Response<String> downloadDentalInvoice(@PathParam("invoiceId") String invoiceId) {
		if (DPDoctorUtils.allStringsEmpty(invoiceId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(dentalLabService.downloadDentalWorkInvoice(invoiceId));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_WORK_RECEIPT)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_WORK_RECEIPT, notes = PathProxy.DentalLabUrls.DOWNLOAD_DENTAL_WORK_RECEIPT)
	public Response<String> downloadDentalLabReceipt(@PathParam("receiptId") String receiptId) {
		if (DPDoctorUtils.allStringsEmpty(receiptId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(dentalLabService.downloadDentalLabReceipt(receiptId));
		return response;
	}

}
