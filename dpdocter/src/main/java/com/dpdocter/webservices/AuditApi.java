package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.AuditTrailData;
import com.dpdocter.services.AuditService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.AUDIT_TRAIL_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.AUDIT_TRAIL_BASE_URL, description = "Endpoint for appointment")
public class AuditApi {

	private static Logger logger = Logger.getLogger(AuditApi.class.getName());

	@Autowired
	private AuditService auditService;

	@Path(value = PathProxy.AuditTrailUrls.GET_AUDIT_TRAIL_REPORT)
	@GET
	@ApiOperation(value = PathProxy.AuditTrailUrls.GET_AUDIT_TRAIL_REPORT, notes = PathProxy.AuditTrailUrls.GET_AUDIT_TRAIL_REPORT)
	public Response<AuditTrailData> getAuditTrailAppointmentData(
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "from") String from, @QueryParam(value = "to") String to,
			@QueryParam(value = "page") int page, @QueryParam(value = "size") int size) {
		List<AuditTrailData> countries = auditService.getAuditTrailAppointmentData(locationId, hospitalId,
				from, to, page, size);
		Response<AuditTrailData> response = new Response<AuditTrailData>();
		response.setDataList(countries);
		return response;
	}

}
