package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.services.HistoryServices;
import common.util.web.Response;

@Component
@Path(PathProxy.HISTORY_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoryApi {
	@Autowired
	private HistoryServices historyServices;

	@Path(value = PathProxy.HistoryUrls.ADD_DISEASE)
	@POST
	public Response<DiseaseAddEditResponse> addDisease(List<DiseaseAddEditRequest> request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		List<DiseaseAddEditResponse> diseases = historyServices.addDiseases(request);
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setDataList(diseases);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.EDIT_DISEASE)
	@POST
	public Response<DiseaseAddEditResponse> addDisease(DiseaseAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DiseaseAddEditResponse diseases = historyServices.editDiseases(request);
		Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
		response.setData(diseases);
		return response;
	}

	@Path(value = PathProxy.HistoryUrls.DELETE_DISEASE)
	@GET
	public Response<Boolean> deleteDisease(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
		if (StringUtils.isEmpty(diseaseId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean diseaseDeleteResponse = historyServices.deleteDisease(diseaseId, doctorId, hospitalId, locationId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(diseaseDeleteResponse);
		return response;
	}
}
