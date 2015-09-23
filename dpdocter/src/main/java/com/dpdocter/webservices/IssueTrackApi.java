package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.IssueTrack;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.IssueTrackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.ISSUE_TRACK_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueTrackApi {

    private static Logger logger = Logger.getLogger(IssueTrackApi.class.getName());

    @Autowired
    private IssueTrackService issueTrackService;

    @Path(value = PathProxy.IssueTrackUrls.RAISE_ISSUE)
    @POST
    public Response<IssueTrack> addEditIssue(IssueTrack request) {

	if (request == null) {
	    logger.warn("Request cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");
	}
	IssueTrack issueTrack = issueTrackService.addEditIssue(request);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setData(issueTrack);

	return response;
    }

    @Path(value = PathProxy.IssueTrackUrls.DELETE_ISSUE)
    @DELETE
    public Response<Boolean> deleteIssue(@PathParam(value = "issueId") String issueId, @PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {

	if (DPDoctorUtils.anyStringEmpty(issueId, doctorId, locationId, hospitalId)) {
	    logger.warn("IssueId or DoctorId or LocationId or HospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "IssueId or DoctorId or LocationId or HospitalId cannot be null");
	}
	Boolean issueTrack = issueTrackService.deleteIssue(issueId,doctorId,locationId,hospitalId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(issueTrack);

	return response;
    }

    @GET
    public Response<IssueTrack> getIssues(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @QueryParam(value = "updatedTime") String updatedTime, @QueryParam(value = "discarded") Boolean discarded,
	    @MatrixParam("scope") List<String> scope) {

	List<IssueTrack> issueTrack = issueTrackService.getIssues(page, size, doctorId, locationId, hospitalId, updatedTime, discarded != null ? discarded: true, scope);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setDataList(issueTrack);

	return response;
    }

    @Path(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC)
    @GET
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
