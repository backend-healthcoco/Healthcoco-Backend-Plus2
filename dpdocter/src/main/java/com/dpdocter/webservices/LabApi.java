package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.LAB_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.LAB_BASE_URL, description = "")
public class LabApi {

	private static Logger logger = LogManager.getLogger(LabApi.class.getName());

	@Autowired
	private LabService labService;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private LabReportsService labReportsService;

	
	@GetMapping(value = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT)
	@ApiOperation(value = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT, notes = PathProxy.LabUrls.GET_CLINICS_WITH_REPORTS_COUNT)
	public Response<List<Clinic>> getClinicWithReportCount(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId) || DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<List<Clinic>> response = new Response<List<Clinic>>();
		response.setDataList(labService.getClinicWithReportCount(doctorId, locationId, hospitalId));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	@ApiOperation(value = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR, notes = PathProxy.LabUrls.GET_REPORTS_FOR_SPECIFIC_DOCTOR)
	public Response<Records> getReports(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(value = "prescribedByDoctorId") String prescribedByDoctorId,
			@RequestParam(value = "prescribedByLocationId") String prescribedByLocationId,
			@RequestParam(value = "prescribedByHospitalId") String prescribedByHospitalId,
			@RequestParam(value = "size") int size, @RequestParam(value = "page") long page) {
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

	
	@PostMapping(value = PathProxy.LabUrls.ADD_EDIT_PICKUP_REQUEST)
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

	
	@GetMapping(value = PathProxy.LabUrls.GET_CB_LIST_BY_PARENT_LAB)
	@ApiOperation(value = PathProxy.LabUrls.GET_CB_LIST_BY_PARENT_LAB, notes = PathProxy.LabUrls.GET_CB_LIST_BY_PARENT_LAB)
	public Response<Object> getCBListByParentLab(@RequestParam("locationId") String locationId,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getCollectionBoyList(size, page, locationId, searchTerm ,LabType.DIAGNOSTIC.getType()));
		response.setData(locationServices.getCBCount(locationId, searchTerm , LabType.DIAGNOSTIC.getType()));
		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_RATE_CARDS)
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARDS, notes = PathProxy.LabUrls.GET_RATE_CARDS)
	public Response<Object> getRateCards(@RequestParam("locationId") String locationId, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getRateCards(page, size, searchTerm, locationId));
		response.setData(locationServices.getRateCardCount(searchTerm, locationId));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARD_TEST, notes = PathProxy.LabUrls.GET_RATE_CARD_TEST)
	public Response<RateCardTestAssociationLookupResponse> getRateCardTests(@RequestParam("rateCardId") String rateCardId,
			@RequestParam("labId") String labId, @RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm,
			@RequestParam("discarded") @DefaultValue("false") Boolean discarded) {
		if (rateCardId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociationLookupResponse> response = new Response<RateCardTestAssociationLookupResponse>();
		response.setDataList(locationServices.getRateCardTests(page, size, searchTerm, rateCardId, labId, discarded));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_GROUPED_LAB_TEST)
	@ApiOperation(value = PathProxy.LabUrls.GET_GROUPED_LAB_TEST, notes = PathProxy.LabUrls.GET_GROUPED_LAB_TEST)
	public Response<LabTestGroupResponse> getGroupedLabTest(@RequestParam("daughterLabId") String daughterLabId,
			@RequestParam("parentLabId") String parentLabId, @RequestParam("labId") String labId,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm) {
		if (daughterLabId == null || parentLabId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabTestGroupResponse> response = new Response<LabTestGroupResponse>();
		response.setDataList(
				locationServices.getGroupedLabTests(page, size, searchTerm, daughterLabId, parentLabId, labId));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_RATE_CARD_TEST_BY_DL)
	@ApiOperation(value = PathProxy.LabUrls.GET_RATE_CARD_TEST_BY_DL, notes = PathProxy.LabUrls.GET_RATE_CARD_TEST_BY_DL)
	public Response<RateCardTestAssociationByLBResponse> getRateCardTests(
			@RequestParam("daughterLabId") String daughterLabId, @RequestParam("parentLabId") String parentLabId,
			@RequestParam("labId") String labId, @RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("specimen") String specimen) {
		if (daughterLabId == null || parentLabId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardTestAssociationByLBResponse> response = new Response<RateCardTestAssociationByLBResponse>();
		response.setDataList(
				locationServices.getRateCardTests(page, size, searchTerm, daughterLabId, parentLabId, labId, specimen));

		return response;
	}

	
	@PostMapping(value = PathProxy.LabUrls.ADD_EDIT_RATE_CARD)
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

	
	@PostMapping(value = PathProxy.LabUrls.ADD_EDIT_RATE_CARD_TESTS)
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

	
	@GetMapping(value = PathProxy.LabUrls.VERIFY_CRN)
	@ApiOperation(value = PathProxy.LabUrls.VERIFY_CRN, notes = PathProxy.LabUrls.VERIFY_CRN)
	public Response<Boolean> verifyCRN(@RequestParam("locationId") String locationId,
			@RequestParam("requestId") String requestId, @RequestParam("crn") String crn) {
		if (crn == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(locationServices.verifyCRN(locationId, crn, requestId));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.DISCARD_COLLECTION_BOY)
	@ApiOperation(value = PathProxy.LabUrls.DISCARD_COLLECTION_BOY, notes = PathProxy.LabUrls.DISCARD_COLLECTION_BOY)
	public Response<CollectionBoy> discardCB(@RequestParam("collectionBoyId") String collectionBoyId,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CollectionBoy> response = new Response<CollectionBoy>();
		response.setData(locationServices.discardCB(collectionBoyId, discarded));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.CHANGE_AVAILABILITY_OF_CB)
	@ApiOperation(value = PathProxy.LabUrls.CHANGE_AVAILABILITY_OF_CB, notes = PathProxy.LabUrls.CHANGE_AVAILABILITY_OF_CB)
	public Response<CollectionBoy> changeCBAvailabilty(@RequestParam("collectionBoyId") String collectionBoyId,
			@RequestParam("isAvailable") Boolean isAvailable) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CollectionBoy> response = new Response<CollectionBoy>();
		response.setData(locationServices.changeAvailability(collectionBoyId, isAvailable));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_PICKUP_REQUEST_BY_ID)
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUP_REQUEST_BY_ID, notes = PathProxy.LabUrls.GET_PICKUP_REQUEST_BY_ID)
	public Response<LabTestPickupLookupResponse> getPickupRequestById(@RequestParam("id") String id,
			@RequestParam("requestId") String requestId) {
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

	
	@PostMapping(value = PathProxy.LabUrls.ADD_CB_LAB_ASSOCIATION)
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

	
	@GetMapping(value = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION)
	@ApiOperation(value = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION, notes = PathProxy.LabUrls.GET_CB_LAB_ASSOCIATION)
	public Response<Location> getCBLabAssociation(@RequestParam("parentLabId") String parentLabId,
			@RequestParam("daughterLabId") String daughterLabId, @RequestParam("collectionBoyId") String collectionBoyId,
			@RequestParam("size") int size, @RequestParam("page") long page) {
		if (collectionBoyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(
				locationServices.getCBAssociatedLabs(parentLabId, daughterLabId, collectionBoyId, size, page));
		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	@ApiOperation(value = PathProxy.LabUrls.GET_ASSOCIATED_LABS, notes = PathProxy.LabUrls.GET_ASSOCIATED_LABS)
	public Response<Location> getAssociateLabs(@RequestParam("locationId") String locationId,
			@RequestParam("isParent")   Boolean isParent, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("page") long page, @RequestParam("size") int size) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(locationServices.getAssociatedLabs(locationId, isParent, searchTerm, page, size));
		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.GET_CLINICS_AND_LABS)
	@ApiOperation(value = PathProxy.LabUrls.GET_CLINICS_AND_LABS, notes = PathProxy.LabUrls.GET_CLINICS_AND_LABS)
	public Response<Location> getClinics(@RequestParam(value = "page") long page, @RequestParam(value = "size") int size,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "isClinic") Boolean isClinic,
			@RequestParam(value = "isLab") Boolean isLab, @RequestParam(value = "isDentalWorksLab") Boolean isDentalWorksLab ,  @RequestParam(value = "isDentalImagingLab") Boolean isDentalImagingLab ,  @RequestParam(value = "isParent") Boolean isParent,
			@RequestParam(value = "searchTerm") String searchTerm) {

		List<Location> locations = locationServices.getClinics(page, size, hospitalId, isClinic, isLab, isParent, isDentalWorksLab , isDentalImagingLab,
				searchTerm);

		Response<Location> response = new Response<Location>();
		response.setDataList(locations);
		return response;
	}

	
	@GetMapping(PathProxy.LabUrls.GET_SPECIMEN_LIST)
	@ApiOperation(value = PathProxy.LabUrls.GET_SPECIMEN_LIST, notes = PathProxy.LabUrls.GET_SPECIMEN_LIST)
	public Response<Specimen> getSpecimen(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm) {
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

	
	@PostMapping(PathProxy.LabUrls.EDIT_COLLECTION_BOY)
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

	
	@PostMapping(PathProxy.LabUrls.ADD_EDIT_LAB_RATE_CARD_ASSOCIAITION)
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

	
	@GetMapping(PathProxy.LabUrls.GET_DL_RATE_CARD)
	@ApiOperation(value = PathProxy.LabUrls.GET_DL_RATE_CARD, notes = PathProxy.LabUrls.GET_DL_RATE_CARD)
	public Response<RateCard> getDLRateCard(@RequestParam("parentLabId") String parentLabId,
			@RequestParam("daughterLabId") String daughterLabId) {
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

	
	@GetMapping(PathProxy.LabUrls.GET_PICKUPS_FOR_CB)
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUPS_FOR_CB, notes = PathProxy.LabUrls.GET_PICKUPS_FOR_CB)
	public Response<LabTestPickupLookupResponse> getPickUpForCB(@RequestParam("collectionBoyId") String collectionBoyId,
			@RequestParam("from") @DefaultValue("0") Long from, @RequestParam("to") @DefaultValue("0") Long to,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") long page) {
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

	
	@GetMapping(PathProxy.LabUrls.GET_PICKUPS_FOR_DL)
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUPS_FOR_DL, notes = PathProxy.LabUrls.GET_PICKUPS_FOR_DL)
	public Response<LabTestPickupLookupResponse> getPickUpForDL(@RequestParam("daughterLabId") String daughterLabId,
			@RequestParam("from") @DefaultValue("0") Long from, @RequestParam("to") @DefaultValue("0") Long to,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") long page) {
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

	
	@GetMapping(PathProxy.LabUrls.GET_PICKUPS_FOR_PL)
	@ApiOperation(value = PathProxy.LabUrls.GET_PICKUPS_FOR_PL, notes = PathProxy.LabUrls.GET_PICKUPS_FOR_PL)
	public Response<LabTestPickupLookupResponse> getPickUpForPL(@RequestParam("parentLabId") String parentLabId,
			@RequestParam("daughterLabId") String daughterLabId, @RequestParam("from") @DefaultValue("0") Long from,
			@RequestParam("to") @DefaultValue("0") Long to, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("size") int size, @RequestParam("page") long page) {
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

	@PostMapping(value = PathProxy.LabUrls.UPLOAD_REPORTS_MULTIPART)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.LabUrls.UPLOAD_REPORTS_MULTIPART, notes = PathProxy.LabUrls.UPLOAD_REPORTS_MULTIPART)
	public Response<LabReports> addRecordsMultipart(@RequestParam("file") MultipartFile file,
			@RequestBody LabReportsAddRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.addLabReports(file, request);

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@PostMapping(value = PathProxy.LabUrls.UPLOAD_REPORTS)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.LabUrls.UPLOAD_REPORTS, notes = PathProxy.LabUrls.UPLOAD_REPORTS)
	public Response<LabReports> addRecordsBase64(@RequestParam("file") MultipartFile file, RecordUploadRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.addLabReportBase64(file, request.getLabReportsAddRequest());

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@PostMapping
	(value = PathProxy.LabUrls.EDIT_LAB_REPORTS)
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

	@GetMapping
	(value = PathProxy.LabUrls.GET_REPORTS_FOR_SAMPLES)
	@ApiOperation(value = PathProxy.LabUrls.GET_REPORTS_FOR_SAMPLES, notes = PathProxy.LabUrls.GET_REPORTS_FOR_SAMPLES)
	public Response<LabReports> getLabReports(@RequestParam("requestId") String requestId,
			@RequestParam("labTestSampleId") String labTestSampleId, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("page") long page, @RequestParam("size") int size) {

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

	@GetMapping
	(value = PathProxy.LabUrls.GET_LAB_REPORTS)
	@ApiOperation(value = PathProxy.LabUrls.GET_LAB_REPORTS, notes = PathProxy.LabUrls.GET_LAB_REPORTS)
	public Response<Object> getLabReports(@RequestParam("locationId") String locationId,
			@RequestParam("isParent")   Boolean isParent,
			@RequestParam("from") @DefaultValue("0") Long from, @RequestParam("to") @DefaultValue("0") Long to,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("page") long page, @RequestParam("size") int size) {

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

	
	@GetMapping(PathProxy.LabUrls.UPDATE_REQUEST_STATUS)
	@ApiOperation(value = PathProxy.LabUrls.UPDATE_REQUEST_STATUS, notes = PathProxy.LabUrls.UPDATE_REQUEST_STATUS)
	public Response<Boolean> updateRequestStatus(@PathVariable("id") String id, @RequestParam("status") String status) {
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

	
	@GetMapping(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_DOCTOR)
	@ApiOperation(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_DOCTOR, notes = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_DOCTOR)
	public Response<DoctorLabReportResponseWithCount> getLabReportsForDoctor(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("patientId") String patientId, @RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm) {
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

	
	@GetMapping(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_LAB)
	@ApiOperation(value = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_LAB, notes = PathProxy.LabUrls.GET_LAB_REPORTS_FOR_LAB)
	public Response<DoctorLabReportResponseWithCount> getLabReportsForLab(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("patientId") String patientId, @RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm) {
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

	@PostMapping(value = PathProxy.LabUrls.UPLOAD_REPORTS_TO_DOCTOR)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = PathProxy.LabUrls.UPLOAD_REPORTS_TO_DOCTOR, notes = PathProxy.LabUrls.UPLOAD_REPORTS_TO_DOCTOR)
	public Response<LabReports> addRecordsBase64(@RequestParam("file") MultipartFile file, DoctorRecordUploadRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReports labReports = labReportsService.addLabReportBase64(file, request.getLabReportsAddRequest());

		Response<LabReports> response = new Response<LabReports>();
		response.setData(labReports);
		return response;
	}

	@PostMapping
	(value = PathProxy.LabUrls.CHANGE_PATIENT_SHARE_STATUS)
	@ApiOperation(value = PathProxy.LabUrls.CHANGE_PATIENT_SHARE_STATUS, notes = PathProxy.LabUrls.CHANGE_PATIENT_SHARE_STATUS)
	public Response<LabReportsResponse> changePatientShareStatus(@RequestParam("id") String id,
			@RequestParam("status") Boolean status) {
		if (id == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabReportsResponse labReports = labReportsService.changePatientShareStatus(id, status);

		Response<LabReportsResponse> response = new Response<LabReportsResponse>();
		response.setData(labReports);
		return response;
	}

	
	@PostMapping(value = PathProxy.LabUrls.ADD_EDIT_DENTAL_WORKS)
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

	
	@GetMapping(value = PathProxy.LabUrls.GET_DENTAL_WORKS)
	@ApiOperation(value = PathProxy.LabUrls.GET_DENTAL_WORKS, notes = PathProxy.LabUrls.GET_DENTAL_WORKS)
	public Response<Object> getDentalWorks(@RequestParam("locationId") String locationId, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getCustomWorks(page, size, searchTerm));
		return response;
	}

	
	@GetMapping(value = PathProxy.LabUrls.DOWNLOAD_REQUISATION_FORM)
	@ApiOperation(value = PathProxy.LabUrls.DOWNLOAD_REQUISATION_FORM, notes = PathProxy.LabUrls.DOWNLOAD_REQUISATION_FORM)
	public Response<String> downloadRequisationForm(@MatrixParam("ids") List<String> ids,
			@RequestParam("isParent") boolean isParent) {
		if (ids == null || ids.isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(labReportsService.downloadLabreportPrint(ids));
		return response;
	}

	@PostMapping
	(value = PathProxy.LabUrls.ALLOCATE_COLLECTION_BOY_DYNAMICALLY)
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

	@GetMapping
	(value = PathProxy.LabUrls.ADD_TO_FAVOURITE_RATE_CARD_TEST)
	@ApiOperation(value = PathProxy.LabUrls.ADD_TO_FAVOURITE_RATE_CARD_TEST, notes = PathProxy.LabUrls.ADD_TO_FAVOURITE_RATE_CARD_TEST)
	public Response<Boolean> makeFavouriteRateCardTest(@PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId, @PathVariable("diagnosticTestId") String diagnosticTestId) {
		if (DPDoctorUtils.allStringsEmpty(hospitalId, locationId, diagnosticTestId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(locationServices.makeFavouriteRateCardTest(locationId, hospitalId, diagnosticTestId));
		return response;
	}

}