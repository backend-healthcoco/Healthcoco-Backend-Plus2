package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.PatientAnalyticResponse;
import com.dpdocter.services.AnalyticsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.ANALYTICS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ANALYTICS_BASE_URL, description = "")
public class AnalyticsAPI {

	private static Logger logger = Logger.getLogger(AnalyticsAPI.class.getName());

	@Autowired
	private AnalyticsService analyticsService;

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA)
	public Response<PatientAnalyticResponse> getPatientAnalyticnData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			 @QueryParam("fromDate") String fromDate,
			 @QueryParam("toDate") String toDate, @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<PatientAnalyticResponse> patientAnalyticResponse = analyticsService.getPatientCount(doctorId, locationId,
				hospitalId, fromDate, toDate, queryType, searchType, searchTerm);

		Response<PatientAnalyticResponse> response = new Response<PatientAnalyticResponse>();
		response.setDataList(patientAnalyticResponse);
		return response;
	}
	
	@Path(value = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS, notes = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS)
	public Response<Object> getMostPrescribedPrescriptionItems(@PathParam("type") String type, 
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId,
			 @QueryParam("fromDate") String fromDate,
			 @QueryParam("toDate") String toDate,
			@QueryParam("searchType") String searchType) {
		if (DPDoctorUtils.allStringsEmpty(type, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Type, locationId, hospitalId should not be empty");
		}
		if (type.equalsIgnoreCase(PrescriptionItems.DRUGS.getItem())) {
			if (DPDoctorUtils.anyStringEmpty(doctorId)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
		} 
		List<?> objects = analyticsService.getMostPrescribedPrescriptionItems(type, doctorId, locationId,
				hospitalId, fromDate, toDate, searchType);

		Response<Object> response = new Response<Object>();
		response.setDataList(objects);
		return response;
	}
}
