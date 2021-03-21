package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
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

import com.dpdocter.beans.IssueTrack;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.IssueTrackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.ISSUE_TRACK_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ISSUE_TRACK_BASE_URL, description = "Endpoint for issue track")
public class IssueTrackApi {

    private static Logger logger = LogManager.getLogger(IssueTrackApi.class.getName());

    @Autowired
    private IssueTrackService issueTrackService;

    @Path(value = PathProxy.IssueTrackUrls.RAISE_ISSUE)
    @POST
    @ApiOperation(value = PathProxy.IssueTrackUrls.RAISE_ISSUE, notes = PathProxy.IssueTrackUrls.RAISE_ISSUE)
    public Response<IssueTrack> addEditIssue(IssueTrack request) {

	if (request == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}else if(DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())){
		logger.warn("DoctorId, LocationId, HospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "DoctorId, LocationId, HospitalId cannot be null");
	}
	IssueTrack issueTrack = issueTrackService.addEditIssue(request);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setData(issueTrack);

	return response;
    }

    @Path(value = PathProxy.IssueTrackUrls.DELETE_ISSUE)
    @DELETE
    @ApiOperation(value = PathProxy.IssueTrackUrls.DELETE_ISSUE, notes = PathProxy.IssueTrackUrls.DELETE_ISSUE)
    public Response<IssueTrack> deleteIssue(@PathParam(value = "issueId") String issueId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(issueId, doctorId, locationId, hospitalId)) {
	    logger.warn("IssueId or DoctorId or LocationId or HospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "IssueId or DoctorId or LocationId or HospitalId cannot be null");
	}
	IssueTrack issueTrack = issueTrackService.deleteIssue(issueId, doctorId, locationId, hospitalId, discarded);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setData(issueTrack);

	return response;
    }

    @GET
    @ApiOperation(value = "GET_ISSUE", notes = "GET_ISSUE")
    public Response<IssueTrack> getIssues(@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
	    @MatrixParam("scope") List<String> scope) {

	List<IssueTrack> issueTrack = issueTrackService.getIssues(page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded != null ? discarded : true, scope);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setDataList(issueTrack);

	return response;
    }

    @Path(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC)
    @GET
    @ApiOperation(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC, notes = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC)
    public Response<Boolean> updateIssueStatus(@PathParam("issueId") String issueId, @PathParam("status") String status,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(issueId, status, doctorId, locationId, hospitalId)) {
	    logger.warn("IssueId or Status or DoctorId or LocationId or HospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "IssueId or Status or DoctorId or LocationId or HospitalId cannot be null");
	}
	Boolean updated = issueTrackService.updateIssueStatus(issueId, status, doctorId, locationId, hospitalId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(updated);

	return response;
    }

    @Path(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_ADMIN)
    @GET
    @ApiOperation(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_ADMIN, notes = PathProxy.IssueTrackUrls.UPDATE_STATUS_ADMIN)
    public Response<Boolean> updateIssueStatus(@PathParam("issueId") String issueId, @PathParam("status") String status) {
	if (DPDoctorUtils.anyStringEmpty(issueId, status)) {
	    logger.warn("IssueId or Status cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "IssueId or Status cannot be null");
	}
	Boolean updated = issueTrackService.updateIssueStatus(issueId, status);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(updated);

	return response;
    }
}
