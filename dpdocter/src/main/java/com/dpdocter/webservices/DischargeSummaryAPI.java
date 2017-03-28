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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DischargeSummary;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.DischargeSummaryService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DISCHARGE_SUMMARY_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DISCHARGE_SUMMARY_BASE_URL)
public class DischargeSummaryAPI {
	
	private Logger logger = Logger.getLogger(DischargeSummaryAPI.class);
	
	@Autowired
	DischargeSummaryService dischargeSummaryService;
	
	@POST
	@ApiOperation(value = "API for adding discharge summary")
	public Response<DischargeSummary> addEditDischargeSummary(DischargeSummary request)
	{
		Response<DischargeSummary> response = null;
		DischargeSummary dischargeSummary = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			//dischargeSummary = new DischargeSummary();
			dischargeSummary = dischargeSummaryService.addEditDischargeSummary(request);
			if (dischargeSummary != null) {
				response = new Response<DischargeSummary>();
				response.setData(dischargeSummary);
			} 
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}
	
	/*String doctorId, String locationId, String hospitalId, String patientId,
	int page, int size, String updatedTime*/
	
	
	@GET
	@ApiOperation(value = "API for getting discharge summaries")
	public Response<DischargeSummary> getDischargeSummary(@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId, @QueryParam(value = "patientId") String patientId, @QueryParam(value = "page") int page,
		    @QueryParam(value = "size") int size, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime)
	{
		Response<DischargeSummary> response = null;
		List<DischargeSummary> dischargeSummaries = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(patientId, doctorId)) {
				throw new BusinessException(ServiceError.InvalidInput, "Doctor or patient id is null");
			}
			dischargeSummaries = dischargeSummaryService.getDischargeSummary(doctorId, locationId, hospitalId,
					patientId, page, size, updatedTime);
			response = new Response<>();
			response.setDataList(dischargeSummaries);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
		
	}
	
	

}
