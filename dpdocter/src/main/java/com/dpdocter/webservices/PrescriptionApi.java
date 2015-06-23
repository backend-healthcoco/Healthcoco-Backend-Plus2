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

import com.dpdocter.beans.Prescription;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateGetResponse;
import com.dpdocter.services.PrescriptionServices;
import common.util.web.DPDoctorUtils;
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
	@GET
	public Response<Boolean> deleteDrug(@PathParam(value = "drugId") String drugId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
		if (StringUtils.isEmpty(drugId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean drugDeleteResponse = prescriptionServices.deleteDrug(drugId, doctorId, hospitalId, locationId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(drugDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.DELETE_GLOBAL_DRUG)
	@GET
	public Response<Boolean> deleteDrug(@PathParam(value = "drugId") String drugId) {
		if (StringUtils.isEmpty(drugId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id Cannot Be Empty");
		}
		Boolean drugDeleteResponse = prescriptionServices.deleteDrug(drugId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(drugDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_DRUG_ID)
	@GET
	public Response<DrugAddEditResponse> getDrugDetails(@PathParam("drugId") String drugId) {
		if (drugId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "drugId Is NULL");
		}
		DrugAddEditResponse drugAddEditResponse = prescriptionServices.getDrugById(drugId);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setData(drugAddEditResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_DRUGS_DOCTOR_SPECIFIC)
	@GET
	public Response<DrugAddEditResponse> getAllDrugs(@PathParam("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		return getDrugs(doctorId, null, null, null);
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_DRUGS_DOCTOR_SPECIFIC_CT)
	@GET
	public Response<DrugAddEditResponse> getAllDrugs(@PathParam("doctorId") String doctorId, @PathParam("createdTime") String createdTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, createdTime)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Created Time Cannot Be Empty");
		}
		return getDrugs(doctorId, null, null, createdTime);
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_DRUGS_ALL_FIELDS)
	@GET
	public Response<DrugAddEditResponse> getAllDrugs(@PathParam("doctorId") String doctorId, @PathParam("hospitalId") String hospitalId,
			@PathParam("locationId") String locationId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, hospitalId, locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		return getDrugs(doctorId, hospitalId, locationId, null);
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_DRUGS_ALL_FIELDS_CT)
	@GET
	public Response<DrugAddEditResponse> getAllDrugs(@PathParam("doctorId") String doctorId, @PathParam("hospitalId") String hospitalId,
			@PathParam("locationId") String locationId, @PathParam("createdTime") String createdTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, hospitalId, locationId, createdTime)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Hospital Id, Location Id, Created Time Cannot Be Empty");
		}
		return getDrugs(doctorId, hospitalId, locationId, createdTime);
	}

	private Response<DrugAddEditResponse> getDrugs(String doctorId, String hospitalId, String locationId, String createdTime) {
		List<DrugAddEditResponse> drugs = prescriptionServices.getDrugs(doctorId, hospitalId, locationId, createdTime);
		Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
		response.setDataList(drugs);
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
	@GET
	public Response<Boolean> deleteTemplate(@PathParam(value = "templateId") String templateId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
		if (StringUtils.isEmpty(templateId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean templateDeleteResponse = prescriptionServices.deleteTemplate(templateId, doctorId, hospitalId, locationId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(templateDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_TEMPLATE_ID)
	@GET
	public Response<TemplateGetResponse> getTemplate(@PathParam(value = "templateId") String templateId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId) {
		if (DPDoctorUtils.anyStringEmpty(templateId, doctorId, hospitalId, locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		TemplateGetResponse templateGetResponse = prescriptionServices.getTemplate(templateId, doctorId, hospitalId, locationId);
		Response<TemplateGetResponse> response = new Response<TemplateGetResponse>();
		response.setData(templateGetResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_DOCTOR_SPECIFIC)
	@GET
	public Response<TemplateGetResponse> getAllTemplates(@PathParam(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		return getTemplates(doctorId, null, null, null);
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_DOCTOR_SPECIFIC_CT)
	@GET
	public Response<TemplateGetResponse> getAllTemplates(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "createdTime") String createdTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Created Time Cannot Be Empty");
		}
		return getTemplates(doctorId, null, null, createdTime);
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_ALL_FIELDS)
	@GET
	public Response<TemplateGetResponse> getAllTemplates(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "locationId") String locationId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		return getTemplates(doctorId, hospitalId, locationId, null);
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_ALL_FIELDS_CT)
	@GET
	public Response<TemplateGetResponse> getAllTemplates(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "createdTime") String createdTime) {
		if (DPDoctorUtils.anyStringEmpty(doctorId, hospitalId, locationId, createdTime)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Hospital Id, Location Id, Created Time Cannot Be Empty");
		}
		return getTemplates(doctorId, hospitalId, locationId, createdTime);
	}

	private Response<TemplateGetResponse> getTemplates(String doctorId, String hospitalId, String locationId, String createdTime) {
		List<TemplateGetResponse> templates = prescriptionServices.getTemplates(doctorId, hospitalId, locationId, createdTime);
		Response<TemplateGetResponse> response = new Response<TemplateGetResponse>();
		response.setDataList(templates);
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
	@GET
	public Response<Boolean> deletePrescription(@PathParam(value = "prescriptionId") String prescriptionId, @PathParam(value = "doctorId") String doctorId,
			@PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "patientId") String patientId) {
		if (StringUtils.isEmpty(prescriptionId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Boolean prescriptionDeleteResponse = prescriptionServices.deletePrescription(prescriptionId, doctorId, hospitalId, locationId, patientId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionDeleteResponse);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION)
	@GET
	public Response<Prescription> getPrescription(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "patientId") String patientId,
			@PathParam(value = "{isOTPVarified}") boolean isOTPVarified) {
		if (StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = prescriptionServices.getPrescriptions(doctorId, hospitalId, locationId, patientId, null, isOTPVarified);
		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_CREATED_TIME)
	@GET
	public Response<Prescription> getPrescription(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "patientId") String patientId,
			@PathParam(value = "{isOTPVarified}") boolean isOTPVarified, @PathParam(value = "createdTime") String createdTime) {
		if (StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = prescriptionServices.getPrescriptions(doctorId, hospitalId, locationId, patientId, createdTime, isOTPVarified);
		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}

	@Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_COUNT)
	@GET
	public Response<Integer> getPrescriptionCount(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "patientId") String patientId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
		Integer prescriptionCount = prescriptionServices.getPrescriptionCount(doctorId, patientId, locationId, hospitalId);
		Response<Integer> response = new Response<Integer>();
		response.setData(prescriptionCount);
		return response;
	}

}
