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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingInvoice;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.Hospital;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DentalImagingLabDoctorRegistrationRequest;
import com.dpdocter.request.DentalimagingReportsUploadRequest;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DentalImagingDataResponse;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.DentalImagingVisitAnalyticsResponse;
import com.dpdocter.response.PatientDentalImagignVisitAnalyticsResponse;
import com.dpdocter.services.DentalImagingService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DENTAL_IMAGING_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_IMAGING_URL, description = "Endpoint for dental imaging")
public class DentalImagingAPI {

	private static Logger logger = LogManager.getLogger(DentalImagingAPI.class.getName());

	@Autowired
	DentalImagingService dentalImagingService;

	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST, notes = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST)
	public Response<DentalImagingResponse> addEditDentalRequest(@RequestBody DentalImagingRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalImagingResponse> response = new Response<DentalImagingResponse>();
		response.setData(dentalImagingService.addEditDentalImagingRequest(request));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_REQUESTS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_REQUESTS, notes = PathProxy.DentalImagingUrl.GET_REQUESTS)
	public Response<DentalImagingResponse> getPickupRequests(@RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("doctorId") String doctorId,
			@DefaultValue("0") @RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") long page,
			@RequestParam("type") String type) {

		Response<DentalImagingResponse> response = new Response<DentalImagingResponse>();
		response.setDataList(dentalImagingService.getRequests(locationId, hospitalId, doctorId, from, to, searchTerm,
				size, page, type));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION, notes = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION)
	public Response<DentalImagingLocationResponse> getServiceLocations(
			@MatrixParam(value = "dentalImagingServiceId") List<String> dentalImagingServiceId,
			@RequestParam("doctorId") String doctorId, @RequestParam("searchTerm") String searchTerm,
			@RequestParam("size") int size, @RequestParam("page") long page) {

		Response<DentalImagingLocationResponse> response = new Response<DentalImagingLocationResponse>();
		response.setDataList(
				dentalImagingService.getServiceLocations(dentalImagingServiceId, doctorId, searchTerm, size, page));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_SERVICES)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICES, notes = PathProxy.DentalImagingUrl.GET_SERVICES)
	public Response<DentalDiagnosticService> getPickupRequests(@RequestParam("searchTerm") String searchTerm,
			@RequestParam("size") int size, @RequestParam("page") long page, @RequestParam("type") String type) {

		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		response.setDataList(dentalImagingService.getServices(searchTerm, type, page, size));
		return response;
	}

	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION, notes = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION)
	public Response<Boolean> addEditDentalImagingLocationServiceAssociation(
			@RequestBody List<DentalImagingLocationServiceAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalImagingService.addEditDentalImagingLocationServiceAssociation(request));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_LOCATION_ASSOCIATED_SERVICES)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_LOCATION_ASSOCIATED_SERVICES, notes = PathProxy.DentalImagingUrl.GET_LOCATION_ASSOCIATED_SERVICES)
	public Response<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") long page,
			@RequestParam("type") String type, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		Response<DentalImagingLocationServiceAssociationLookupResponse> response = new Response<DentalImagingLocationServiceAssociationLookupResponse>();
		response.setDataList(dentalImagingService.getLocationAssociatedServices(locationId, hospitalId, searchTerm,
				type, page, size, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST, notes = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST)
	public Response<Hospital> getLocationAssociatedServices(@RequestParam("doctorId") String doctorId,
			@RequestParam("hospitalId") String hospitalId) {
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(dentalImagingService.getHospitalList(doctorId, hospitalId));
		return response;
	}

	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_RECORDS)	
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_RECORDS, notes = PathProxy.DentalImagingUrl.ADD_RECORDS)
	public Response<DentalImagingReports> addRecordsBase64(@RequestBody DentalimagingReportsUploadRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DentalImagingReports dentalImagingReports = dentalImagingService
				.addDentalImagingReportBase64(request.getFileDetails(), request.getRequest());

		Response<DentalImagingReports> response = new Response<DentalImagingReports>();
		response.setData(dentalImagingReports);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.DentalImagingUrl.DISCARD_REQUEST)
	@ApiOperation(value = PathProxy.DentalImagingUrl.DISCARD_REQUEST, notes = PathProxy.DentalImagingUrl.DISCARD_REQUEST)
	public Response<DentalImaging> discardRequest(@RequestParam("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		DentalImaging dentalImaging = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalImaging = dentalImagingService.discardRequest(id, discarded);
		Response<DentalImaging> response = new Response<DentalImaging>();
		response.setData(dentalImaging);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.DentalImagingUrl.DISCARD_RECORD)
	@ApiOperation(value = PathProxy.DentalImagingUrl.DISCARD_RECORD, notes = PathProxy.DentalImagingUrl.DISCARD_RECORD)
	public Response<DentalImagingReports> discardReports(@RequestParam("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		DentalImagingReports dentalImagingReports = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalImagingReports = dentalImagingService.discardReport(id, discarded);
		Response<DentalImagingReports> response = new Response<DentalImagingReports>();
		response.setData(dentalImagingReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_ASSOCIATED_DOCTORS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_ASSOCIATED_DOCTORS, notes = PathProxy.DentalImagingUrl.GET_ASSOCIATED_DOCTORS)
	public Response<Object> getLocationAssociatedServices(@RequestParam("hospitalId") String hospitalId,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") int page) {
		Response<Object> response = new Response<Object>();
		response.setData(dentalImagingService.countHospitalAssociatedDoctor(hospitalId,searchTerm));
		response.setDataList(dentalImagingService.getHospitalAssociatedDoctor(hospitalId, searchTerm, size, page));
		return response;
	}

	
	@PostMapping(value = PathProxy.DentalImagingUrl.DOCTOR_REGISTRATION)
	@ApiOperation(value = PathProxy.DentalImagingUrl.DOCTOR_REGISTRATION, notes = PathProxy.DentalImagingUrl.DOCTOR_REGISTRATION)
	public Response<Boolean> doctorRegistration(@RequestBody DentalImagingLabDoctorRegistrationRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalImagingService.dentalLabDoctorRegistration(request));
		return response;
	}

	
	@PostMapping(value = PathProxy.DentalImagingUrl.ADD_EDIT_INVOICE)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_INVOICE, notes = PathProxy.DentalImagingUrl.ADD_EDIT_INVOICE)
	public Response<DentalImagingInvoice> addEditDentalImaging(@RequestBody DentalImagingInvoice request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalImagingInvoice> response = new Response<DentalImagingInvoice>();

		response.setData(dentalImagingService.addEditInvoice(request , false));

		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_INVOICES)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_INVOICES, notes = PathProxy.DentalImagingUrl.GET_INVOICES)

	public Response<DentalImagingInvoice> getInvoices(@RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam("dentalImagingLocationId") String dentalImagingLocationId,
			@RequestParam("dentalImagingHospitalId") String dentalImagingHospitalId,
			@DefaultValue("0") @RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") long page,
			@RequestParam("isPaid") Boolean isPaid) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId, dentalImagingLocationId,
				dentalImagingHospitalId)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalImagingInvoice> response = new Response<DentalImagingInvoice>();
		response.setDataList(dentalImagingService.getInvoices(doctorId, locationId, hospitalId, dentalImagingLocationId,
				dentalImagingHospitalId, from, to, searchTerm, size, page, isPaid));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.DentalImagingUrl.DISCARD_INVOICE)
	@ApiOperation(value = PathProxy.DentalImagingUrl.DISCARD_INVOICE, notes = PathProxy.DentalImagingUrl.DISCARD_INVOICE)
	public Response<DentalImagingInvoice> discardInvoice(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		DentalImagingInvoice dentalImagingInvoice = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalImagingInvoice = dentalImagingService.discardInvoice(id, discarded);
		Response<DentalImagingInvoice> response = new Response<DentalImagingInvoice>();
		response.setData(dentalImagingInvoice);
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.CHANGE_PAYMENT_STATUS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.CHANGE_PAYMENT_STATUS, notes = PathProxy.DentalImagingUrl.CHANGE_PAYMENT_STATUS)
	public Response<DentalImagingInvoice> changePaymentStatus(@PathVariable("id") String id,
			@RequestParam("isPaid") boolean isPaid) {

		DentalImagingInvoice dentalImagingInvoice = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalImagingInvoice = dentalImagingService.changeInvoicePaymentStatus(id, isPaid);
		Response<DentalImagingInvoice> response = new Response<DentalImagingInvoice>();
		response.setData(dentalImagingInvoice);
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.DOWNLOAD_INVOICES)
	@ApiOperation(value = PathProxy.DentalImagingUrl.DOWNLOAD_INVOICES, notes = PathProxy.DentalImagingUrl.DOWNLOAD_INVOICES)
	public Response<String> downloadInvoices(@PathVariable("id") String id) {
		if (DPDoctorUtils.allStringsEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(dentalImagingService.downloadInvoice(id));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_SERVICE_VISIT_ANALYTICS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICE_VISIT_ANALYTICS, notes = PathProxy.DentalImagingUrl.GET_SERVICE_VISIT_ANALYTICS)
	public Response<DentalImagingVisitAnalyticsResponse> getVisitAnalytics(
			@RequestParam("dentalImagingLocationId") String dentalImagingLocationId,
			@RequestParam("dentalImagingHospitalId") String dentalImagingHospitalId,
			@DefaultValue("0") @RequestParam("from") String from, @RequestParam("to") String to) {
		if (DPDoctorUtils.allStringsEmpty(dentalImagingLocationId, dentalImagingHospitalId)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalImagingVisitAnalyticsResponse> response = new Response<DentalImagingVisitAnalyticsResponse>();
		response.setData(
				dentalImagingService.getVisitAnalytics(from, to, dentalImagingLocationId, dentalImagingHospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_PATIENT_VISIT_ANALYTICS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_PATIENT_VISIT_ANALYTICS, notes = PathProxy.DentalImagingUrl.GET_PATIENT_VISIT_ANALYTICS)
	public Response<AnalyticResponse> getPatientVisitAnalytics(
			@RequestParam("dentalImagingLocationId") String dentalImagingLocationId,
			@RequestParam("dentalImagingHospitalId") String dentalImagingHospitalId,
			@DefaultValue("0") @RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam("searchType") String searchType) {
		if (DPDoctorUtils.allStringsEmpty(dentalImagingLocationId, dentalImagingHospitalId)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(dentalImagingService.getPatientVisitAnalytics(from, to, dentalImagingLocationId,
				dentalImagingHospitalId, searchType));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_REPORTS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_REPORTS, notes = PathProxy.DentalImagingUrl.GET_REPORTS)
	public Response<DentalImagingReports> getReports(@RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("doctorId") String doctorId,
			@RequestParam("dentalImagingLocationId") String dentalImagingLocationId,
			@RequestParam("dentalImagingHospitalId") String dentalImagingHospitalId,
			@RequestParam("patientId") String patientId, @DefaultValue("0") @RequestParam("from") Long from,
			@RequestParam("to") Long to, @RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size,
			@RequestParam("page") long page) {

		Response<DentalImagingReports> response = new Response<DentalImagingReports>();
		response.setDataList(dentalImagingService.getReports(doctorId, locationId, hospitalId, dentalImagingLocationId,
				dentalImagingHospitalId, patientId, from, to, searchTerm, size, page));
		return response;
	}


	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_DOCTOR_VISIT_ANALYTICS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_DOCTOR_VISIT_ANALYTICS, notes = PathProxy.DentalImagingUrl.GET_DOCTOR_VISIT_ANALYTICS)
	public Response<PatientDentalImagignVisitAnalyticsResponse> getDoctorVisitAnalytics(
			@RequestParam("dentalImagingLocationId") String dentalImagingLocationId,
			@RequestParam("dentalImagingHospitalId") String dentalImagingHospitalId,
			@RequestParam("doctorId") String doctorId, @DefaultValue("0") @RequestParam("from") Long from,
			@RequestParam("to") Long to, @RequestParam("searchType") String searchType) {
		if (DPDoctorUtils.allStringsEmpty(dentalImagingLocationId, dentalImagingHospitalId, doctorId)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<PatientDentalImagignVisitAnalyticsResponse> response = new Response<PatientDentalImagignVisitAnalyticsResponse>();
		response.setDataList(dentalImagingService.getDoctorVisitAnalytics(from, to, dentalImagingLocationId,
				dentalImagingHospitalId, doctorId, searchType));
		return response;
	}

	
	@PostMapping(value = PathProxy.DentalImagingUrl.SEND_INVOICE_EMAIL)
	@ApiOperation(value = PathProxy.DentalImagingUrl.SEND_INVOICE_EMAIL, notes = PathProxy.DentalImagingUrl.SEND_INVOICE_EMAIL)
	public Response<Boolean> sendInvoiceEmail(@PathVariable("invoiceId") String invoiceId,
			@RequestParam("emailAddress") String emailAddress) {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalImagingService.emailInvoice(invoiceId, emailAddress));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.SEND_REPORT_EMAIL)
	@ApiOperation(value = PathProxy.DentalImagingUrl.SEND_REPORT_EMAIL, notes = PathProxy.DentalImagingUrl.SEND_REPORT_EMAIL)
	public Response<Boolean> sendReportEmail(@PathVariable("id") String id,
			@RequestParam("emailAddress") String emailAddress) {
		if (DPDoctorUtils.allStringsEmpty(id)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalImagingService.emailReports(id, emailAddress));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.CHANGE_VISIT_STATUS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.CHANGE_VISIT_STATUS, notes = PathProxy.DentalImagingUrl.CHANGE_VISIT_STATUS)
	public Response<DentalImaging> changeVisitStatus(@PathVariable("id") String id,
			@RequestParam("isVisited") boolean isVisited) {

		DentalImaging dentalImaging = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalImaging = dentalImagingService.changeVisitedStatus(id, isVisited);
		Response<DentalImaging> response = new Response<DentalImaging>();
		response.setData(dentalImaging);
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_DETAILED_DOCTOR_VISIT_ANALYTICS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_DETAILED_DOCTOR_VISIT_ANALYTICS, notes = PathProxy.DentalImagingUrl.GET_DETAILED_DOCTOR_VISIT_ANALYTICS)
	public Response<PatientDentalImagignVisitAnalyticsResponse> getDetailedDoctorVisitAnalytics(
			@RequestParam("dentalImagingLocationId") String dentalImagingLocationId,
			@RequestParam("dentalImagingHospitalId") String dentalImagingHospitalId,
			@RequestParam("doctorId") String doctorId, @DefaultValue("0") @RequestParam("from") Long from,
			@RequestParam("to") Long to, @RequestParam("searchType") String searchType, @RequestParam("page") int page,
			@RequestParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(dentalImagingLocationId, dentalImagingHospitalId, doctorId)) {

			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<PatientDentalImagignVisitAnalyticsResponse> response = new Response<PatientDentalImagignVisitAnalyticsResponse>();
		response.setData(dentalImagingService.getDetailedDoctorVisitAnalytics(from, to, dentalImagingLocationId,
				dentalImagingHospitalId, doctorId, searchType, page, size));
		return response;
	}

	
	@GetMapping(value = PathProxy.DentalImagingUrl.GET_DENTAL_IMAGING_DATA)
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_DENTAL_IMAGING_DATA, notes = PathProxy.DentalImagingUrl.GET_DENTAL_IMAGING_DATA)
	public Response<DentalImagingDataResponse> getDentalImagingData() {
		Response<DentalImagingDataResponse> response = new Response<DentalImagingDataResponse>();
		response.setData(dentalImagingService.getDentalImagingData());
		return response;
	}

}
