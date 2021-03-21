package com.dpdocter.webservices;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
@Path(PathProxy.Lab_PRINT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.Lab_PRINT_BASE_URL, description = "")
public class LabPrintAPI {

	private static Logger logger = LogManager.getLogger(LabPrintAPI.class.getName());
	@Autowired
	private LabPrintServices labprintservices;

	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_SETTING)
	@POST
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

	@Path(value = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING)
	@GET
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING, notes = PathProxy.LabPrintUrls.GET_Lab_PRINT_SETTING)
	public Response<LabPrintSetting> getLabPrintSetting(@PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintSetting> response = new Response<LabPrintSetting>();
		response.setData(labprintservices.getLabPrintSetting(locationId, hospitalId));

		return response;
	}

	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_HEADER)
	@POST
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

	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_PRINT_FOOTER)
	@POST
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

	@Path(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENT)
	@GET
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENT, notes = PathProxy.LabPrintUrls.GET_LAB_DOCUMENT)
	public Response<LabPrintDocument> getLabPrintDocument(@PathParam("documentId") String documentId) {
		if (DPDoctorUtils.anyStringEmpty(documentId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintDocument> response = new Response<LabPrintDocument>();
		response.setData(labprintservices.getLabPrintDocument(documentId));

		return response;
	}

	@Path(value = PathProxy.LabPrintUrls.ADD_EDIT_LAB_DOCUMENT)
	@POST
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

	@Path(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENTS)
	@GET
	@ApiOperation(value = PathProxy.LabPrintUrls.GET_LAB_DOCUMENTS, notes = PathProxy.LabPrintUrls.GET_LAB_DOCUMENTS)
	public Response<LabPrintDocument> getLabPrintDocuments(@QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("searchTerm") String searchTerm,
			@DefaultValue("0") @QueryParam("to") Long to, @DefaultValue("0") @QueryParam("from") Long from,
			@QueryParam("isParent") boolean isParent, @QueryParam("isdiscarded") boolean isdiscarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<LabPrintDocument> response = new Response<LabPrintDocument>();
		response.setDataList(labprintservices.getLabPrintDocuments(page, size, locationId, doctorId, hospitalId,
				searchTerm, isParent, from, to, isdiscarded));

		return response;
	}

	@Path(value = PathProxy.LabPrintUrls.DELETE_LAB_DOCUMENT)
	@DELETE
	@ApiOperation(value = PathProxy.LabPrintUrls.DELETE_LAB_DOCUMENT, notes = PathProxy.LabPrintUrls.DELETE_LAB_DOCUMENT)
	public Response<Boolean> deleteLabPrintDocument(@PathParam("documentId") String documentId,
			@QueryParam("isdiscarded") boolean isdiscarded) {
		if (DPDoctorUtils.anyStringEmpty(documentId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(labprintservices.deleteLabPrintDocument(documentId, isdiscarded));

		return response;
	}

}
