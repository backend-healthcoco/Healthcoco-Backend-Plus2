package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
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

import com.dpdocter.services.VisitFieldWiseService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.VISIT_FIELDWISE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value=PathProxy.VISIT_FIELDWISE_BASE_URL,description="Endpoint")
public class VisitFieldWiseAPI {
	
	private static Logger logger = Logger.getLogger(VisitFieldWiseAPI.class.getName());

	@Autowired
	private VisitFieldWiseService visitfieldWiseService;
	
	@Path(value = PathProxy.VisitFieldWiseUrls.GET_DATA)
	@GET
	@ApiOperation(value = PathProxy.VisitFieldWiseUrls.GET_DATA, notes = PathProxy.VisitFieldWiseUrls.GET_DATA)
	public Response<Object> getData(@PathParam(value = "patientId") String patientId,
			@QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId){
		
		Response<Object> response = visitfieldWiseService.getComplaintData(doctorId,locationId,hospitalId,patientId);

		return response;
	}
}
