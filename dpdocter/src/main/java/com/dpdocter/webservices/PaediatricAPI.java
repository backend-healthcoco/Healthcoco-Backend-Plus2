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

import com.dpdocter.beans.BirthAchievement;
import com.dpdocter.beans.GrowthChart;
import com.dpdocter.elasticsearch.response.GrowthChartGraphResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.MultipleVaccineEditRequest;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.GroupedVaccineBrandAssociationResponse;
import com.dpdocter.response.MasterVaccineResponse;
import com.dpdocter.response.PatientVaccineGroupedResponse;
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
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		VaccineResponse vaccineResponse = paediatricService.addEditVaccine(request);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setData(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.ADD_EDIT_GROWTH_CHART)
	@POST
	@ApiOperation(value = PathProxy.PaediatricUrls.ADD_EDIT_GROWTH_CHART, notes = PathProxy.PaediatricUrls.ADD_EDIT_GROWTH_CHART)
	public Response<GrowthChart> addGrowthChart(GrowthChart request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		GrowthChart growthChart = paediatricService.addEditGrowthChart(request);
		Response<GrowthChart> response = new Response<GrowthChart>();
		response.setData(growthChart);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.ADD_EDIT_ACHIEVEMENT)
	@POST
	@ApiOperation(value = PathProxy.PaediatricUrls.ADD_EDIT_ACHIEVEMENT, notes = PathProxy.PaediatricUrls.ADD_EDIT_ACHIEVEMENT)
	public Response<BirthAchievement> addEditBirthAchievement(BirthAchievement request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		BirthAchievement growthChart = paediatricService.addEditBirthAchievement(request);
		Response<BirthAchievement> response = new Response<BirthAchievement>();
		response.setData(growthChart);
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

	@Path(value = PathProxy.PaediatricUrls.GET_GROWTH_CHART_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROWTH_CHART_BY_ID, notes = PathProxy.PaediatricUrls.GET_GROWTH_CHART_BY_ID)
	public Response<GrowthChart> getGrowthChartById(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		GrowthChart growthChart = paediatricService.getGrowthChartById(id);
		Response<GrowthChart> response = new Response<GrowthChart>();
		response.setData(growthChart);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENT_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENT_BY_ID, notes = PathProxy.PaediatricUrls.GET_ACHIEVEMENT_BY_ID)
	public Response<BirthAchievement> getbirthAchievementById(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		BirthAchievement birthAchievement = paediatricService.getBirthAchievementById(id);
		Response<BirthAchievement> response = new Response<BirthAchievement>();
		response.setData(birthAchievement);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.DISCARD_GROWTH_CHART_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.DISCARD_GROWTH_CHART_BY_ID, notes = PathProxy.PaediatricUrls.DISCARD_GROWTH_CHART_BY_ID)
	public Response<Boolean> discardGrowthChart(@PathParam("id") String id,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean status = paediatricService.discardGrowthChart(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_VACCINES)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINES, notes = PathProxy.PaediatricUrls.GET_VACCINES)
	public Response<VaccineResponse> getVaccines(@QueryParam("patientId") String patientId,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<VaccineResponse> vaccineResponse = paediatricService.getVaccineList(patientId, doctorId, locationId,
				hospitalId, updatedTime);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS, notes = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS)
	public Response<GrowthChart> getGrowthCharts(@QueryParam("patientId") String patientId,
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<GrowthChart> vaccineResponse = paediatricService.getGrowthChartList(patientId, doctorId, locationId,
				hospitalId, updatedTime);
		Response<GrowthChart> response = new Response<GrowthChart>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENTS)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENTS, notes = PathProxy.PaediatricUrls.GET_ACHIEVEMENTS)
	public Response<BirthAchievement> getGrowthCharts(@PathParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @QueryParam("page") int page,
			@QueryParam("size") int size) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<BirthAchievement> vaccineResponse = paediatricService.getBirthAchievementList(patientId, updatedTime, page,
				size);
		Response<BirthAchievement> response = new Response<BirthAchievement>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION, notes = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION)
	public Response<VaccineBrandAssociationResponse> getVaccineBrandAssociation(
			@QueryParam("vaccineId") String vaccineId, @QueryParam("vaccineBrandId") String vaccineBrandId) {
		/*
		 * if (DPDoctorUtils.allStringsEmpty(vaccineBrandId,vaccineId)) {
		 * logger.warn("Invalid Input"); throw new
		 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); }
		 */
		List<VaccineBrandAssociationResponse> vaccineResponse = paediatricService.getVaccineBrandAssociation(vaccineId,
				vaccineBrandId);
		Response<VaccineBrandAssociationResponse> response = new Response<VaccineBrandAssociationResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION, notes = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION)
	public Response<GroupedVaccineBrandAssociationResponse> getMultipleVaccineBrandAssociation(
			@MatrixParam("vaccineId") List<String> vaccineIds, @QueryParam("vaccineBrandId") String vaccineBrandId) {
		/*
		 * if (DPDoctorUtils.allStringsEmpty(vaccineBrandId,vaccineId)) {
		 * logger.warn("Invalid Input"); throw new
		 * BusinessException(ServiceError.InvalidInput, "Invalid Input"); }
		 */
		List<GroupedVaccineBrandAssociationResponse> vaccineResponse = paediatricService
				.getGroupedVaccineBrandAssociation(vaccineIds);
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
		Response<Boolean> response = new Response<Boolean>();
		response.setData(paediatricService.addEditMultipleVaccineStatus(request));
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_MASTER_VACCINES)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_MASTER_VACCINES, notes = PathProxy.PaediatricUrls.GET_MASTER_VACCINES)
	public Response<MasterVaccineResponse> getVaccines(@QueryParam("searchTerm") String searchTerm,
			@DefaultValue("false") @QueryParam("isChartVaccine") Boolean isChartVaccine, @QueryParam("page") int page,
			@QueryParam("size") int size) {
		List<MasterVaccineResponse> vaccineResponse = paediatricService.getMasterVaccineList(searchTerm, isChartVaccine,
				page, size);
		Response<MasterVaccineResponse> response = new Response<MasterVaccineResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.UPDATE_OLD_DATA)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.UPDATE_OLD_DATA, notes = PathProxy.PaediatricUrls.UPDATE_OLD_DATA)
	public Response<Boolean> updateOldData() {
		Response<Boolean> response = new Response<>();
		response.setData(paediatricService.updateOldPatientData());
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_GROUPED_VACCINES)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROUPED_VACCINES, notes = PathProxy.PaediatricUrls.GET_GROUPED_VACCINES)
	public Response<PatientVaccineGroupedResponse> getGroupedVaccines(@QueryParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<PatientVaccineGroupedResponse> vaccineResponse = paediatricService.getPatientGroupedVaccines(patientId);
		Response<PatientVaccineGroupedResponse> response = new Response<PatientVaccineGroupedResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.UPDATE_VACCINATION_CHART)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.UPDATE_VACCINATION_CHART, notes = PathProxy.PaediatricUrls.UPDATE_VACCINATION_CHART)
	public Response<Boolean> updateVaccinationChart(@PathParam("patientId") String patientId,
			@PathParam("vaccineStartDate") Long vaccineStartDate) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean vaccineResponse = paediatricService.updateImmunisationChart(patientId, vaccineStartDate);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS_GRAPH)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS_GRAPH, notes = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS_GRAPH)
	public Response<GrowthChartGraphResponse> getGrowthCharts(@QueryParam("patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<GrowthChartGraphResponse> vaccineResponse = paediatricService.getGrowthChartList(patientId, updatedTime);
		Response<GrowthChartGraphResponse> response = new Response<GrowthChartGraphResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	@Path(value = PathProxy.PaediatricUrls.DOWNLOAD_VACCINE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.PaediatricUrls.DOWNLOAD_VACCINE_BY_ID, notes = PathProxy.PaediatricUrls.DOWNLOAD_VACCINE_BY_ID)
	public Response<String> downloadCVaccineById(@PathParam("periodTime") Integer periodTime,
			@QueryParam("patientId") String patientId, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(
				paediatricService.downloadVaccineById(periodTime, patientId, doctorId, locationId, hospitalId));
		return response;
	}
}
