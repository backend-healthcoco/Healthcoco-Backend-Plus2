package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.LabTestPickupLookupResponse;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardLabAssociation;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Specimen;
import com.dpdocter.enums.LabType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.request.DoctorRecordUploadRequest;
import com.dpdocter.request.DynamicCollectionBoyAllocationRequest;
import com.dpdocter.request.EditLabReportsRequest;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.request.RecordUploadRequest;
import com.dpdocter.response.DoctorLabReportResponseWithCount;
import com.dpdocter.response.DynamicCollectionBoyAllocationResponse;
import com.dpdocter.response.LabReportsResponse;
import com.dpdocter.response.LabTestGroupResponse;
import com.dpdocter.response.PatientLabTestSampleReportResponse;
import com.dpdocter.response.RateCardTestAssociationByLBResponse;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;
import com.dpdocter.services.LabReportsService;
import com.dpdocter.services.LabService;
import com.dpdocter.services.LocationServices;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

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

	private static Logger logger = LogManager.getLogger(LabApi.class.getName());

	@Autowired
	private LabService labService;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private LabReportsService labReportsService;

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
	public Response<Records> getReports(@PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "prescribedByDoctorId") String prescribedByDoctorId,
			@QueryParam(value = "prescribedByLocationId") String prescribedByLocationId,
			@QueryParam(value = "prescribedByHospitalId") String prescribedByHospitalId,
			@QueryParam(value = "size") int size, @QueryParam(value = "page") long page) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(hospitalId)
				|| DPDoctorUtils.anyStringEmpty(hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Records> response = new Response<Records>();
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
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getCollectionBoyList(size, page, locationId, searchTerm ,LabType.DIAGNOSTIC.getType()));
		response.setData(locationServices.getCBCount(locationId, searchTerm , LabType.DIAGNOSTIC.getType()));
		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARDS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARDS, notes = PathProxy.LabUrls.GET_RATE_CARDS)
	public Response<Object> getRateCards(@QueryParam("locationId") String locationId, @QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getRateCards(page, size, searchTerm, locationId));
		response.setData(locationServices.getRateCardCount(searchTerm, locationId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARD_TEST, notes = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	public Response<RateCardTestAssociationLookupResponse> getRateCardTests(@QueryParam("rateCardId") String rateCardId,
			@QueryParam("labId") String labId, @QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm,
			@QueryParam("discarded") @DefaultValue("false") Boolean discarded) {
		if (rateCardId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociationLookupResponse> response = new Response<RateCardTestAssociationLookupResponse>();
		response.setDataList(locationServices.getRateCardTests(page, size, searchTerm, rateCardId, labId, discarded));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_GROUPED_LAB_TEST)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_GROUPED_LAB_TEST, notes = PathProxy.LabUrls.GET_GROUPED_LAB_TEST)
	public Response<LabTestGroupResponse> getGroupedLabTest(@QueryParam("daughterLabId") String daughterLabId,
			@QueryParam("parentLabId") String parentLabId, @QueryParam("labId") String labId,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (daughterLabId == null || parentLabId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabTestGroupResponse> response = new Response<LabTestGroupResponse>();
		response.setDataList(
				locationServices.getGroupedLabTests(page, size, searchTerm, daughterLabId, parentLabId, labId));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_RATE_CARD_TEST_BY_DL)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARD_TEST_BY_DL, notes = PathProxy.LabUrls.GET_RATE_CARD_TEST_BY_DL)
	public Response<RateCardTestAssociationByLBResponse> getRateCardTests(
			@QueryParam("daughterLabId") String daughterLabId, @QueryParam("parentLabId") String parentLabId,
			@QueryParam("labId") String labId, @QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("specimen") String specimen) {
		if (daughterLabId == null || parentLabId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociationByLBResponse> response = new Response<RateCardTestAssociationByLBResponse>();
		response.setDataList(
				locationServices.getRateCardTests(page, size, searchTerm, daughterLabId, parentLabId, labId, specimen));

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
	public Response<Boolean> addEditRateCardTest(List<RateCardTestAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(locationServices.addEditRateCardTestAssociation(request));

		return response;
	}

	@Path(value = PathProxy.LabUrls.VERIFY_CRN)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.VERIFY_CRN, notes = PathProxy.LabUrls.VERIFY_CRN)
	public Response<Boolean> verifyCRN(@QueryParam("locationId") String locationId,
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
	public Response<LabTestPickupLookupResponse> getPickupRequestById(@QueryParam("id") String id,
			@QueryParam("requestId") String requestId) {
		if (id == null && requestId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabTestPickupLookupResponse> response = new Response<LabTestPickupLookupResponse>();
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
	public Response<Location> addEditCBLabAssociation(List<CollectionBoyLabAssociation> collectionBoyLabAssociations) {
		Response<Location> response = null;
		try {
			if (collectionBoyLabAssociations == null) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			response = new Response<Location>();
			response.setDataList(locationServices.addCollectionBoyAssociatedLabs(collectionBoyLabAssociations));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION, notes = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION)
	public Response<Location> getCBLabAssociation(@QueryParam("parentLabId") String parentLabId,
			@QueryParam("daughterLabId") String daughterLabId, @QueryParam("collectionBoyId") String collectionBoyId,
			@QueryParam("size") int size, @QueryParam("page") long page) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(
				locationServices.getCBAssociatedLabs(parentLabId, daughterLabId, collectionBoyId, size, page));
		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_ASSOCIATED_LABS, notes = PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	public Response<Location> getAssociateLabs(@QueryParam("locationId") String locationId,
			@QueryParam("isParent") @DefaultValue("true") Boolean isParent, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") long page, @QueryParam("size") int size) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(locationServices.getAssociatedLabs(locationId, isParent, searchTerm, page, size));
		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_CLINICS_AND_LABS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_CLINICS_AND_LABS, notes = PathProxy.LabUrls.GET_CLINICS_AND_LABS)
	public Response<Location> getClinics(@QueryParam(value = "page") long page, @QueryParam(value = "size") int size,
			@QueryParam(value = "hospitalId") String hospitalId, @QueryParam(value = "isClinic") Boolean isClinic,
			@QueryParam(value = "isLab") Boolean isLab, @QueryParam(value = "isDentalWorksLab") Boolean isDentalWorksLab ,  @QueryParam(value = "isDentalImagingLab") Boolean isDentalImagingLab ,  @QueryParam(value = "isParent") Boolean isParent,
			@QueryParam(value = "searchTerm") String searchTerm) {

		List<Location> locations = locationServices.getClinics(page, size, hospitalId, isClinic, isLab, isParent, isDentalWorksLab , isDentalImagingLab,
				searchTerm);

		Response<Location> response = new Response<Location>();
		response.setDataList(locations);
		return response;
	}

	@Path(PathProxy.LabUrls.GET_SPECIMEN_LIST)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_SPECIMEN_LIST, notes = PathProxy.LabUrls.GET_SPECIMEN_LIST)
	public Response<Specimen> getSpecimen(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm) {
		List<Specimen> specimens = null;
		Response<Specimen> response = null;

		try {
			specimens = locationServices.getSpecimenList(page, size, searchTerm);
			response = new Response<Specimen>();
			response.setDataList(specimens);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(PathProxy.LabUrls.EDIT_COLLECTION_BOY)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.EDIT_COLLECTION_BOY, notes = PathProxy.LabUrls.EDIT_COLLECTION_BOY)
	public Response<CollectionBoy> editCollectionBoy(CollectionBoy request) {

		Response<CollectionBoy> response = null;
		CollectionBoy collectionBoy = null;
		try {
			if (request.getId() == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Id is null");
			}
			collectionBoy = locationServices.editCollectionBoy(request);
			response = new Response<CollectionBoy>();
			response.setData(collectionBoy);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(PathProxy.LabUrls.ADD_EDIT_LAB_RATE_CARD_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.ADD_EDIT_LAB_RATE_CARD_ASSOCIAITION, notes = PathProxy.LabUrls.ADD_EDIT_LAB_RATE_CARD_ASSOCIAITION)
	public Response<RateCardLabAssociation> editRateCardLabAssociation(RateCardLabAssociation request) {

		Response<RateCardLabAssociation> response = null;
		RateCardLabAssociation rateCardLabAssociation = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			rateCardLabAssociation = locationServices.addEditRateCardAssociatedLab(request);
			response = new Response<RateCardLabAssociation>();
			response.setData(rateCardLabAssociation);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(PathProxy.LabUrls.GET_DL_RATE_CARD)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_DL_RATE_CARD, notes = PathProxy.LabUrls.GET_DL_RATE_CARD)
	public Response<RateCard> getDLRateCard(@QueryParam("parentLabId") String parentLabId,
			@QueryParam("daughterLabId") String daughterLabId) {
		RateCard rateCard = null;
		Response<RateCard> response = null;

		try {
			rateCard = locationServices.getDLRateCard(daughterLabId, parentLabId);
			response = new Response<RateCard>();
			response.setData(rateCard);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(PathProxy.LabUrls.GET_PICKUPS_FOR_CB)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUPS_FOR_CB, notes = PathProxy.LabUrls.GET_PICKUPS_FOR_CB)
	public Response<LabTestPickupLookupResponse> getPickUpForCB(@QueryParam("collectionBoyId") String collectionBoyId,
			@QueryParam("from") @DefaultValue("0") Long from, @QueryParam("to") @DefaultValue("0") Long to,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size, @QueryParam("page") long page) {
		List<LabTestPickupLookupResponse> labTestPickups = null;
		Response<LabTestPickupLookupResponse> response = null;

		try {
			labTestPickups = locationServices.getRequestForCB(collectionBoyId, from, to, searchTerm, size, page);
			response = new Response<LabTestPickupLookupResponse>();
			response.setDataList(labTestPickups);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(PathProxy.LabUrls.GET_PICKUPS_FOR_DL)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUPS_FOR_DL, notes = PathProxy.LabUrls.GET_PICKUPS_FOR_DL)
	public Response<LabTestPickupLookupResponse> getPickUpForDL(@QueryParam("daughterLabId") String daughterLabId,
			@QueryParam("from") @DefaultValue("0") Long from, @QueryParam("to") @DefaultValue("0") Long to,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size, @QueryParam("page") long page) {
		List<LabTestPickupLookupResponse> labTestPickups = null;
		Response<LabTestPickupLookupResponse> response = null;
		try {
			labTestPickups = locationServices.getRequestForDL(daughterLabId, from, to, searchTerm, size, page);
			response = new Response<LabTestPickupLookupResponse>();
			response.setDataList(labTestPickups);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(PathProxy.LabUrls.GET_PICKUPS_FOR_PL)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUPS_FOR_PL, notes = PathProxy.LabUrls.GET_PICKUPS_FOR_PL)
	public Response<LabTestPickupLookupResponse> getPickUpForPL(@QueryParam("parentLabId") String parentLabId,
			@QueryParam("daughterLabId") String daughterLabId, @QueryParam("from") @DefaultValue("0") Long from,
			@QueryParam("to") @DefaultValue("0") Long to, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("size") int size, @QueryParam("page") long page) {
		List<LabTestPickupLookupResponse> labTestPickups = null;
		Response<LabTestPickupLookupResponse> response = null;

		try {
			labTestPickups = locationServices.getRequestForPL(parentLabId, daughterLabId, from, to, searchTerm, size,
					page);
			response = new Response<LabTestPickupLookupResponse>();
			response.setDataList(labTestPickups);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path(value = PathProxy.LabUrls.UPLOAD_REPORTS_MULTIPART)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.LabUrls.UPLOAD_REPORTS_MULTIPART, notes = PathProxy.LabUrls.UPLOAD_REPORTS_MULTIPART)
	public Response<LabReports> addRecordsMultipart(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("data") FormDataBodyPart data) {
		data.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		LabReportsAddRequest request = data.getValueAs(LabReportsAddRequest.class);

		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.addLabReports(file, request);

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@POST
	@Path(value = PathProxy.LabUrls.UPLOAD_REPORTS)
	@ApiOperation(value = PathProxy.LabUrls.UPLOAD_REPORTS, notes = PathProxy.LabUrls.UPLOAD_REPORTS)
	public Response<LabReports> addRecordsBase64(RecordUploadRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.addLabReportBase64(request.getFileDetails(),
				request.getLabReportsAddRequest());

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@POST
	@Path(value = PathProxy.LabUrls.EDIT_LAB_REPORTS)
	@ApiOperation(value = PathProxy.LabUrls.EDIT_LAB_REPORTS, notes = PathProxy.LabUrls.EDIT_LAB_REPORTS)
	public Response<LabReports> editLabReport(EditLabReportsRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.editLabReports(request);

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@GET
	@Path(value = PathProxy.LabUrls.GET_REPORTS_FOR_SAMPLES)
	@ApiOperation(value = PathProxy.LabUrls.GET_REPORTS_FOR_SAMPLES, notes = PathProxy.LabUrls.GET_REPORTS_FOR_SAMPLES)
	public Response<LabReports> getLabReports(@QueryParam("requestId") String requestId,
			@QueryParam("labTestSampleId") String labTestSampleId, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") long page, @QueryParam("size") int size) {

		List<LabReports> labReports = null;
		try {

			if (DPDoctorUtils.anyStringEmpty(labTestSampleId)) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}

			labReports = labReportsService.getLabReports(labTestSampleId, searchTerm, page, size);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error while getting lab reports");
		}

		Response<LabReports> response = new Response<LabReports>();
		response.setDataList(labReports);
		return response;
	}

	@GET
	@Path(value = PathProxy.LabUrls.GET_LAB_REPORTS)
	@ApiOperation(value = PathProxy.LabUrls.GET_LAB_REPORTS, notes = PathProxy.LabUrls.GET_LAB_REPORTS)
	public Response<Object> getLabReports(@QueryParam("locationId") String locationId,
			@QueryParam("isParent") @DefaultValue("true") Boolean isParent,
			@QueryParam("from") @DefaultValue("0") Long from, @QueryParam("to") @DefaultValue("0") Long to,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("page") long page, @QueryParam("size") int size) {

		List<PatientLabTestSampleReportResponse> labTestSamples = null;
		try {

			if (DPDoctorUtils.anyStringEmpty(locationId)) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}

			labTestSamples = locationServices.getLabReports(locationId, isParent, from, to, searchTerm, page, size);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("error while getting lab reports");
		}

		Response<Object> response = new Response<Object>();
		response.setData(locationServices.countLabReports(locationId, isParent, from, to, searchTerm));
		response.setDataList(labTestSamples);
		return response;
	}

	@Path(PathProxy.LabUrls.UPDATE_REQUEST_STATUS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.UPDATE_REQUEST_STATUS, notes = PathProxy.LabUrls.UPDATE_REQUEST_STATUS)
	public Response<Boolean> updateRequestStatus(@PathParam("id") String id, @QueryParam("status") String status) {
		// List<LabTestPickupLookupResponse> labTestPickups = null;
		Boolean result = null;
		Response<Boolean> response = null;

		try {
			result = locationServices.updateRequestStatus(id, status);
			response = new Response<Boolean>();
			response.setData(result);
		} catch (Exception e) {
			// TODO: handle exception
			// logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_DOCTOR, notes = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_DOCTOR)
	public Response<DoctorLabReportResponseWithCount> getLabReportsForDoctor(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("patientId") String patientId, @QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorLabReportResponseWithCount doctorLabReportResponseWithCount = new DoctorLabReportResponseWithCount();
		List<LabReportsResponse> labReportsResponses = labReportsService.getLabReportsForDoctor(doctorId, locationId,
				hospitalId, patientId, searchTerm, page, size);

		Response<DoctorLabReportResponseWithCount> response = new Response<DoctorLabReportResponseWithCount>();
		doctorLabReportResponseWithCount.setLabReportsResponses(labReportsResponses);
		doctorLabReportResponseWithCount.setCount(labReportsResponses.size());
		response.setData(doctorLabReportResponseWithCount);
		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_LAB)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_LAB, notes = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_LAB)
	public Response<DoctorLabReportResponseWithCount> getLabReportsForLab(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("patientId") String patientId, @QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorLabReportResponseWithCount doctorLabReportResponseWithCount = new DoctorLabReportResponseWithCount();
		List<LabReportsResponse> labReportsResponses = labReportsService.getLabReportsForLab(doctorId, locationId,
				hospitalId, patientId, searchTerm, page, size);

		Response<DoctorLabReportResponseWithCount> response = new Response<DoctorLabReportResponseWithCount>();
		doctorLabReportResponseWithCount.setLabReportsResponses(labReportsResponses);
		doctorLabReportResponseWithCount.setCount(labReportsResponses.size());
		response.setData(doctorLabReportResponseWithCount);
		return response;
	}

	@POST
	@Path(value = PathProxy.LabUrls.UPLOAD_REPORTS_TO_DOCTOR)
	@ApiOperation(value = PathProxy.LabUrls.UPLOAD_REPORTS_TO_DOCTOR, notes = PathProxy.LabUrls.UPLOAD_REPORTS_TO_DOCTOR)
	public Response<LabReports> addRecordsBase64(DoctorRecordUploadRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.addLabReportBase64(request.getFileDetails(),
				request.getLabReportsAddRequest());

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@POST
	@Path(value = PathProxy.LabUrls.CHANGE_PATIENT_SHARE_STATUS)
	@ApiOperation(value = PathProxy.LabUrls.CHANGE_PATIENT_SHARE_STATUS, notes = PathProxy.LabUrls.CHANGE_PATIENT_SHARE_STATUS)
	public Response<LabReportsResponse> changePatientShareStatus(@QueryParam("id") String id,
			@QueryParam("status") Boolean status) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReportsResponse labReports = labReportsService.changePatientShareStatus(id, status);

		Response<LabReportsResponse> response = new Response<LabReportsResponse>();
		response.setData(labReports);
		return response;
	}

	@Path(value = PathProxy.LabUrls.ADD_EDIT_DENTAL_WORKS)
	@POST
	@ApiOperation(value = PathProxy.LabUrls.ADD_EDIT_DENTAL_WORKS, notes = PathProxy.LabUrls.ADD_EDIT_DENTAL_WORKS)
	public Response<DentalWork> addEditPickupRequest(AddEditCustomWorkRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalWork> response = new Response<DentalWork>();
		response.setData(locationServices.addEditCustomWork(request));

		return response;
	}

	@Path(value = PathProxy.LabUrls.GET_DENTAL_WORKS)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.GET_DENTAL_WORKS, notes = PathProxy.LabUrls.GET_DENTAL_WORKS)
	public Response<Object> getDentalWorks(@QueryParam("locationId") String locationId, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getCustomWorks(page, size, searchTerm));
		return response;
	}

	@Path(value = PathProxy.LabUrls.DOWNLOAD_REQUISATION_FORM)
	@GET
	@ApiOperation(value = PathProxy.LabUrls.DOWNLOAD_REQUISATION_FORM, notes = PathProxy.LabUrls.DOWNLOAD_REQUISATION_FORM)
	public Response<String> downloadRequisationForm(@MatrixParam("ids") List<String> ids,
			@QueryParam("isParent") boolean isParent) {
		if (ids == null || ids.isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(labReportsService.downloadLabreportPrint(ids));
		return response;
	}

	@POST
	@Path(value = PathProxy.LabUrls.ALLOCATE_COLLECTION_BOY_DYNAMICALLY)
	@ApiOperation(value = PathProxy.LabUrls.ALLOCATE_COLLECTION_BOY_DYNAMICALLY, notes = PathProxy.LabUrls.ALLOCATE_COLLECTION_BOY_DYNAMICALLY)
	public Response<DynamicCollectionBoyAllocationResponse> AllocateDynamicCB(
			DynamicCollectionBoyAllocationRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DynamicCollectionBoyAllocationResponse dynamicCollectionBoyAllocationResponse = locationServices
				.allocateCBDynamically(request);
		Response<DynamicCollectionBoyAllocationResponse> response = new Response<DynamicCollectionBoyAllocationResponse>();
		response.setData(dynamicCollectionBoyAllocationResponse);
		return response;
	}

	@GET
	@Path(value = PathProxy.LabUrls.ADD_TO_FAVOURITE_RATE_CARD_TEST)
	@ApiOperation(value = PathProxy.LabUrls.ADD_TO_FAVOURITE_RATE_CARD_TEST, notes = PathProxy.LabUrls.ADD_TO_FAVOURITE_RATE_CARD_TEST)
	public Response<Boolean> makeFavouriteRateCardTest(@PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @PathParam("diagnosticTestId") String diagnosticTestId) {
		if (DPDoctorUtils.allStringsEmpty(hospitalId, locationId, diagnosticTestId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(locationServices.makeFavouriteRateCardTest(locationId, hospitalId, diagnosticTestId));
		return response;
	}

}