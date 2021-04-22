package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddEditProcedureSheetRequest;
import com.dpdocter.request.AddEditProcedureSheetStructureRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.ProcedureSheetResponse;
import com.dpdocter.response.ProcedureSheetStructureResponse;
import com.dpdocter.services.ProcedureSheetService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PROCEDURE_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.PROCEDURE_BASE_URL, description = "Endpoint for procedure")
public class ProcedureAPI {

	private static Logger logger = LogManager.getLogger(ProcedureAPI.class.getName());

	@Autowired
	private ProcedureSheetService procedureSheetService;

	
	@PostMapping(value = PathProxy.ProcedureUrls.ADD_PROCEDURE)
	@ApiOperation(value = PathProxy.ProcedureUrls.ADD_PROCEDURE, notes = PathProxy.ProcedureUrls.ADD_PROCEDURE)
	public Response<ProcedureSheetResponse> addProcedure(AddEditProcedureSheetRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetResponse procedureSheetResponse = procedureSheetService.addEditProcedureSheet(request);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ProcedureUrls.GET_PROCEDURE)
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE, notes = PathProxy.ProcedureUrls.GET_PROCEDURE)
	public Response<ProcedureSheetResponse> getProcedure(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetResponse procedureSheetResponse = procedureSheetService.getProcedureSheet(id);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ProcedureUrls.GET_PROCEDURE_LIST)
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE_LIST, notes = PathProxy.ProcedureUrls.GET_PROCEDURE_LIST)
	public Response<ProcedureSheetResponse> getProcedureList(@RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("doctorId") String doctorId,
			@RequestParam("patientId") String patientId, @DefaultValue("0") @RequestParam("from") Long from,
			@RequestParam("to") Long to, @RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size,
			@RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("type") String type) {
		List<ProcedureSheetResponse> procedureSheetResponses = procedureSheetService.getProcedureSheetList(doctorId,
				hospitalId, locationId, patientId, searchTerm, from, to, discarded, page, size, type);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setDataList(procedureSheetResponses);
		response.setCount(procedureSheetService.getProcedureSheetListCount(doctorId, hospitalId, locationId, patientId, searchTerm, from, to, discarded, type));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE)
	@ApiOperation(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE, notes = PathProxy.ProcedureUrls.DISCARD_PROCEDURE)
	public Response<ProcedureSheetResponse> deleteProcedure(@PathVariable("id") String id,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetResponse procedureSheetResponse = procedureSheetService.discardProcedureSheet(id, discarded);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.ProcedureUrls.ADD_PROCEDURE_STRUCTURE)
	@ApiOperation(value = PathProxy.ProcedureUrls.ADD_PROCEDURE_STRUCTURE, notes = PathProxy.ProcedureUrls.ADD_PROCEDURE_STRUCTURE)
	public Response<ProcedureSheetStructureResponse> addProcedureStructure(
			AddEditProcedureSheetStructureRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetStructureResponse procedureSheetResponse = procedureSheetService
				.addEditProcedureSheetStructure(request);
		Response<ProcedureSheetStructureResponse> response = new Response<ProcedureSheetStructureResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE)
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE, notes = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE)
	public Response<ProcedureSheetStructureResponse> getProcedureStructure(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetStructureResponse procedureSheetResponse = procedureSheetService.getProcedureSheetStructure(id);
		Response<ProcedureSheetStructureResponse> response = new Response<ProcedureSheetStructureResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE_LIST)
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE_LIST, notes = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE_LIST)
	public Response<ProcedureSheetStructureResponse> getProcedureList(@RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("doctorId") String doctorId,
			@DefaultValue("0") @RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("type") String type) {
		List<ProcedureSheetStructureResponse> procedureSheetResponses = procedureSheetService
				.getProcedureSheetStructureList(doctorId, hospitalId, locationId, searchTerm, from, to, discarded, page,
						size, type);
		Response<ProcedureSheetStructureResponse> response = new Response<ProcedureSheetStructureResponse>();
		response.setDataList(procedureSheetResponses);
		response.setCount(procedureSheetService.getProcedureSheetStructureListCount(doctorId, hospitalId, locationId, searchTerm, from, to, discarded, type));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE_STRUCTURE)
	@ApiOperation(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE_STRUCTURE, notes = PathProxy.ProcedureUrls.DISCARD_PROCEDURE_STRUCTURE)
	public Response<ProcedureSheetStructureResponse> getProcedure(@PathVariable("id") String id,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetStructureResponse procedureSheetResponse = procedureSheetService
				.discardProcedureSheetStructure(id, discarded);
		Response<ProcedureSheetStructureResponse> response = new Response<ProcedureSheetStructureResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	@PostMapping
	(value = PathProxy.ProcedureUrls.ADD_DIAGRAM)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.ProcedureUrls.ADD_DIAGRAM, notes = PathProxy.ProcedureUrls.ADD_DIAGRAM)
	public Response<ImageURLResponse> addDiagramMultipart(@RequestParam("file") MultipartFile file) {

		if (file == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ImageURLResponse imageURLResponse = procedureSheetService.addDiagrams(file);

		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(imageURLResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.ProcedureUrls.DOWNLOAD_PROCEDURE_SHEET)
	@ApiOperation(value = PathProxy.ProcedureUrls.DOWNLOAD_PROCEDURE_SHEET, notes = PathProxy.ProcedureUrls.DOWNLOAD_PROCEDURE_SHEET)
	public Response<String> downloadProcedureSheet(@PathVariable("id") String id) {
		Response<String> response = new Response<String>();
		response.setData(procedureSheetService.downloadProcedureSheet(id));
		return response;
	}

}
