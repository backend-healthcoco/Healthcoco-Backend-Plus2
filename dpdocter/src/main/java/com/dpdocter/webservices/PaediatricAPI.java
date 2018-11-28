package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.MultipleVaccineEditRequest;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.GroupedVaccineBrandAssociationResponse;
import com.dpdocter.response.MasterVaccineResponse;
import com.dpdocter.response.VaccineBrandAssociationResponse;
import com.dpdocter.response.VaccineResponse;
import com.dpdocter.services.PaediatricService;

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
	public Response<VaccineResponse> addVaccines(VaccineRequest request) {
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
	public Response<VaccineResponse> getVaccineById(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		VaccineResponse vaccineResponse = paediatricService.getVaccineById(id);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setData(vaccineResponse);
		return response;
	}
	
	@Path(value = PathProxy.PaediatricUrls.GET_VACCINES)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINES, notes = PathProxy.PaediatricUrls.GET_VACCINES)
	public Response<VaccineResponse> getVaccines(@QueryParam("patientId") String patientId, @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId ,@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId,doctorId,locationId,hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<VaccineResponse> vaccineResponse = paediatricService.getVaccineList(patientId, doctorId, locationId, hospitalId,updatedTime);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}
	
	@Path(value = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION, notes = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION)
	public Response<VaccineBrandAssociationResponse> getVaccineBrandAssociation(@QueryParam("vaccineId") String vaccineId, @QueryParam("vaccineBrandId") String vaccineBrandId) {
		/*if (DPDoctorUtils.allStringsEmpty(vaccineBrandId,vaccineId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}*/
		List<VaccineBrandAssociationResponse> vaccineResponse = paediatricService.getVaccineBrandAssociation(vaccineId, vaccineBrandId);
		Response<VaccineBrandAssociationResponse> response = new Response<VaccineBrandAssociationResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}
	
	@Path(value = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION, notes = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION)
	public Response<GroupedVaccineBrandAssociationResponse> getMultipleVaccineBrandAssociation(@MatrixParam("vaccineId") List<String> vaccineIds, @QueryParam("vaccineBrandId") String vaccineBrandId) {
		/*if (DPDoctorUtils.allStringsEmpty(vaccineBrandId,vaccineId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}*/
		List<GroupedVaccineBrandAssociationResponse> vaccineResponse = paediatricService.getGroupedVaccineBrandAssociation(vaccineIds);
		Response<GroupedVaccineBrandAssociationResponse> response = new Response<GroupedVaccineBrandAssociationResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE)
	@POST
	@ApiOperation(value = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE, notes = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE)
	public Response<Boolean> addEditVaccinesMultiple(List<VaccineRequest> requests) {
		if (requests == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
	//	VaccineResponse vaccineResponse = paediatricService.addEditVaccine(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(paediatricService.addEditMultipleVaccine(requests));
		return response;
	}
	
	@Path(value = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE_STATUS)
	@POST
	@ApiOperation(value = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE_STATUS, notes = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE_STATUS)
	public Response<Boolean> addEditVaccinesMultipleStatus(MultipleVaccineEditRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
	//	VaccineResponse vaccineResponse = paediatricService.addEditVaccine(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(paediatricService.addEditMultipleVaccineStatus(request));
		return response;
	}
	
	@Path(value = PathProxy.PaediatricUrls.GET_MASTER_VACCINES)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_MASTER_VACCINES, notes = PathProxy.PaediatricUrls.GET_MASTER_VACCINES)
	public Response<MasterVaccineResponse> getVaccines(@QueryParam("searchTerm") String searchTerm, @QueryParam("isChartVaccine") Boolean isChartVaccine, @QueryParam("page") int  page, @QueryParam("size") int size) {
		List<MasterVaccineResponse> vaccineResponse = paediatricService.getMasterVaccineList(searchTerm, isChartVaccine, page, size);
		Response<MasterVaccineResponse> response = new Response<MasterVaccineResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}
	
}
