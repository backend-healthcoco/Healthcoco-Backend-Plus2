package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.Location;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorHospitalDentalImagingAssociationRepository;
import com.dpdocter.request.DentalimagingReportsUploadRequest;
import com.dpdocter.request.RecordUploadRequest;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationResponse;
import com.dpdocter.response.ServiceLocationResponse;
import com.dpdocter.services.DentalImagingService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Component
@Path(PathProxy.DENTAL_IMAGING_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_IMAGING_URL, description = "Endpoint for dental imaging")
public class DentalImagingAPI {
	
	private static Logger logger = Logger.getLogger(DentalImagingAPI.class.getName());

	
	@Autowired
	DentalImagingService dentalImagingService;
	

	@Path(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST)
	@POST
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST, notes = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_REQUEST)
	public Response<DentalImaging> addEditDentalRequest(DentalImagingRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalImaging> response = new Response<DentalImaging>();
		response.setData(dentalImagingService.addEditDentalImagingRequest(request));
		return response;
	}
	
	
	@Path(value = PathProxy.DentalImagingUrl.GET_REQUESTS)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_REQUESTS, notes = PathProxy.DentalImagingUrl.GET_REQUESTS)
	public Response<DentalImagingResponse> getPickupRequests(@QueryParam("locationId") String locationId,@QueryParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId, @DefaultValue("0") @QueryParam("from") Long from,
			@QueryParam("to") Long to, @QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page,@QueryParam("type") String type) {

		Response<DentalImagingResponse> response = new Response<DentalImagingResponse>();
		response.setDataList(dentalImagingService.getRequests(locationId, hospitalId, doctorId, from, to, searchTerm, size, page , type));
		return response;
	}
	
	
	@Path(value = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION, notes = PathProxy.DentalImagingUrl.GET_SERVICE_LOCATION)
	public Response<DentalImagingLocationResponse> getServiceLocations(@MatrixParam(value = "dentalImagingServiceId") List<String> dentalImagingServiceId,
			@QueryParam("doctorId") String doctorId,@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page) {

		Response<DentalImagingLocationResponse> response = new Response<DentalImagingLocationResponse>();
		response.setDataList(dentalImagingService.getServiceLocations(dentalImagingServiceId, doctorId, searchTerm, size, page));
		return response;
	}
	
	@Path(value = PathProxy.DentalImagingUrl.GET_SERVICES)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_SERVICES, notes = PathProxy.DentalImagingUrl.GET_SERVICES)
	public Response<DentalDiagnosticService> getPickupRequests(@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page , 	@QueryParam("type") String type) {

		Response<DentalDiagnosticService> response = new Response<DentalDiagnosticService>();
		response.setDataList(dentalImagingService.getServices(searchTerm, type, page, size));
		return response;
	}
	
	@Path(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION)
	@POST
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION, notes = PathProxy.DentalImagingUrl.ADD_EDIT_DENTAL_IMAGING_LOCATION_ASSOCIATION)
	public Response<Boolean> addEditDentalImagingLocationServiceAssociation(List<DentalImagingLocationServiceAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalImagingService.addEditDentalImagingLocationServiceAssociation(request));
		return response;
	}
	
	@Path(value = PathProxy.DentalImagingUrl.GET_LOCATION_ASSOCIATED_SERVICES)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_LOCATION_ASSOCIATED_SERVICES, notes = PathProxy.DentalImagingUrl.GET_LOCATION_ASSOCIATED_SERVICES)
	public Response<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page , 	@QueryParam("type") String type , @QueryParam("discarded") Boolean discarded) {
		Response<DentalImagingLocationServiceAssociationLookupResponse> response = new Response<DentalImagingLocationServiceAssociationLookupResponse>();
		response.setDataList(dentalImagingService.getLocationAssociatedServices(locationId, hospitalId, searchTerm, type, page, size, discarded));
		return response;
	}
	
	@Path(value = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST, notes = PathProxy.DentalImagingUrl.GET_HOSPITAL_LIST)
	public Response<Hospital> getLocationAssociatedServices(@QueryParam("doctorId") String doctorId, @QueryParam("hospitalId") String hospitalId) {
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(dentalImagingService.getHospitalList(doctorId, hospitalId));
		return response;
	}
	
	
	@POST
	@Path(value = PathProxy.DentalImagingUrl.ADD_RECORDS)
	@ApiOperation(value = PathProxy.DentalImagingUrl.ADD_RECORDS, notes = PathProxy.DentalImagingUrl.ADD_RECORDS)
	public Response<DentalImagingReports> addRecordsBase64(DentalimagingReportsUploadRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		DentalImagingReports dentalImagingReports = dentalImagingService.addDentalImagingReportBase64(request.getFileDetails(),
				request.getRequest());

		Response<DentalImagingReports> response = new Response<DentalImagingReports>();
		response.setData(dentalImagingReports);
		return response;
	}
	
	
	
	@Path(value = PathProxy.DentalImagingUrl.DISCARD_REQUEST)
	@DELETE
	@ApiOperation(value = PathProxy.DentalImagingUrl.DISCARD_REQUEST, notes = PathProxy.DentalImagingUrl.DISCARD_REQUEST)
	public Response<DentalImaging> discardRequest(@QueryParam("id") String id,
			@QueryParam("discarded") boolean discarded) {

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
	
	@Path(value = PathProxy.DentalImagingUrl.DISCARD_RECORD)
	@DELETE
	@ApiOperation(value = PathProxy.DentalImagingUrl.DISCARD_RECORD, notes = PathProxy.DentalImagingUrl.DISCARD_RECORD)
	public Response<DentalImagingReports> discardReports(@QueryParam("id") String id,
			@QueryParam("discarded") boolean discarded) {

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
	
	@Path(value = PathProxy.DentalImagingUrl.GET_ASSOCIATED_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.DentalImagingUrl.GET_ASSOCIATED_DOCTORS, notes = PathProxy.DentalImagingUrl.GET_ASSOCIATED_DOCTORS)
	public Response<DoctorHospitalDentalImagingAssociationResponse> getLocationAssociatedServices(@QueryParam("hospitalId") String hospitalId,@QueryParam("searchTerm") String searchTerm, @QueryParam("size") int size,
			@QueryParam("page") int page) {
		Response<DoctorHospitalDentalImagingAssociationResponse> response = new Response<DoctorHospitalDentalImagingAssociationResponse>();
		response.setDataList(dentalImagingService.getHospitalAssociatedDoctor(hospitalId, searchTerm, size, page));
		return response;
	}
	
}
