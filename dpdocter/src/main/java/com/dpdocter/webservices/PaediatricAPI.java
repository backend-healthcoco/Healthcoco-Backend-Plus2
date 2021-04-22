package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping(value=PathProxy.PAEDIATRIC_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.PAEDIATRIC_BASE_URL, description = "Endpoint for paediatric")
public class PaediatricAPI {

	private static Logger logger = LogManager.getLogger(ProcedureAPI.class.getName());

	@Autowired
	private PaediatricService paediatricService;

	
	@PostMapping(value = PathProxy.PaediatricUrls.ADD_EDIT_VACCINE)
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

	
	@PostMapping(value = PathProxy.PaediatricUrls.ADD_EDIT_GROWTH_CHART)
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

	
	@PostMapping(value = PathProxy.PaediatricUrls.ADD_EDIT_ACHIEVEMENT)
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

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_VACCINE_BY_ID)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINE_BY_ID, notes = PathProxy.PaediatricUrls.GET_VACCINE_BY_ID)
	public Response<VaccineResponse> getVaccineById(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		VaccineResponse vaccineResponse = paediatricService.getVaccineById(id);
		Response<VaccineResponse> response = new Response<VaccineResponse>();
		response.setData(vaccineResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_GROWTH_CHART_BY_ID)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROWTH_CHART_BY_ID, notes = PathProxy.PaediatricUrls.GET_GROWTH_CHART_BY_ID)
	public Response<GrowthChart> getGrowthChartById(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		GrowthChart growthChart = paediatricService.getGrowthChartById(id);
		Response<GrowthChart> response = new Response<GrowthChart>();
		response.setData(growthChart);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENT_BY_ID)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENT_BY_ID, notes = PathProxy.PaediatricUrls.GET_ACHIEVEMENT_BY_ID)
	public Response<BirthAchievement> getbirthAchievementById(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		BirthAchievement birthAchievement = paediatricService.getBirthAchievementById(id);
		Response<BirthAchievement> response = new Response<BirthAchievement>();
		response.setData(birthAchievement);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.DISCARD_GROWTH_CHART_BY_ID)
	@ApiOperation(value = PathProxy.PaediatricUrls.DISCARD_GROWTH_CHART_BY_ID, notes = PathProxy.PaediatricUrls.DISCARD_GROWTH_CHART_BY_ID)
	public Response<Boolean> discardGrowthChart(@PathVariable("id") String id,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean status = paediatricService.discardGrowthChart(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_VACCINES)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINES, notes = PathProxy.PaediatricUrls.GET_VACCINES)
	public Response<VaccineResponse> getVaccines(@RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {
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

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS, notes = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS)
	public Response<GrowthChart> getGrowthCharts(@RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {
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

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENTS)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_ACHIEVEMENTS, notes = PathProxy.PaediatricUrls.GET_ACHIEVEMENTS)
	public Response<BirthAchievement> getGrowthCharts(@PathVariable("patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime, @RequestParam("page") int page,
			@RequestParam("size") int size) {
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

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION, notes = PathProxy.PaediatricUrls.GET_VACCINE_BRAND_ASSOCIATION)
	public Response<VaccineBrandAssociationResponse> getVaccineBrandAssociation(
			@RequestParam("vaccineId") String vaccineId, @RequestParam("vaccineBrandId") String vaccineBrandId) {
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

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION, notes = PathProxy.PaediatricUrls.GET_MULTIPLE_VACCINE_BRAND_ASSOCIATION)
	public Response<GroupedVaccineBrandAssociationResponse> getMultipleVaccineBrandAssociation(
			@MatrixParam("vaccineId") List<String> vaccineIds, @RequestParam("vaccineBrandId") String vaccineBrandId) {
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

	
	@PostMapping(value = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE)
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

	
	@PostMapping(value = PathProxy.PaediatricUrls.ADD_EDIT_MULTIPLE_VACCINE_STATUS)
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

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_MASTER_VACCINES)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_MASTER_VACCINES, notes = PathProxy.PaediatricUrls.GET_MASTER_VACCINES)
	public Response<MasterVaccineResponse> getVaccines(@RequestParam("searchTerm") String searchTerm,
			@DefaultValue("false") @RequestParam("isChartVaccine") Boolean isChartVaccine, @RequestParam("page") int page,
			@RequestParam("size") int size) {
		List<MasterVaccineResponse> vaccineResponse = paediatricService.getMasterVaccineList(searchTerm, isChartVaccine,
				page, size);
		Response<MasterVaccineResponse> response = new Response<MasterVaccineResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.UPDATE_OLD_DATA)
	@ApiOperation(value = PathProxy.PaediatricUrls.UPDATE_OLD_DATA, notes = PathProxy.PaediatricUrls.UPDATE_OLD_DATA)
	public Response<Boolean> updateOldData() {
		Response<Boolean> response = new Response<>();
		response.setData(paediatricService.updateOldPatientData());
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_GROUPED_VACCINES)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROUPED_VACCINES, notes = PathProxy.PaediatricUrls.GET_GROUPED_VACCINES)
	public Response<PatientVaccineGroupedResponse> getGroupedVaccines(@RequestParam("patientId") String patientId) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<PatientVaccineGroupedResponse> vaccineResponse = paediatricService.getPatientGroupedVaccines(patientId);
		Response<PatientVaccineGroupedResponse> response = new Response<PatientVaccineGroupedResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.UPDATE_VACCINATION_CHART)
	@ApiOperation(value = PathProxy.PaediatricUrls.UPDATE_VACCINATION_CHART, notes = PathProxy.PaediatricUrls.UPDATE_VACCINATION_CHART)
	public Response<Boolean> updateVaccinationChart(@PathVariable("patientId") String patientId,
			@PathVariable("vaccineStartDate") Long vaccineStartDate) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean vaccineResponse = paediatricService.updateImmunisationChart(patientId, vaccineStartDate);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(vaccineResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS_GRAPH)
	@ApiOperation(value = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS_GRAPH, notes = PathProxy.PaediatricUrls.GET_GROWTH_CHARTS_GRAPH)
	public Response<GrowthChartGraphResponse> getGrowthCharts(@RequestParam("patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<GrowthChartGraphResponse> vaccineResponse = paediatricService.getGrowthChartList(patientId, updatedTime);
		Response<GrowthChartGraphResponse> response = new Response<GrowthChartGraphResponse>();
		response.setDataList(vaccineResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PaediatricUrls.DOWNLOAD_VACCINE_BY_ID)
	@ApiOperation(value = PathProxy.PaediatricUrls.DOWNLOAD_VACCINE_BY_ID, notes = PathProxy.PaediatricUrls.DOWNLOAD_VACCINE_BY_ID)
	public Response<String> downloadCVaccineById(@PathVariable("periodTime") Integer periodTime,
			@RequestParam("patientId") String patientId, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId) {
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
