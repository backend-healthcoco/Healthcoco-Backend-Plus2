package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.services.PaediatricService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.VaccineResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.PAEDIATRIC_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PAEDIATRIC_BASE_URL, description = "Endpoint for paediatric")
public class PaediatricAPI {

	private static Logger logger = Logger.getLogger(ProcedureAPI.class.getName());

	@Autowired
	private PaediatricService paediatricService;
	
	@Path(value = PathProxy.PaediatricUrls.ADD_EDIT_VACCINE)
	@POST
	@ApiOperation(value = PathProxy.PaediatricUrls.ADD_EDIT_VACCINE, notes = PathProxy.PaediatricUrls.ADD_EDIT_VACCINE)
	public Response<VaccineResponse> addProcedure(VaccineRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		VaccineResponse vaccineResponse = paediatricService.addEditVaccine(request);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setData(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_VACCINE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINE_BY_ID, notes = PathProxy.PaediatricUrls.GET_VACCINE_BY_ID)
	public Response<VaccineResponse> getProcedure(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		VaccineResponse vaccineResponse = paediatricService.getVaccineById(id);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setData(vaccineResponse);
		return response;
	}

	
	
}
