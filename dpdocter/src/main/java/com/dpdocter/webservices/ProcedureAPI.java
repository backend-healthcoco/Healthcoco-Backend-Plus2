package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

@Component
@Path(PathProxy.PROCEDURE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PROCEDURE_BASE_URL, description = "Endpoint for procedure")
public class ProcedureAPI {

	private static Logger logger = Logger.getLogger(ProcedureAPI.class.getName());

	@Autowired
	private ProcedureSheetService procedureSheetService;

	@Path(value = PathProxy.ProcedureUrls.ADD_PROCEDURE)
	@POST
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

	@Path(value = PathProxy.ProcedureUrls.GET_PROCEDURE)
	@GET
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE, notes = PathProxy.ProcedureUrls.GET_PROCEDURE)
	public Response<ProcedureSheetResponse> getProcedure(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetResponse procedureSheetResponse = procedureSheetService.getProcedureSheet(id);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	@Path(value = PathProxy.ProcedureUrls.GET_PROCEDURE_LIST)
	@GET
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE_LIST, notes = PathProxy.ProcedureUrls.GET_PROCEDURE_LIST)
	public Response<ProcedureSheetResponse> getProcedureList(@QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@QueryParam("patientId") String patientId, @DefaultValue("0") @QueryParam("from") Long from,
			@QueryParam("to") Long to, @QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page, @QueryParam("discarded") Boolean discarded, @QueryParam("type") String type) {
		List<ProcedureSheetResponse> procedureSheetResponses = procedureSheetService.getProcedureSheetList(doctorId,
				hospitalId, locationId, patientId, searchTerm, from, to, discarded, page, size, type);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setDataList(procedureSheetResponses);
		return response;
	}

	@Path(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE)
	@DELETE
	@ApiOperation(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE, notes = PathProxy.ProcedureUrls.DISCARD_PROCEDURE)
	public Response<ProcedureSheetResponse> deleteProcedure(@PathParam("id") String id,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetResponse procedureSheetResponse = procedureSheetService.discardProcedureSheet(id, discarded);
		Response<ProcedureSheetResponse> response = new Response<ProcedureSheetResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	@Path(value = PathProxy.ProcedureUrls.ADD_PROCEDURE_STRUCTURE)
	@POST
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

	@Path(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE)
	@GET
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE, notes = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE)
	public Response<ProcedureSheetStructureResponse> getProcedureStructure(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureSheetStructureResponse procedureSheetResponse = procedureSheetService.getProcedureSheetStructure(id);
		Response<ProcedureSheetStructureResponse> response = new Response<ProcedureSheetStructureResponse>();
		response.setData(procedureSheetResponse);
		return response;
	}

	@Path(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE_LIST)
	@GET
	@ApiOperation(value = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE_LIST, notes = PathProxy.ProcedureUrls.GET_PROCEDURE_STRUCTURE_LIST)
	public Response<ProcedureSheetStructureResponse> getProcedureList(@QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@DefaultValue("0") @QueryParam("from") Long from, @QueryParam("to") Long to,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("discarded") Boolean discarded, @QueryParam("type") String type) {
		List<ProcedureSheetStructureResponse> procedureSheetResponses = procedureSheetService
				.getProcedureSheetStructureList(doctorId, hospitalId, locationId, searchTerm, from, to, discarded, page,
						size, type);
		Response<ProcedureSheetStructureResponse> response = new Response<ProcedureSheetStructureResponse>();
		response.setDataList(procedureSheetResponses);
		return response;
	}

	@Path(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE_STRUCTURE)
	@DELETE
	@ApiOperation(value = PathProxy.ProcedureUrls.DISCARD_PROCEDURE_STRUCTURE, notes = PathProxy.ProcedureUrls.DISCARD_PROCEDURE_STRUCTURE)
	public Response<ProcedureSheetStructureResponse> getProcedure(@PathParam("id") String id,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
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

	@POST
	@Path(value = PathProxy.ProcedureUrls.ADD_DIAGRAM)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.ProcedureUrls.ADD_DIAGRAM, notes = PathProxy.ProcedureUrls.ADD_DIAGRAM)
	public Response<ImageURLResponse> addDiagramMultipart(@FormDataParam("file") FormDataBodyPart file) {

		if (file == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ImageURLResponse imageURLResponse = procedureSheetService.addDiagrams(file);

		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(imageURLResponse);
		return response;
	}

	@Path(value = PathProxy.ProcedureUrls.DOWNLOAD_PROCEDURE_SHEET)
	@GET
	@ApiOperation(value = PathProxy.ProcedureUrls.DOWNLOAD_PROCEDURE_SHEET, notes = PathProxy.ProcedureUrls.DOWNLOAD_PROCEDURE_SHEET)
	public Response<String> downloadProcedureSheet(@PathParam("id") String id) {
		Response<String> response = new Response<String>();
		response.setData(procedureSheetService.downloadProcedureSheet(id));
		return response;
	}

}
