package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.elasticsearch.index.analysis.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.PatientAnalyticResponse;
import com.dpdocter.services.AnalyticService;
import com.squareup.okhttp.internal.spdy.ErrorCode;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.ANALYTIC_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ANALYTIC_BASE_URL, description = "")
public class AnalyticAPi {

	private static Logger logger = Logger.getLogger(AnalyticAPi.class.getName());

	@Autowired
	private AnalyticService analyticService;

	@Path(value = PathProxy.AnalyticUrls.GET_PATIENT_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticUrls.GET_PATIENT_ANALYTICS_DATA, notes = PathProxy.AnalyticUrls.GET_PATIENT_ANALYTICS_DATA)
	public Response<PatientAnalyticResponse> getPatientAnalyticnData(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("fromDate") String fromDate,
			@DefaultValue("0") @QueryParam("toDate") String toDate, @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<PatientAnalyticResponse> patientAnalyticResponse = analyticService.getPatientCount(doctorId, locationId,
				hospitalId, fromDate, toDate, queryType, searchType, searchTerm);

		Response<PatientAnalyticResponse> response = new Response<PatientAnalyticResponse>();
		response.setDataList(patientAnalyticResponse);
		return response;
	}

}
