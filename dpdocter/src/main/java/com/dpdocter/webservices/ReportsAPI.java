package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BrokenAppointment;
import com.dpdocter.beans.ClinicalIndicator;
import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.EquipmentLogAMCAndServicingRegister;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.beans.RepairRecordsOrComplianceBook;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.OPDReportCustomResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsResponse;
import com.dpdocter.services.ReportsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.REPORTS_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REPORTS_BASE_URL, description = "Endpoint for Medical Report Register")
public class ReportsAPI {

	private static Logger logger = LogManager.getLogger(ReportsAPI.class.getName());

	@Autowired
	private ReportsService reportsService;

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_IPD_REPORTS, notes = PathProxy.ReportsUrls.GET_IPD_REPORTS)
	public Response<IPDReportsResponse> getIPDReports(@RequestParam("locationId") String locationId,
			@RequestParam("doctorId") String doctorId, @RequestParam("patientId") String patientId,
			@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime) {
		IPDReportsResponse ipdReports = reportsService.getIPDReportsList(locationId, doctorId, patientId, from, to,
				page, size, updatedTime);
		Response<IPDReportsResponse> response = new Response<IPDReportsResponse>();
		response.setData(ipdReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORTS, notes = PathProxy.ReportsUrls.GET_OPD_REPORTS)
	public Response<OPDReportsResponse> getOPDReports(@RequestParam("locationId") String locationId,@RequestParam("doctorId") String doctorId,@RequestParam("patientId") String patientId,@RequestParam("from") String from,
			@RequestParam("to")String to,@RequestParam("page")  long page, @RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime) {
		OPDReportsResponse opdReports = reportsService.getOPDReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OPDReportsResponse> response = new Response<OPDReportsResponse>();
		response.setData(opdReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_OT_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORTS, notes = PathProxy.ReportsUrls.GET_OT_REPORTS)
	public Response<OTReportsResponse> getOTReports(@RequestParam("locationId") String locationId,
			@RequestParam("doctorId") String doctorId, @RequestParam("patientId") String patientId,
			@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime) {
		OTReportsResponse otReports = reportsService.getOTReportsList(locationId, doctorId, patientId, from, to, page,
				size, updatedTime);
		Response<OTReportsResponse> response = new Response<OTReportsResponse>();
		response.setData(otReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORTS)
	public Response<DeliveryReportsResponse> getDeliveryReports(@RequestParam("locationId") String locationId,
			@RequestParam("doctorId") String doctorId, @RequestParam("patientId") String patientId,
			@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("updatedTime") String updatedTime) {
		DeliveryReportsResponse deliveryReports = reportsService.getDeliveryReportsList(locationId, doctorId, patientId,
				from, to, page, size, updatedTime);
		Response<DeliveryReportsResponse> response = new Response<DeliveryReportsResponse>();
		response.setData(deliveryReports);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.SUBMIT_IPD_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_IPD_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_IPD_REPORTS)
	public Response<IPDReports> submitIPDReports(IPDReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		IPDReports ipdReports = reportsService.submitIPDReport(request);
		Response<IPDReports> response = new Response<IPDReports>();
		response.setData(ipdReports);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.SUBMIT_OPD_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_OPD_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_OPD_REPORTS)
	public Response<OPDReports> submitOPDReports(OPDReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		OPDReports opdReports = reportsService.submitOPDReport(request);
		Response<OPDReports> response = new Response<OPDReports>();
		response.setData(opdReports);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.SUBMIT_OT_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_OT_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_OT_REPORTS)
	public Response<OTReports> submitOTReports(OTReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		OTReports otReports = reportsService.submitOTReport(request);
		Response<OTReports> response = new Response<OTReports>();
		response.setData(otReports);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.SUBMIT_DELIVERY_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.SUBMIT_DELIVERY_REPORTS, notes = PathProxy.ReportsUrls.SUBMIT_DELIVERY_REPORTS)
	public Response<DeliveryReports> submitDeliveryReports(DeliveryReports request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		DeliveryReports deliveryReports = reportsService.submitDeliveryReport(request);
		Response<DeliveryReports> response = new Response<DeliveryReports>();
		response.setData(deliveryReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.ADD_PRESCRIPTION_IN_OPD_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.ADD_PRESCRIPTION_IN_OPD_REPORTS, notes = PathProxy.ReportsUrls.ADD_PRESCRIPTION_IN_OPD_REPORTS)
	public Response<Boolean> addPrescriptionOPDReports() {
		Boolean save = reportsService.addPrescriptionOPDReports();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(save);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.ADD_CLINICAL_INDICATOR)
	@ApiOperation(value = PathProxy.ReportsUrls.ADD_CLINICAL_INDICATOR, notes = PathProxy.ReportsUrls.ADD_CLINICAL_INDICATOR)
	public Response<ClinicalIndicator> addClinicalIndicator(ClinicalIndicator request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		ClinicalIndicator save = reportsService.addClinicalIndicator(request);
		Response<ClinicalIndicator> response = new Response<ClinicalIndicator>();
		response.setData(save);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_CLINICAL_INDICATOR)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_CLINICAL_INDICATOR, notes = PathProxy.ReportsUrls.GET_CLINICAL_INDICATOR)
	public Response<ClinicalIndicator> getClinicalIndicator(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		ClinicalIndicator clinicalIndicator = reportsService.getClinicalIndicatorById(id);
		Response<ClinicalIndicator> response = new Response<ClinicalIndicator>();
		response.setData(clinicalIndicator);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_CLINICAL_INDICATORS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_CLINICAL_INDICATORS, notes = PathProxy.ReportsUrls.GET_CLINICAL_INDICATORS)
	public Response<ClinicalIndicator> getClinicalIndicators(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("type") String type) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		List<ClinicalIndicator> clinicalIndicator = reportsService.getClinicalIndicators(size, page, doctorId,
				locationId, hospitalId, discarded, type);
		Response<ClinicalIndicator> response = new Response<ClinicalIndicator>();
		response.setDataList(clinicalIndicator);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ReportsUrls.DELETE_CLINICAL_INDICATORS)
	@ApiOperation(value = PathProxy.ReportsUrls.DELETE_CLINICAL_INDICATORS, notes = PathProxy.ReportsUrls.DELETE_CLINICAL_INDICATORS)
	public Response<ClinicalIndicator> deleteClinicalIndicator(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		ClinicalIndicator clinicalIndicator = reportsService.discardClinicalIndicators(id, discarded);
		Response<ClinicalIndicator> response = new Response<ClinicalIndicator>();
		response.setData(clinicalIndicator);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.ADD_BROKEN_APPOINTMENT)
	@ApiOperation(value = PathProxy.ReportsUrls.ADD_BROKEN_APPOINTMENT, notes = PathProxy.ReportsUrls.ADD_BROKEN_APPOINTMENT)
	public Response<BrokenAppointment> addBrokenAppointment(BrokenAppointment request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		BrokenAppointment appointment = reportsService.addBrokenAppointment(request);
		Response<BrokenAppointment> response = new Response<BrokenAppointment>();
		response.setData(appointment);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_BROKEN_APPOINTMENT)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_BROKEN_APPOINTMENT, notes = PathProxy.ReportsUrls.GET_BROKEN_APPOINTMENT)
	public Response<BrokenAppointment> getBrokenAppointment(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		BrokenAppointment brokenAppointment = reportsService.getBrokenAppointment(id);
		Response<BrokenAppointment> response = new Response<BrokenAppointment>();
		response.setData(brokenAppointment);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_BROKEN_APPOINTMENTS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_BROKEN_APPOINTMENTS, notes = PathProxy.ReportsUrls.GET_BROKEN_APPOINTMENTS)
	public Response<BrokenAppointment> getBrokenAppointments(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("type") String type) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		List<BrokenAppointment> brokenAppointments = reportsService.getBrokenAppointments(size, page, doctorId,
				locationId, hospitalId, discarded);
		Response<BrokenAppointment> response = new Response<BrokenAppointment>();
		response.setDataList(brokenAppointments);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ReportsUrls.DELETE_BROKEN_APPOINTMENT)
	@ApiOperation(value = PathProxy.ReportsUrls.DELETE_BROKEN_APPOINTMENT, notes = PathProxy.ReportsUrls.DELETE_BROKEN_APPOINTMENT)
	public Response<BrokenAppointment> deleteBrokenAppointment(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		BrokenAppointment brokenAppointment = reportsService.discardBrokenAppointment(id, discarded);
		Response<BrokenAppointment> response = new Response<BrokenAppointment>();
		response.setData(brokenAppointment);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.ADD_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER)
	@ApiOperation(value = PathProxy.ReportsUrls.ADD_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER, notes = PathProxy.ReportsUrls.ADD_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER)
	public Response<EquipmentLogAMCAndServicingRegister> addEquipmentLogAMCAndServicingRegister(
			EquipmentLogAMCAndServicingRegister request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		EquipmentLogAMCAndServicingRegister amcAndServicingRegister = reportsService
				.addEquipmentLogAMCAndServicingRegister(request);
		Response<EquipmentLogAMCAndServicingRegister> response = new Response<EquipmentLogAMCAndServicingRegister>();
		response.setData(amcAndServicingRegister);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER, notes = PathProxy.ReportsUrls.GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER)
	public Response<EquipmentLogAMCAndServicingRegister> getEquipmentLogAMCAndServicingRegister(
			@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		EquipmentLogAMCAndServicingRegister amcAndServicingRegister = reportsService
				.getEquipmentLogAMCAndServicingRegisterById(id);
		Response<EquipmentLogAMCAndServicingRegister> response = new Response<EquipmentLogAMCAndServicingRegister>();
		response.setData(amcAndServicingRegister);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTERS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTERS, notes = PathProxy.ReportsUrls.GET_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTERS)
	public Response<EquipmentLogAMCAndServicingRegister> getEquipmentLogAMCAndServicingRegisters(
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("type") String type) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		List<EquipmentLogAMCAndServicingRegister> amcAndServicingRegisters = reportsService
				.getEquipmentLogAMCAndServicingRegisters(size, page, doctorId, locationId, hospitalId, discarded);
		Response<EquipmentLogAMCAndServicingRegister> response = new Response<EquipmentLogAMCAndServicingRegister>();
		response.setDataList(amcAndServicingRegisters);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ReportsUrls.DELETE_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER)
	@ApiOperation(value = PathProxy.ReportsUrls.DELETE_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER, notes = PathProxy.ReportsUrls.DELETE_EQUIPMENT_LOG_AMC_AND_SERVICING_REGISTER)
	public Response<EquipmentLogAMCAndServicingRegister> deleteEquipmentLogAMCAndServicingRegister(
			@PathVariable("id") String id, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		EquipmentLogAMCAndServicingRegister amcAndServicingRegister = reportsService
				.discardEquipmentLogAMCAndServicingRegister(id, discarded);
		Response<EquipmentLogAMCAndServicingRegister> response = new Response<EquipmentLogAMCAndServicingRegister>();
		response.setData(amcAndServicingRegister);
		return response;
	}

	
	@PostMapping(value = PathProxy.ReportsUrls.ADD_REPAIR_RECORDS_OR_COMPLAINCE_BOOK)
	@ApiOperation(value = PathProxy.ReportsUrls.ADD_REPAIR_RECORDS_OR_COMPLAINCE_BOOK, notes = PathProxy.ReportsUrls.ADD_REPAIR_RECORDS_OR_COMPLAINCE_BOOK)
	public Response<RepairRecordsOrComplianceBook> addRepairRecordsOrComplianceBook(
			RepairRecordsOrComplianceBook request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		RepairRecordsOrComplianceBook complianceBook = reportsService.addRepairRecordsOrComplianceBook(request);
		Response<RepairRecordsOrComplianceBook> response = new Response<RepairRecordsOrComplianceBook>();
		response.setData(complianceBook);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOK)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOK, notes = PathProxy.ReportsUrls.GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOK)
	public Response<RepairRecordsOrComplianceBook> getRepairRecordsOrComplianceBook(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		RepairRecordsOrComplianceBook recordsOrComplianceBook = reportsService.getRepairRecordsOrComplianceBookById(id);
		Response<RepairRecordsOrComplianceBook> response = new Response<RepairRecordsOrComplianceBook>();
		response.setData(recordsOrComplianceBook);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOKS)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOKS, notes = PathProxy.ReportsUrls.GET_REPAIR_RECORDS_OR_COMPLAINCE_BOOKS)
	public Response<RepairRecordsOrComplianceBook> getRepairRecordsOrComplianceBooks(@RequestParam("size") int size,
			@RequestParam("page") int page, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("type") String type) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,locationId,hospitalId is NULL");
		}
		List<RepairRecordsOrComplianceBook> repairRecordsOrComplianceBooks = reportsService
				.getRepairRecordsOrComplianceBooks(size, page, doctorId, locationId, hospitalId, discarded);
		Response<RepairRecordsOrComplianceBook> response = new Response<RepairRecordsOrComplianceBook>();
		response.setDataList(repairRecordsOrComplianceBooks);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ReportsUrls.DELETE_REPAIR_RECORDS_OR_COMPLAINCE_BOOK)
	@ApiOperation(value = PathProxy.ReportsUrls.DELETE_REPAIR_RECORDS_OR_COMPLAINCE_BOOK, notes = PathProxy.ReportsUrls.DELETE_REPAIR_RECORDS_OR_COMPLAINCE_BOOK)
	public Response<RepairRecordsOrComplianceBook> deleteRepairRecordsOrComplianceBook(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "id is NULL");
		}
		RepairRecordsOrComplianceBook complianceBook = reportsService.discardrepairRecordsOrComplianceBook(id,
				discarded);
		Response<RepairRecordsOrComplianceBook> response = new Response<RepairRecordsOrComplianceBook>();
		response.setData(complianceBook);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.DOWNLOAD_OT_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.DOWNLOAD_OT_REPORTS, notes = PathProxy.ReportsUrls.DOWNLOAD_OT_REPORTS)
	public Response<String> downloadOTReports(@PathVariable("otId") String otId) {
		if (DPDoctorUtils.allStringsEmpty(otId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(reportsService.getOTReportsFile(otId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.ReportsUrls.DOWNLOAD_DELIVERY_REPORT)
	@ApiOperation(value = PathProxy.ReportsUrls.DOWNLOAD_DELIVERY_REPORT, notes = PathProxy.ReportsUrls.DOWNLOAD_DELIVERY_REPORT)
	public Response<String> downloadDeliveryReports(@PathVariable("reportId") String reportId) {
		if (DPDoctorUtils.allStringsEmpty(reportId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(reportsService.getDeliveryReportsFile(reportId));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.ReportsUrls.UPDATE_OT_REPORTS)
	@ApiOperation(value = PathProxy.ReportsUrls.UPDATE_OT_REPORTS, notes = PathProxy.ReportsUrls.UPDATE_OT_REPORTS)
	public Response<Boolean> updateOTReports() {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(reportsService.updateOTReports());
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_IPD_REPORT)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_IPD_REPORT, notes = PathProxy.ReportsUrls.GET_IPD_REPORT)
	public Response<IPDReports> getIPDReportById(@PathVariable("id") String reportId) {
		IPDReports ipdReports = reportsService.getIPDReportById(reportId);
		Response<IPDReports> response = new Response<IPDReports>();
		response.setData(ipdReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_OPD_REPORT)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OPD_REPORT, notes = PathProxy.ReportsUrls.GET_OPD_REPORT)
	public Response<OPDReportCustomResponse> getOPDReportById(@PathVariable("id") String reportId) {
		OPDReportCustomResponse opdReports = reportsService.getOPDReportById(reportId);
		Response<OPDReportCustomResponse> response = new Response<OPDReportCustomResponse>();
		response.setData(opdReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_OT_REPORT)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_OT_REPORT, notes = PathProxy.ReportsUrls.GET_OT_REPORT)
	public Response<OTReports> getOTReportById(@PathVariable("id") String reportId) {
		OTReports otReports = reportsService.getOTReportById(reportId);
		Response<OTReports> response = new Response<OTReports>();
		response.setData(otReports);
		return response;
	}

	
	@GetMapping(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORT)
	@ApiOperation(value = PathProxy.ReportsUrls.GET_DELIVERY_REPORT, notes = PathProxy.ReportsUrls.GET_DELIVERY_REPORT)
	public Response<DeliveryReports> getDeliveryReportById(@PathVariable("id") String reportId) {
		DeliveryReports deliveryReports = reportsService.getDeliveryReportById(reportId);
		Response<DeliveryReports> response = new Response<DeliveryReports>();
		response.setData(deliveryReports);
		return response;
	}

}
