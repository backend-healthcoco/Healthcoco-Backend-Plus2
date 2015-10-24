package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AddMultipleDataRequest;
import com.dpdocter.response.PatientVisitResponse;
import com.dpdocter.services.PatientVisitService;

import common.util.web.Response;

@Component
@Path(PathProxy.PATIENT_VISIT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientVisitApi {
  
	private static Logger logger = Logger.getLogger(PatientVisitApi.class.getName());
	
	@Autowired
    private PatientVisitService patientVisitService;
    
     @Path(value = PathProxy.PatientVisitUrls.ADD_MULTIPLE_DATA)
     @POST
     public Response<PatientVisitResponse> addMultipleData(AddMultipleDataRequest request) {
    
    	if (request == null) {
    	    logger.warn("Request Sent Is NULL");
    	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
    	}
     PatientVisitResponse patienVisitResponse = patientVisitService.addMultipleData(request);
     Response<PatientVisitResponse> response = new   Response<PatientVisitResponse>();
     response.setData(patienVisitResponse);
     return response;
     }
     
     @Path(value = PathProxy.PatientVisitUrls.GET_VISITS)
     @GET
     public Response<PatientVisitResponse> getVisit(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
    		 @PathParam(value = "patientId") String patientId, @QueryParam(value = "page") int page, @QueryParam(value = "size") int size) {
    	
      if (StringUtils.isEmpty(patientId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
    	   logger.warn("Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	   throw new BusinessException(ServiceError.InvalidInput, "Patient Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
      }
      List<PatientVisitResponse> patienVisitResponse = patientVisitService.getVisit(doctorId, locationId, hospitalId, patientId, page, size);
     Response<PatientVisitResponse> response = new   Response<PatientVisitResponse>();
     response.setDataList(patienVisitResponse);
     return response;
     }
     
 }
