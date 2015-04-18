package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Tags;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.PrescriptionDeleteRequest;
import com.dpdocter.request.PrescriptionGetRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.request.TemplateDeleteRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionGetResponse;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.services.PrescriptionServices;

import common.util.web.Response;

@Component
@Path(PathProxy.PRESCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrescriptionApi {
	@Autowired
	private PrescriptionServices prescriptionServices;

	@Path(value = PathProxy.PrescriptionUrls.ADD_DRUG)
	@POST
	public Response<DrugAddEditResponse> addDrug(DrugAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.addDrug(request);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG)
	@POST
	public Response<DrugAddEditResponse> editDrug(DrugAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.editDrug(request);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG)
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

		Boolean drugDeleteResponse = prescriptionServices.deleteDrug(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(drugDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE)
	@POST
	public Response<TemplateAddEditResponse> addTemplate(TemplateAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		TemplateAddEditResponse templateAddEditResponse = prescriptionServices.addTemplate(request);
		Response<TemplateAddEditResponse> response = new Response<TemplateAddEditResponse>();
		response.setData(templateAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.EDIT_TEMPLATE)
	@POST
	public Response<TemplateAddEditResponse> editTemplate(TemplateAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		TemplateAddEditResponse templateAddEditResponse = prescriptionServices.editTemplate(request);
		Response<TemplateAddEditResponse> response = new Response<TemplateAddEditResponse>();
		response.setData(templateAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.DELETE_TEMPLATE)
	@POST
	public Response<Boolean> deleteTemplate(TemplateDeleteRequest request) {
		if (request != null) {
			if (request.getId() == null || request.getId().isEmpty() || request.getDoctorId() == null || request.getDoctorId().isEmpty()
					|| request.getHospitalId() == null || request.getHospitalId().isEmpty() || request.getLocationId() == null
					|| request.getLocationId().isEmpty()) {
				throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			}
		} else {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		Boolean templateDeleteResponse = prescriptionServices.deleteTemplate(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(templateDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION)
	@POST
	public Response<PrescriptionAddEditResponse> addPrescription(PrescriptionAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		PrescriptionAddEditResponse prescriptionAddEditResponse = prescriptionServices.addPrescription(request);
		Response<PrescriptionAddEditResponse> response = new Response<PrescriptionAddEditResponse>();
		response.setData(prescriptionAddEditResponse);
		return response;

	}

	@Path(value = PathProxy.PrescriptionUrls.EDIT_PRESCRIPTION)
	@POST
	public Response<PrescriptionAddEditResponse> editPrescription(PrescriptionAddEditRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		PrescriptionAddEditResponse prescriptionAddEditResponse = prescriptionServices.editPrescription(request);
		Response<PrescriptionAddEditResponse> response = new Response<PrescriptionAddEditResponse>();
		response.setData(prescriptionAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION)
	@POST
	public Response<Boolean> deletePrescription(PrescriptionDeleteRequest request) {
		if (request != null) {
			if (request.getId() == null || request.getId().isEmpty() || request.getDoctorId() == null || request.getDoctorId().isEmpty()
					|| request.getHospitalId() == null || request.getHospitalId().isEmpty() || request.getLocationId() == null
					|| request.getLocationId().isEmpty() || request.getPatientId() == null || request.getPatientId().isEmpty()) {
				throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id, Hospital Id, Location Id, Patient Id Cannot Be Empty");
			}
		} else {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		Boolean prescriptionDeleteResponse = prescriptionServices.deletePrescription(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION)
	@POST
	public Response<List<PrescriptionGetResponse>> getPrescription(PrescriptionGetRequest request) {
		if (request != null) {
			if (request.getDoctorId() == null || request.getDoctorId().isEmpty() || request.getHospitalId() == null || request.getHospitalId().isEmpty()
					|| request.getLocationId() == null || request.getLocationId().isEmpty() || request.getPatientId() == null
					|| request.getPatientId().isEmpty()) {
				throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id, Hospital Id, Location Id, Patient Id Cannot Be Empty");
			}
		} else {
			throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
		}
		List<PrescriptionGetResponse> prescriptionGetResponse = prescriptionServices.getPrescription(request);
		Response<List<PrescriptionGetResponse>> response = new Response<List<PrescriptionGetResponse>>();
		response.setData(prescriptionGetResponse);
		return response;
	}
	
	@Path(value = PathProxy.PrescriptionUrls.GET_DRUG_ID)
	@GET
	public Response<DrugAddEditResponse> getDrugDetails(@PathParam("drugId") String drugId) {
		if(drugId == null){
			throw new BusinessException(ServiceError.InvalidInput, "drugId Is NULL");
		}
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.getDrugById(drugId);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}
	
}
