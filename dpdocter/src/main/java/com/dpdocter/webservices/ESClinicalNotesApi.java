package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
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

@Component
@Path(PathProxy.SOLR_CLINICAL_NOTES_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_CLINICAL_NOTES_BASEURL, description = "Endpoint for es clinical notes")
public class ESClinicalNotesApi {

	private static Logger logger = Logger.getLogger(ESClinicalNotesApi.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	public Response<ESComplaintsDocument> searchComplaints(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESComplaintsDocument> complaints = esClinicalNotesService.searchComplaints(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESComplaintsDocument> response = new Response<ESComplaintsDocument>();
		response.setDataList(complaints);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
	public Response<ESDiagnosesDocument> searchDiagnoses(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESDiagnosesDocument> diagnoses = esClinicalNotesService.searchDiagnoses(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESDiagnosesDocument> response = new Response<ESDiagnosesDocument>();
		response.setDataList(diagnoses);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
	public Response<ESNotesDocument> searchNotes(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESNotesDocument> notes = esClinicalNotesService.searchNotes(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<ESNotesDocument> response = new Response<ESNotesDocument>();
		response.setDataList(notes);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
	public Response<ESDiagramsDocument> searchDiagrams(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESDiagramsDocument> diagrams = esClinicalNotesService.searchDiagrams(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		diagrams = getFinalDiagrams(diagrams);
		Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
		response.setDataList(diagrams);
		return response;
	}

	// @Path(value =
	// PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
	// @GET
	// @ApiOperation(value =
	// PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY, notes =
	// PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS_BY_SPECIALITY)
	// public Response<ESDiagramsDocument>
	// searchDiagramsBySpeciality(@PathParam(value = "searchTerm") String
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

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
	public Response<ESInvestigationsDocument> searchInvestigations(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESInvestigationsDocument> investigations = esClinicalNotesService.searchInvestigations(range, page, size,
				doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESInvestigationsDocument> response = new Response<ESInvestigationsDocument>();
		response.setDataList(investigations);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
	public Response<ESObservationsDocument> searchObservations(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESObservationsDocument> observations = esClinicalNotesService.searchObservations(range, page, size,
				doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESObservationsDocument> response = new Response<ESObservationsDocument>();
		response.setDataList(observations);
		return response;
	}

	private List<ESDiagramsDocument> getFinalDiagrams(List<ESDiagramsDocument> diagrams) {
		for (ESDiagramsDocument diagram : diagrams) {
			if (diagram.getDiagramUrl() != null) {
				diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
			}
		}
		return diagrams;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT)
	public Response<ESPresentComplaintDocument> searchPresentComplaint(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESPresentComplaintDocument> presentComplaints = esClinicalNotesService.searchPresentComplaints(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESPresentComplaintDocument> response = new Response<ESPresentComplaintDocument>();
		response.setDataList(presentComplaints);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PRESENT_COMPLAINT_HISTORY)
	public Response<ESPresentComplaintHistoryDocument> searchPresentComplaintHistory(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESPresentComplaintHistoryDocument> presentComplaintHistories = esClinicalNotesService
				.searchPresentComplaintsHistory(range, page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded, searchTerm);
		Response<ESPresentComplaintHistoryDocument> response = new Response<ESPresentComplaintHistoryDocument>();
		response.setDataList(presentComplaintHistories);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PROVISIONAL_DIAGNOSIS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PROVISIONAL_DIAGNOSIS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PROVISIONAL_DIAGNOSIS)
	public Response<ESProvisionalDiagnosisDocument> searchProvisionalDiagnosis(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESProvisionalDiagnosisDocument> provisionalDiagnosis = esClinicalNotesService.searchProvisionalDiagnosis(
				range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESProvisionalDiagnosisDocument> response = new Response<ESProvisionalDiagnosisDocument>();
		response.setDataList(provisionalDiagnosis);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_GENERAL_EXAM)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_GENERAL_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_GENERAL_EXAM)
	public Response<ESGeneralExamDocument> searchGeneralExam(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESGeneralExamDocument> generalExams = esClinicalNotesService.searchGeneralExam(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESGeneralExamDocument> response = new Response<ESGeneralExamDocument>();
		response.setDataList(generalExams);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_SYSTEM_EXAM)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_SYSTEM_EXAM, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_SYSTEM_EXAM)
	public Response<ESSystemExamDocument> searchSystemExam(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESSystemExamDocument> systemExams = esClinicalNotesService.searchSystemExam(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESSystemExamDocument> response = new Response<ESSystemExamDocument>();
		response.setDataList(systemExams);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_MENSTRUAL_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_MENSTRUAL_HISTORY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_MENSTRUAL_HISTORY)
	public Response<ESMenstrualHistoryDocument> searchMenstrualHistory(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESMenstrualHistoryDocument> menstrualHistories = esClinicalNotesService.searchMenstrualHistory(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESMenstrualHistoryDocument> response = new Response<ESMenstrualHistoryDocument>();
		response.setDataList(menstrualHistories);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSTETRIC_HISTORY)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSTETRIC_HISTORY, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSTETRIC_HISTORY)
	public Response<ESObstetricHistoryDocument> searchObstetricHistory(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESObstetricHistoryDocument> obstetricHistories = esClinicalNotesService.searchObstetricHistory(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESObstetricHistoryDocument> response = new Response<ESObstetricHistoryDocument>();
		response.setDataList(obstetricHistories);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INDICATION_OF_USG)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INDICATION_OF_USG, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_INDICATION_OF_USG)
	public Response<ESIndicationOfUSGDocument> searchIndicationOfUSG(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESIndicationOfUSGDocument> esIndicationOfUSGs = esClinicalNotesService.searchIndicationOfUSG(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESIndicationOfUSGDocument> response = new Response<ESIndicationOfUSGDocument>();
		response.setDataList(esIndicationOfUSGs);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PA)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PA, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PA)
	public Response<ESPADocument> searchPA(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESPADocument> espaDocuments = esClinicalNotesService.searchPA(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<ESPADocument> response = new Response<ESPADocument>();
		response.setDataList(espaDocuments);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PV)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PV, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PV)
	public Response<ESPVDocument> searchPV(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESPVDocument> espvDocuments = esClinicalNotesService.searchPV(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<ESPVDocument> response = new Response<ESPVDocument>();
		response.setDataList(espvDocuments);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_PS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_PS)
	public Response<ESPSDocument> searchPS(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESPSDocument> espsDocuments = esClinicalNotesService.searchPS(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<ESPSDocument> response = new Response<ESPSDocument>();
		response.setDataList(espsDocuments);
		return response;
	}
	
	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_X_RAY_DETAILS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_X_RAY_DETAILS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_X_RAY_DETAILS)
	public Response<ESXRayDetailsDocument> searchXRayDetails(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESXRayDetailsDocument> esxRayDetailsDocuments = esClinicalNotesService.searchXRayDetails(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESXRayDetailsDocument> response = new Response<ESXRayDetailsDocument>();
		response.setDataList(esxRayDetailsDocuments);
		return response;
	}
	
	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECG_DETAILS)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECG_DETAILS, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_ECG_DETAILS)
	public Response<ESECGDetailsDocument> searchECGDetails(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESECGDetailsDocument> esecgDetailsDocuments = esClinicalNotesService.searchECGDetails(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESECGDetailsDocument> response = new Response<ESECGDetailsDocument>();
		response.setDataList(esecgDetailsDocuments);
		return response;
	}
	
	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECHO)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_ECHO, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_ECHO)
	public Response<ESEchoDocument> searchEcho(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESEchoDocument> esEchoDocuments = esClinicalNotesService.searchEcho(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESEchoDocument> response = new Response<ESEchoDocument>();
		response.setDataList(esEchoDocuments);
		return response;
	}
	
	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_HOLTER)
	@GET
	@ApiOperation(value = PathProxy.SolrClinicalNotesUrls.SEARCH_HOLTER, notes = PathProxy.SolrClinicalNotesUrls.SEARCH_HOLTER)
	public Response<ESHolterDocument> searchHolter(@PathParam("range") String range, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESHolterDocument> espsDocuments = esClinicalNotesService.searchHolter(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESHolterDocument> response = new Response<ESHolterDocument>();
		response.setDataList(espsDocuments);
		return response;
	}

}
