package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.IssueTrack;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.IssueTrackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.ISSUE_TRACK_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ISSUE_TRACK_BASE_URL, description = "Endpoint for issue track")
public class IssueTrackApi {

    private static Logger logger = LogManager.getLogger(IssueTrackApi.class.getName());

    @Autowired
    private IssueTrackService issueTrackService;

    
    @PostMapping(value = PathProxy.IssueTrackUrls.RAISE_ISSUE)
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

    
    @DeleteMapping(value = PathProxy.IssueTrackUrls.DELETE_ISSUE)
    @ApiOperation(value = PathProxy.IssueTrackUrls.DELETE_ISSUE, notes = PathProxy.IssueTrackUrls.DELETE_ISSUE)
    public Response<IssueTrack> deleteIssue(@PathVariable(value = "issueId") String issueId, @PathVariable(value = "doctorId") String doctorId,
	    @PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
	      @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(issueId, doctorId, locationId, hospitalId)) {
	    logger.warn("IssueId or DoctorId or LocationId or HospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "IssueId or DoctorId or LocationId or HospitalId cannot be null");
	}
	IssueTrack issueTrack = issueTrackService.deleteIssue(issueId, doctorId, locationId, hospitalId, discarded);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setData(issueTrack);

	return response;
    }

    @GetMapping
    @ApiOperation(value = "GET_ISSUE", notes = "GET_ISSUE")
    public Response<IssueTrack> getIssues(@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
	    @RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,   @RequestParam(value = "discarded") Boolean discarded,
	    @MatrixParam("scope") List<String> scope) {

	List<IssueTrack> issueTrack = issueTrackService.getIssues(page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded != null ? discarded : true, scope);
	Response<IssueTrack> response = new Response<IssueTrack>();
	response.setDataList(issueTrack);

	return response;
    }

   
    @GetMapping (value = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC)
    @ApiOperation(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC, notes = PathProxy.IssueTrackUrls.UPDATE_STATUS_DOCTOR_SPECIFIC)
    public Response<Boolean> updateIssueStatus(@PathVariable("issueId") String issueId, @PathVariable("status") String status,
	    @PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
	    @PathVariable(value = "hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(issueId, status, doctorId, locationId, hospitalId)) {
	    logger.warn("IssueId or Status or DoctorId or LocationId or HospitalId cannot be null");
	    throw new BusinessException(ServiceError.InvalidInput, "IssueId or Status or DoctorId or LocationId or HospitalId cannot be null");
	}
	Boolean updated = issueTrackService.updateIssueStatus(issueId, status, doctorId, locationId, hospitalId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(updated);

	return response;
    }

    
    @GetMapping(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_ADMIN)
    @ApiOperation(value = PathProxy.IssueTrackUrls.UPDATE_STATUS_ADMIN, notes = PathProxy.IssueTrackUrls.UPDATE_STATUS_ADMIN)
    public Response<Boolean> updateIssueStatus(@PathVariable("issueId") String issueId, @PathVariable("status") String status) {
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
