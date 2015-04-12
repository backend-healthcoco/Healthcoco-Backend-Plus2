package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.services.PrescriptionServices;
import common.util.web.Response;

@Component
@Path(PathProxy.DRUG_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrescriptionApi {
	@Autowired
	private PrescriptionServices drugServices;

	@Path(value = PathProxy.DrugUrls.ADD_DRUG)
	@POST
	public Response<DrugAddEditResponse> addDrug(DrugAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugAddEditResponse drugAddEditResponse = drugServices.addDrug(request);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.DrugUrls.EDIT_DRUG)
	@POST
	public Response<DrugAddEditResponse> editDrug(DrugAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugAddEditResponse drugAddEditResponse = drugServices.editDrug(request);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.DrugUrls.DELETE_DRUG)
	@POST
	public Response<Boolean> deleteDrug(DrugDeleteRequest request) {
		if (request != null) {
			if (request.getId() == null || request.getId().isEmpty() || request.getDoctorId() == null || request.getDoctorId().isEmpty()
					|| request.getHospitalId() == null || request.getHospitalId().isEmpty() || request.getLocationId() == null
					|| request.getLocationId().isEmpty()) {
				throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			}
		} else {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}

		Boolean drugDeleteResponse = drugServices.deleteDrug(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(drugDeleteResponse);
		return response;
	}

}
