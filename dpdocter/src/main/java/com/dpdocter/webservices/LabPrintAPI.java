package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import com.dpdocter.beans.LabPrintDocument;
import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.LabPrintContentRequest;
import com.dpdocter.request.LabPrintDocumentAddEditRequest;
import com.dpdocter.services.LabPrintServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.Lab_PRINT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.Lab_PRINT_BASE_URL, description = "")
public class LabPrintAPI {

	private static Logger logger = LogManager.getLogger(LabPrintAPI.class.getName());
	@Autowired
	private LabPrintServices labprintservices;

	
	@PostMapping(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING)
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING)
	public Response<LabPrintSetting> addEditPrintSetting(LabPrintSetting request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getDoctorId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.addEditPrintSetting(request));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING)
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING, notes = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING)
	public Response<LabPrintSetting> getLabPrintSetting(@PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.getLabPrintSetting(locationId, hospitalId));

		return response;
	}

	
	@PostMapping(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER)
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER)
	public Response<LabPrintSetting> addEditPrintHeader(LabPrintContentRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getDoctorId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.setHeaderAndFooterSetup(request, "HEADER"));

		return response;
	}

	
	@PostMapping(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER)
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER)
	public Response<LabPrintSetting> addEditPrintFooter(LabPrintContentRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getDoctorId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.setHeaderAndFooterSetup(request, "FOOTER"));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENT)
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENT, notes = PathProxy.LabPrintUrls.GET_LAB_DOCUMENT)
	public Response<LabPrintDocument> getLabPrintDocument(@PathVariable("documentId") String documentId) {
		if (DPDoctorUtils.anyStringEmpty(documentId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintDocument> response = new Response<LabPrintDocument>();
		response.setData(labprintservices.getLabPrintDocument(documentId));

		return response;
	}

	
	@PostMapping(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_DOCUMENT)
	@ApiOperation(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_DOCUMENT, notes = PathProxy.LabPrintUrls.ADD_EDIT_LAB_DOCUMENT)
	public Response<LabPrintDocument> addLabPrintDocument(LabPrintDocumentAddEditRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.allStringsEmpty(request.getDoctorId(), request.getHospitalId(), request.getLocationId(),
				request.getUploadedByDoctorId(), request.getUploadedByLocationId(),
				request.getUploadedByHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintDocument> response = new Response<LabPrintDocument>();
		response.setData(labprintservices.addEditDocument(request));

		return response;
	}

	
	@GetMapping(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENTS)
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENTS, notes = PathProxy.LabPrintUrls.GET_LAB_DOCUMENTS)
	public Response<LabPrintDocument> getLabPrintDocuments(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("searchTerm") String searchTerm,
			@DefaultValue("0") @RequestParam("to") Long to, @DefaultValue("0") @RequestParam("from") Long from,
			@RequestParam("isParent") boolean isParent, @RequestParam("isdiscarded") boolean isdiscarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintDocument> response = new Response<LabPrintDocument>();
		response.setDataList(labprintservices.getLabPrintDocuments(page, size, locationId, doctorId, hospitalId,
				searchTerm, isParent, from, to, isdiscarded));

		return response;
	}

	
	@DeleteMapping(value = PathProxy.LabPrintUrls.DELETE_LAB_DOCUMENT)
	@ApiOperation(value = PathProxy.LabPrintUrls.DELETE_LAB_DOCUMENT, notes = PathProxy.LabPrintUrls.DELETE_LAB_DOCUMENT)
	public Response<Boolean> deleteLabPrintDocument(@PathVariable("documentId") String documentId,
			@RequestParam("isdiscarded") boolean isdiscarded) {
		if (DPDoctorUtils.anyStringEmpty(documentId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(labprintservices.deleteLabPrintDocument(documentId, isdiscarded));

		return response;
	}

}
