package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_CLINICAL_NOTES_BASEURL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_CLINICAL_NOTES_BASEURL, description = "Endpoint for es clinical notes")
public class ESClinicalNotesApi {

	private static Logger logger = LogManager.getLogger(ESClinicalNotesApi.class.getName());

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	public Response<ESComplaintsDocument> searchComplaints(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESComplaintsDocument> response = esClinicalNotesService.searchComplaints(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
	public Response<ESDiagnosesDocument> searchDiagnoses(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESDiagnosesDocument> response = esClinicalNotesService.searchDiagnoses(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
	public Response<ESNotesDocument> searchNotes(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESNotesDocument> response = esClinicalNotesService.searchNotes(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
	public Response<ESDiagramsDocument> searchDiagrams(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESDiagramsDocument> response = esClinicalNotesService.searchDiagrams(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	// (value =
	// PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
	// @GetMapping
	// @ApiOperation(value =
	// PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY, notes =
	// PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
	// public Response<ESDiagramsDocument>
	// searchDiagramsBySpeciality(@PathVariable(value = "searchTerm") String
	// searchTerm) {
	//
	// List<ESDiagramsDocument> diagrams =
	// esClinicalNotesService.searchDiagramsBySpeciality(searchTerm);
	// diagrams = getFinalDiagrams(diagrams);
	// Response<ESDiagramsDocument> response = new
	// Response<ESDiagramsDocument>();
	// response.setDataList(diagrams);
	// return response;
	// }

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
	public Response<ESInvestigationsDocument> searchInvestigations(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESInvestigationsDocument> response = esClinicalNotesService.searchInvestigations(range, page, size,
				doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
	public Response<ESObservationsDocument> searchObservations(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESObservationsDocument> response = esClinicalNotesService.searchObservations(range, page, size,
				doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT)
	public Response<ESPresentComplaintDocument> searchPresentComplaint(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPresentComplaintDocument> response = esClinicalNotesService.searchPresentComplaints(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT_HISTORY)
	public Response<ESPresentComplaintHistoryDocument> searchPresentComplaintHistory(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPresentComplaintHistoryDocument> response = esClinicalNotesService
				.searchPresentComplaintsHistory(range, page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PROVISIONAL_DIAGNOSIS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PROVISIONAL_DIAGNOSIS)
	public Response<ESProvisionalDiagnosisDocument> searchProvisionalDiagnosis(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESProvisionalDiagnosisDocument> response = esClinicalNotesService.searchProvisionalDiagnosis(
				range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_GENERAL_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_GENERAL_EXAM)
	public Response<ESGeneralExamDocument> searchGeneralExam(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESGeneralExamDocument> response = esClinicalNotesService.searchGeneralExam(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_SYSTEM_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_SYSTEM_EXAM)
	public Response<ESSystemExamDocument> searchSystemExam(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESSystemExamDocument> response = esClinicalNotesService.searchSystemExam(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_MENSTRUAL_HISTORY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_MENSTRUAL_HISTORY)
	public Response<ESMenstrualHistoryDocument> searchMenstrualHistory(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESMenstrualHistoryDocument> response = esClinicalNotesService.searchMenstrualHistory(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSTETRIC_HISTORY)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSTETRIC_HISTORY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSTETRIC_HISTORY)
	public Response<ESObstetricHistoryDocument> searchObstetricHistory(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESObstetricHistoryDocument> response = esClinicalNotesService.searchObstetricHistory(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INDICATION_OF_USG, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INDICATION_OF_USG)
	public Response<ESIndicationOfUSGDocument> searchIndicationOfUSG(@PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESIndicationOfUSGDocument> response = esClinicalNotesService.searchIndicationOfUSG(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PA)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PA, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PA)
	public Response<ESPADocument> searchPA(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPADocument> response = esClinicalNotesService.searchPA(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PV)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PV, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PV)
	public Response<ESPVDocument> searchPV(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPVDocument> response = esClinicalNotesService.searchPV(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PS)
	public Response<ESPSDocument> searchPS(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPSDocument> response = esClinicalNotesService.searchPS(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_X_RAY_DETAILS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_X_RAY_DETAILS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_X_RAY_DETAILS)
	public Response<ESXRayDetailsDocument> searchXRayDetails(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESXRayDetailsDocument> response = esClinicalNotesService.searchXRayDetails(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECG_DETAILS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECG_DETAILS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_ECG_DETAILS)
	public Response<ESECGDetailsDocument> searchECGDetails(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESECGDetailsDocument> response = esClinicalNotesService.searchECGDetails(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECHO)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECHO, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_ECHO)
	public Response<ESEchoDocument> searchEcho(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESEchoDocument> response = esClinicalNotesService.searchEcho(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_HOLTER)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_HOLTER, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_HOLTER)
	public Response<ESHolterDocument> searchHolter(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESHolterDocument> response = esClinicalNotesService.searchHolter(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PROCEDURE_NOTE)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PROCEDURE_NOTE, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PROCEDURE_NOTE)
	public Response<ESProcedureNoteDocument> searchProcedureNote(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESProcedureNoteDocument> response = esClinicalNotesService.searchProcedureNote(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_NOSE)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_NOSE, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_NOSE)
	public Response<ESPresentingComplaintNoseDocument> searchPCNose(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPresentingComplaintNoseDocument> response = esClinicalNotesService.searchPCNose(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_EARS)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_EARS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_EARS)
	public Response<ESPresentingComplaintEarsDocument> searchPCEars(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPresentingComplaintEarsDocument> response = esClinicalNotesService.searchPCEars(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_THROAT)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_THROAT, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_THROAT)
	public Response<ESPresentingComplaintThroatDocument> searchPCThroat(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPresentingComplaintThroatDocument> response = esClinicalNotesService.searchPCThroat(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_ORAL_CAVITY)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_ORAL_CAVITY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PC_ORAL_CAVITY)
	public Response<ESPresentingComplaintOralCavityDocument> searchPCOralCavity(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESPresentingComplaintOralCavityDocument> response = esClinicalNotesService.searchPCOralCavity(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOSE_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOSE_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NOSE_EXAM)
	public Response<ESNoseExaminationDocument> searchNoseExam(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESNoseExaminationDocument> response = esClinicalNotesService.searchNoseExam(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NECK_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NECK_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NECK_EXAM)
	public Response<ESNeckExaminationDocument> searchNeckExam(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESNeckExaminationDocument> response = esClinicalNotesService.searchNeckExam(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_EARS_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_EARS_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_EARS_EXAM)
	public Response<ESEarsExaminationDocument> searchEarsExam(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESEarsExaminationDocument> response = esClinicalNotesService.searchEarsExam(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ORAL_CAVITY_THROAT_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ORAL_CAVITY_THROAT_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_ORAL_CAVITY_THROAT_EXAM)
	public Response<ESOralCavityAndThroatExaminationDocument> searchOralCavityThroatExam(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESOralCavityAndThroatExaminationDocument> response = esClinicalNotesService.searchOralCavityThroatExam(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INDIRECT_LARYGOSCOPY_EXAM)
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INDIRECT_LARYGOSCOPY_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INDIRECT_LARYGOSCOPY_EXAM)
	public Response<ESIndirectLarygoscopyExaminationDocument> searchIndirectLarygoscopyExam(@PathVariable("range") String range, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ESIndirectLarygoscopyExaminationDocument> response = esClinicalNotesService.searchIndirectLarygoscopyExam(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		return response;
	}

}
