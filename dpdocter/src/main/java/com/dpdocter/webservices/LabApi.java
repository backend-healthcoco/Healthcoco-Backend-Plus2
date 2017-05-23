package com.dpdocter.webservices;

import java.util.List;

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

import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.beans.Records;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditLabTestPickupRequest;
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
	public Response<CollectionBoy> getCBListByParentLab(@QueryParam("locationId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CollectionBoy> response = new Response<CollectionBoy>();
		response.setDataList(locationServices.getCollectionBoyList(size, page, locationId, searchTerm));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARDS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARDS, notes = PathProxy.LabUrls.GET_RATE_CARDS)
	public Response<RateCard> getRateCards(@QueryParam("locationId") String locationId, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCard> response = new Response<RateCard>();
		response.setDataList(locationServices.getRateCards(page, size, searchTerm, locationId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARD_TEST, notes = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	public Response<RateCardTestAssociation> getRateCardTests(@QueryParam("rateCardId") String rateCardId,
			@QueryParam("labId") String labId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm) {
		if (rateCardId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociation> response = new Response<RateCardTestAssociation>();
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

}
