package com.dpdocter.webservices;

import java.util.List;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Specimen;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.response.CollectionBoyLabAssociationLookupResponse;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;
import com.dpdocter.services.LabService;
import com.dpdocter.services.LocationServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.LAB_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.LAB_BASE_URL, description = "")
public class LabApi {

	private static Logger logger = Logger.getLogger(LabApi.class.getName());

	@Autowired
	private LabService labService;

	@Autowired
	private LocationServices locationServices;

	@Path(value = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT, notes = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT)
	public Response<List<Clinic>> getClinicWithReportCount(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<List<Clinic>> response = new Response<List<Clinic>>();
		response.setDataList(labService.getClinicWithReportCount(doctorId, locationId, hospitalId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR, notes = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	public Response<List<Records>> getReports(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "prescribedByDoctorId") String prescribedByDoctorId,
			@QueryParam(value = "prescribedByLocationId") String prescribedByLocationId,
			@QueryParam(value = "prescribedByHospitalId") String prescribedByHospitalId,
			@QueryParam(value = "size") int size, @QueryParam(value = "page") int page) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(hospitalId)
				|| DPDoctorUtils.anyStringEmpty(hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<List<Records>> response = new Response<List<Records>>();
		response.setDataList(labService.getReports(doctorId, locationId, hospitalId, prescribedByDoctorId,
				prescribedByLocationId, prescribedByHospitalId, size, page));

		return response;
	}

	@Path(value = PathProxy.LabUrls.ADD_EDIT_PICKUP_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.ADD_EDIT_PICKUP_REQUEST, notes = PathProxy.LabUrls.ADD_EDIT_PICKUP_REQUEST)
	public Response<LabTestPickup> addEditPickupRequest(AddEditLabTestPickupRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabTestPickup> response = new Response<LabTestPickup>();
		response.setData(locationServices.addEditLabTestPickupRequest(request));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_CB_LIST_BY_PARENT_LAB)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CB_LIST_BY_PARENT_LAB, notes = PathProxy.LabUrls.GET_CB_LIST_BY_PARENT_LAB)
	public Response<Object> getCBListByParentLab(@QueryParam("locationId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getCollectionBoyList(size, page, locationId, searchTerm));
		response.setData(locationServices.getCBCount(size, page, locationId, searchTerm));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARDS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARDS, notes = PathProxy.LabUrls.GET_RATE_CARDS)
	public Response<Object> getRateCards(@QueryParam("locationId") String locationId, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getRateCards(page, size, searchTerm, locationId));
		response.setData(locationServices.getRateCardCount(page, size, searchTerm, locationId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARD_TEST, notes = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	public Response<RateCardTestAssociationLookupResponse> getRateCardTests(@QueryParam("rateCardId") String rateCardId,
			@QueryParam("labId") String labId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm) {
		if (rateCardId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociationLookupResponse> response = new Response<RateCardTestAssociationLookupResponse>();
		response.setDataList(locationServices.getRateCardTests(page, size, searchTerm, rateCardId, labId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.ADD_EDIT_RATE_CARD)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.ADD_EDIT_RATE_CARD, notes = PathProxy.LabUrls.ADD_EDIT_RATE_CARD)
	public Response<RateCard> addEditRateCard(RateCard request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCard> response = new Response<RateCard>();
		response.setData(locationServices.addEditRateCard(request));

		return response;
	}

	@Path(value = PathProxy.LabUrls.ADD_EDIT_RATE_CARD_TESTS)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.ADD_EDIT_RATE_CARD_TESTS, notes = PathProxy.LabUrls.ADD_EDIT_RATE_CARD_TESTS)
	public Response<RateCardTestAssociation> addEditRateCardTest(RateCardTestAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociation> response = new Response<RateCardTestAssociation>();
		response.setData(locationServices.addEditRateCardTestAssociation(request));

		return response;
	}

	@Path(value = PathProxy.LabUrls.VERIFY_CRN)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.VERIFY_CRN, notes = PathProxy.LabUrls.VERIFY_CRN)
	public Response<Boolean> getRateCardTests(@QueryParam("locationId") String locationId,
			@QueryParam("requestId") String requestId, @QueryParam("crn") String crn) {
		if (crn == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(locationServices.verifyCRN(locationId, crn, requestId));

		return response;
	}
	
	@Path(value = PathProxy.LabUrls.DISCARD_COLLECTION_BOY)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.DISCARD_COLLECTION_BOY, notes = PathProxy.LabUrls.DISCARD_COLLECTION_BOY)
	public Response<CollectionBoy> discardCB(@QueryParam("collectionBoyId") String collectionBoyId,
			@QueryParam("discarded") Boolean discarded) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CollectionBoy> response = new Response<CollectionBoy>();
		response.setData(locationServices.discardCB(collectionBoyId, discarded));

		return response;
	}
	
	@Path(value = PathProxy.LabUrls.CHANGE_AVAILABILITY_OF_CB)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.CHANGE_AVAILABILITY_OF_CB, notes = PathProxy.LabUrls.CHANGE_AVAILABILITY_OF_CB)
	public Response<CollectionBoy> changeCBAvailabilty(@QueryParam("collectionBoyId") String collectionBoyId,
			@QueryParam("isAvailable") Boolean isAvailable) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CollectionBoy> response = new Response<CollectionBoy>();
		response.setData(locationServices.changeAvailability(collectionBoyId, isAvailable));

		return response;
	}
	
	@Path(value = PathProxy.LabUrls.GET_PICKUP_REQUEST_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUP_REQUEST_BY_ID, notes = PathProxy.LabUrls.GET_PICKUP_REQUEST_BY_ID)
	public Response<LabTestPickup> getPickupRequestById(@QueryParam("id") String id,
			@QueryParam("requestId") String requestId) {
		if (id == null && requestId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabTestPickup> response = new Response<LabTestPickup>();
		if (id != null) {
			response.setData(locationServices.getLabTestPickupById(id));
		} else if (requestId != null) {
			response.setData(locationServices.getLabTestPickupByRequestId(requestId));
		}
		return response;
	}
	
	@Path(value = PathProxy.LabUrls.ADD_CB_LAB_ASSOCIATION)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.ADD_CB_LAB_ASSOCIATION, notes = PathProxy.LabUrls.ADD_CB_LAB_ASSOCIATION)
	public Response<Location> addEditRateCard(List<CollectionBoyLabAssociation> collectionBoyLabAssociations) {
		if (collectionBoyLabAssociations == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(locationServices.addCollectionBoyAssociatedLabs(collectionBoyLabAssociations));

		return response;
	}
	
	@Path(value = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION, notes = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION)
	public Response<Location> getCBLabAssociation(@QueryParam("parentLabId") String parentLabId,
			@QueryParam("daughterLabId") String daughterLabId, @QueryParam("collectionBoyId") String collectionBoyId ,  @QueryParam("size") int size , @QueryParam("page") int page) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(locationServices.getCBAssociatedLabs(parentLabId, daughterLabId, collectionBoyId, size, page));
		return response;
	}
	
	
	@Path(value = PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_ASSOCIATED_LABS, notes = PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	public Response<Location> getAssociateLabs(@QueryParam("locationId") String locationId,
			@QueryParam("isParent") @DefaultValue("true") Boolean isParent, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(locationServices.getAssociatedLabs(locationId, isParent , searchTerm));
		return response;
	}
	
	@Path(value = PathProxy.LabUrls.GET_CLINICS_AND_LABS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CLINICS_AND_LABS, notes = PathProxy.LabUrls.GET_CLINICS_AND_LABS)
	public Response<Location> getClinics(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size,
			@QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "isClinic")  Boolean isClinic,
			@QueryParam(value = "isLab")  Boolean isLab, @QueryParam(value = "isParent") Boolean isParent,
			@QueryParam(value = "searchTerm") String searchTerm) {

		List<Location> locations = locationServices.getClinics(page, size, hospitalId, isClinic, isLab, isParent, searchTerm);

		Response<Location> response = new Response<Location>();
		response.setDataList(locations);
		return response;
	}
	
	@Path(PathProxy.LabUrls.GET_SPECIMEN_LIST)
	@GET
	public Response<Specimen> getSpecimen(@QueryParam("page") int page ,@QueryParam("size") int size , @QueryParam("searchTerm") String searchTerm) {
		List<Specimen> specimens = null;
		Response<Specimen> response = null;

		try {
			specimens = locationServices.getSpecimenList(page, size,searchTerm);
			response = new Response<Specimen>();
			response.setDataList(specimens);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}
	
	

}
