package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.ECGDetails;
import com.dpdocter.beans.EarsExamination;
import com.dpdocter.beans.Echo;
import com.dpdocter.beans.EyeObservation;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.Holter;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.IndirectLarygoscopyExamination;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.NeckExamination;
import com.dpdocter.beans.NoseExamination;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.OralCavityAndThroatExamination;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PS;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.XRayDetails;
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
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.request.ListIdrequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.OphthalmologyService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CLINICAL_NOTES_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CLINICAL_NOTES_BASE_URL, description = "Endpoint for clinical notes")
public class ClinicalNotesApi {

	private static Logger logger = LogManager.getLogger(ClinicalNotesApi.class.getName());

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private PatientVisitService patientTrackService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private OTPService otpService;

	@Autowired
	private OphthalmologyService ophthalmologyService;

	@Value(value = "${image.path}")
	private String imagePath;

	@PostMapping(value = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE, notes = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
	public Response<ClinicalNotes> addNotes(@RequestBody ClinicalNotesAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request, true, null, null);

		if (clinicalNotes != null) {
			String visitId = patientTrackService.addRecord(clinicalNotes, VisitedFor.CLINICAL_NOTES,
					request.getVisitId());
			clinicalNotes.setVisitId(visitId);
		}
		if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
			clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
		}
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}

	@PutMapping(value = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
	public Response<ClinicalNotes> editNotes(@PathVariable(value = "clinicalNotesId") String clinicalNotesId,
			@RequestBody ClinicalNotesEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(clinicalNotesId, request.getDoctorId(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		request.setId(clinicalNotesId);
		ClinicalNotes clinicalNotes = clinicalNotesService.editNotes(request);
		if (clinicalNotes != null) {
			String visitId = patientTrackService.editRecord(clinicalNotes.getId(), VisitedFor.CLINICAL_NOTES);
			clinicalNotes.setVisitId(visitId);
		}
		if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
			clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
		}
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
	public Response<ClinicalNotes> deleteNotes(@PathVariable(value = "clinicalNotesId") String clinicalNotesId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		ClinicalNotes clinicalNotes = clinicalNotesService.deleteNote(clinicalNotesId, discarded);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}

	@GetMapping(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID, notes = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
	public Response<ClinicalNotes> getNotesById(@PathVariable(value = "clinicalNotesId") String clinicalNotesId) {
		ClinicalNotes clinicalNotes = clinicalNotesService.getNotesById(clinicalNotesId, null);
		if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
			clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
		}
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}

	@GetMapping
	@ApiOperation(value = "GET_CLINICAL_NOTES", notes = "GET_CLINICAL_NOTES")
	public Response<ClinicalNotes> getNotes(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {

		List<ClinicalNotes> clinicalNotes = clinicalNotesService.getClinicalNotes(page, size, doctorId, locationId,
				hospitalId, patientId, updatedTime,
				otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded, false);

		if (clinicalNotes != null && !clinicalNotes.isEmpty()) {
			for (ClinicalNotes clinicalNote : clinicalNotes) {
				if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
					clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
				}
			}
		}
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setDataList(clinicalNotes);
		return response;
	}

	@GetMapping(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_PATIENT_ID)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_PATIENT_ID, notes = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_PATIENT_ID)
	public Response<ClinicalNotes> getNotes(@PathVariable(value = "patientId") String patientId,
			@RequestParam("page") long page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ClinicalNotes> clinicalNotes = null;
		clinicalNotes = clinicalNotesService.getClinicalNotes(patientId, page, size, updatedTime, discarded);

		if (clinicalNotes != null && !clinicalNotes.isEmpty()) {
			for (ClinicalNotes clinicalNote : clinicalNotes) {
				if (clinicalNote.getDiagrams() != null && !clinicalNote.getDiagrams().isEmpty()) {
					clinicalNote.setDiagrams(getFinalDiagrams(clinicalNote.getDiagrams()));
				}
			}
		}
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setDataList(clinicalNotes);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
	public Response<Complaint> addComplaint(@RequestBody Complaint request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getComplaint())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Complaint complaint = clinicalNotesService.addEditComplaint(request);

		transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
		ESComplaintsDocument esComplaints = new ESComplaintsDocument();
		BeanUtil.map(complaint, esComplaints);
		esClinicalNotesService.addComplaints(esComplaints);

		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
	public Response<Observation> addObservation(@RequestBody Observation request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getObservation())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Observation observation = clinicalNotesService.addEditObservation(request);

		transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
		ESObservationsDocument esObservations = new ESObservationsDocument();
		BeanUtil.map(observation, esObservations);
		esClinicalNotesService.addObservations(esObservations);
		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION, notes = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
	public Response<Investigation> addInvestigation(@RequestBody Investigation request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getInvestigation())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Investigation investigation = clinicalNotesService.addEditInvestigation(request);

		transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION, false);
		ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
		BeanUtil.map(investigation, esInvestigations);
		esClinicalNotesService.addInvestigations(esInvestigations);

		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
	public Response<Diagnoses> addDiagnosis(@RequestBody Diagnoses request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getDiagnosis())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Diagnoses diagnosis = clinicalNotesService.addEditDiagnosis(request);

		transactionalManagementService.addResource(new ObjectId(diagnosis.getId()), Resource.DIAGNOSIS, false);
		ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
		BeanUtil.map(diagnosis, esDiagnoses);
		esClinicalNotesService.addDiagnoses(esDiagnoses);

		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setData(diagnosis);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NOTES, notes = PathProxy.ClinicalNotesUrls.ADD_NOTES)
	public Response<Notes> addNotes(@RequestBody Notes request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNote())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Notes notes = clinicalNotesService.addEditNotes(request);

		transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
		ESNotesDocument esNotes = new ESNotesDocument();
		BeanUtil.map(notes, esNotes);
		esClinicalNotesService.addNotes(esNotes);

		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM, notes = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
	public Response<Diagram> addDiagram(@RequestBody Diagram request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())
				|| request.getDiagram() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		/*
		 * try {
		 * DPDoctorUtils.fileValidator(request.getDiagram().getFileEncoded()); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		Diagram diagram = clinicalNotesService.addEditDiagram(request);
		transactionalManagementService.addResource(new ObjectId(diagram.getId()), Resource.DIAGRAM, false);
		ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
		BeanUtil.map(diagram, esDiagrams);
		esClinicalNotesService.addDiagrams(esDiagrams);

		if (diagram.getDiagramUrl() != null) {
			diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
		}
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
	public Response<Complaint> deleteComplaint(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Complaint complaint = clinicalNotesService.deleteComplaint(id, doctorId, locationId, hospitalId, discarded);

		if (complaint != null) {
			transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaint, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
		}
		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
	public Response<Observation> deleteObservation(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Observation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Observation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Observation observation = clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId,
				discarded);
		if (observation != null) {
			transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observation, esObservations);
			esClinicalNotesService.addObservations(esObservations);
		}

		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION, notes = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
	public Response<Investigation> deleteInvestigation(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded")Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Investigation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Investigation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Investigation investigation = clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId,
				discarded);
		if (investigation != null) {
			transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION,
					false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigation, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
		}

		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
	public Response<Diagnoses> deleteDiagnosis(@PathVariable("id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			 @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Diagnoses diagnoses = clinicalNotesService.deleteDiagnosis(id, doctorId, locationId, hospitalId, discarded);
		if (diagnoses != null) {
			transactionalManagementService.addResource(new ObjectId(diagnoses.getId()), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnoses, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
		}
		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setData(diagnoses);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE, notes = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
	public Response<Notes> deleteNote(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Note Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Note Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Notes notes = clinicalNotesService.deleteNotes(id, doctorId, locationId, hospitalId, discarded);
		if (notes != null) {
			transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
			ESNotesDocument esNotes = new ESNotesDocument();
			BeanUtil.map(notes, esNotes);
			esClinicalNotesService.addNotes(esNotes);
		}
		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM, notes = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
	public Response<Diagram> deleteDiagram(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Diagram Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Diagram, Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Diagram diagram = clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId, discarded);
		if (diagram != null) {
			ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
			BeanUtil.map(diagram, esDiagrams);
			esClinicalNotesService.addDiagrams(esDiagrams);
		}
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@GetMapping(value = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS, notes = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS)
	public Response<Object> getClinicalItems(@PathVariable("type") String type, @PathVariable("range") String range,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(type, range, doctorId)) {
			logger.warn("Invalid Input.");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
		}
		List<?> clinicalItems = clinicalNotesService.getClinicalItems(type, range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, null);
		if (clinicalItems != null && !clinicalItems.isEmpty()
				&& ClinicalItems.DIAGRAMS.getType().equalsIgnoreCase(type)) {
			for (Object clinicalItem : clinicalItems) {
				((Diagram) clinicalItem).setDiagramUrl(getFinalImageURL(((Diagram) clinicalItem).getDiagramUrl()));
			}
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(clinicalItems);
		return response;
	}

	@GetMapping(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES)
	public Response<Boolean> emailClinicalNotes(@PathVariable(value = "clinicalNotesId") String clinicalNotesId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		clinicalNotesService.emailClinicalNotes(clinicalNotesId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	private List<Diagram> getFinalDiagrams(List<Diagram> diagrams) {
		for (Diagram diagram : diagrams) {
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

	@GetMapping(value = PathProxy.ClinicalNotesUrls.DOWNLOAD_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DOWNLOAD_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.DOWNLOAD_CLINICAL_NOTES)
	public Response<String> downloadClinicalNotes(@PathVariable("clinicalNotesId") String clinicalNotesId,
			@DefaultValue("false") @RequestParam("showPH") Boolean showPH,
			@DefaultValue("false") @RequestParam("showPLH") Boolean showPLH,
			@DefaultValue("false") @RequestParam("showFH") Boolean showFH,
			@DefaultValue("false") @RequestParam("showDA") Boolean showDA,
			@DefaultValue("false") @RequestParam("showUSG") Boolean showUSG,
			@DefaultValue("false") @RequestParam("isCustomPDF") Boolean isCustomPDF,
			@DefaultValue("false") @RequestParam("showLMP") Boolean showLMP,
			@DefaultValue("false") @RequestParam("showEDD") Boolean showEDD,
			@DefaultValue("false") @RequestParam("showNoOfChildren") Boolean showNoOfChildren) {

		Response<String> response = new Response<String>();
		response.setData(clinicalNotesService.getClinicalNotesFile(clinicalNotesId, showPH, showPLH, showFH, showDA,
				showUSG, isCustomPDF, showLMP, showEDD, showNoOfChildren));
		return response;
	}

	@GetMapping(value = PathProxy.ClinicalNotesUrls.UPDATE_QUERY_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.UPDATE_QUERY_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.UPDATE_QUERY_CLINICAL_NOTES)
	public Response<Boolean> updateQuery() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(clinicalNotesService.updateQuery());
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT)
	public Response<PresentComplaint> addPresentComplaints(@RequestBody PresentComplaint request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPresentComplaint())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentComplaint presentComplaint = clinicalNotesService.addEditPresentComplaint(request);

		transactionalManagementService.addResource(new ObjectId(presentComplaint.getId()), Resource.PRESENT_COMPLAINT,
				false);
		ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
		BeanUtil.map(presentComplaint, esPresentComplaint);
		esClinicalNotesService.addPresentComplaint(esPresentComplaint);
		Response<PresentComplaint> response = new Response<PresentComplaint>();
		response.setData(presentComplaint);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT_HISTORY)
	public Response<PresentComplaintHistory> addPresentComplaintsHistory(@RequestBody PresentComplaintHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPresentComplaintHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentComplaintHistory presentComplaintHistory = clinicalNotesService.addEditPresentComplaintHistory(request);

		transactionalManagementService.addResource(new ObjectId(presentComplaintHistory.getId()),
				Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
		ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
		BeanUtil.map(presentComplaintHistory, esPresentComplaintHistory);
		esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
		Response<PresentComplaintHistory> response = new Response<PresentComplaintHistory>();
		response.setData(presentComplaintHistory);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PROVISIONAL_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.ADD_PROVISIONAL_DIAGNOSIS)
	public Response<ProvisionalDiagnosis> addProvisionalDiagnosis(@RequestBody ProvisionalDiagnosis request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getProvisionalDiagnosis())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ProvisionalDiagnosis provisionalDiagnosis = clinicalNotesService.addEditProvisionalDiagnosis(request);

		transactionalManagementService.addResource(new ObjectId(provisionalDiagnosis.getId()),
				Resource.PROVISIONAL_DIAGNOSIS, false);
		ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
		BeanUtil.map(provisionalDiagnosis, esProvisionalDiagnosis);
		esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
		Response<ProvisionalDiagnosis> response = new Response<ProvisionalDiagnosis>();
		response.setData(provisionalDiagnosis);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_SYSTEM_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_SYSTEM_EXAM)
	public Response<SystemExam> addSystemExam(@RequestBody SystemExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getSystemExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		SystemExam systemExam = clinicalNotesService.addEditSystemExam(request);

		transactionalManagementService.addResource(new ObjectId(systemExam.getId()), Resource.SYSTEMIC_EXAMINATION,
				false);
		ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
		BeanUtil.map(systemExam, esSystemExam);
		esClinicalNotesService.addSystemExam(esSystemExam);
		Response<SystemExam> response = new Response<SystemExam>();
		response.setData(systemExam);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_GENERAL_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_GENERAL_EXAM)
	public Response<GeneralExam> addGeneralExam(@RequestBody GeneralExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getGeneralExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		GeneralExam generalExam = clinicalNotesService.addEditGeneralExam(request);

		transactionalManagementService.addResource(new ObjectId(generalExam.getId()), Resource.GENERAL_EXAMINATION,
				false);
		ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
		BeanUtil.map(generalExam, esGeneralExam);
		esClinicalNotesService.addGeneralExam(esGeneralExam);
		Response<GeneralExam> response = new Response<GeneralExam>();
		response.setData(generalExam);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_MENSTRUAL_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_MENSTRUAL_HISTORY)
	public Response<MenstrualHistory> addMenstrualHistory(@RequestBody MenstrualHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getMenstrualHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		MenstrualHistory menstrualHistory = clinicalNotesService.addEditMenstrualHistory(request);

		transactionalManagementService.addResource(new ObjectId(menstrualHistory.getId()), Resource.MENSTRUAL_HISTORY,
				false);
		ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
		BeanUtil.map(menstrualHistory, esMenstrualHistory);
		esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
		Response<MenstrualHistory> response = new Response<MenstrualHistory>();
		response.setData(menstrualHistory);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY)
	public Response<ObstetricHistory> addObstetricHistory(@RequestBody ObstetricHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getObstetricHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ObstetricHistory obstetricHistory = clinicalNotesService.addEditObstetricHistory(request);

		transactionalManagementService.addResource(new ObjectId(obstetricHistory.getId()), Resource.OBSTETRIC_HISTORY,
				false);
		ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
		BeanUtil.map(obstetricHistory, esObstetricHistory);
		esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
		Response<ObstetricHistory> response = new Response<ObstetricHistory>();
		response.setData(obstetricHistory);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INDICATION_OF_USG, notes = PathProxy.ClinicalNotesUrls.ADD_INDICATION_OF_USG)
	public Response<IndicationOfUSG> addIndicationOfUSG(@RequestBody IndicationOfUSG request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getIndicationOfUSG())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		IndicationOfUSG indicationOfUSG = clinicalNotesService.addEditIndicationOfUSG(request);

		transactionalManagementService.addResource(new ObjectId(indicationOfUSG.getId()), Resource.INDICATION_OF_USG,
				false);
		ESIndicationOfUSGDocument esIndicationOfUSG = new ESIndicationOfUSGDocument();
		BeanUtil.map(indicationOfUSG, esIndicationOfUSG);
		esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG);
		Response<IndicationOfUSG> response = new Response<IndicationOfUSG>();
		response.setData(indicationOfUSG);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PA)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PA, notes = PathProxy.ClinicalNotesUrls.ADD_PA)
	public Response<PA> addEditPA(@RequestBody PA request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPa())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PA pa = clinicalNotesService.addEditPA(request);

		transactionalManagementService.addResource(new ObjectId(pa.getId()), Resource.PA, false);
		ESPADocument espa = new ESPADocument();
		BeanUtil.map(pa, espa);
		esClinicalNotesService.addPA(espa);
		Response<PA> response = new Response<PA>();
		response.setData(pa);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PV)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PV, notes = PathProxy.ClinicalNotesUrls.ADD_PV)
	public Response<PV> addEditPV(@RequestBody PV request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPv())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PV pv = clinicalNotesService.addEditPV(request);

		transactionalManagementService.addResource(new ObjectId(pv.getId()), Resource.PV, false);
		ESPVDocument espv = new ESPVDocument();
		BeanUtil.map(pv, espv);
		esClinicalNotesService.addPV(espv);
		Response<PV> response = new Response<PV>();
		response.setData(pv);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PS, notes = PathProxy.ClinicalNotesUrls.ADD_PS)
	public Response<PS> addEditPS(@RequestBody PS request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPs())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PS ps = clinicalNotesService.addEditPS(request);

		transactionalManagementService.addResource(new ObjectId(ps.getId()), Resource.PS, false);
		ESPSDocument esps = new ESPSDocument();
		BeanUtil.map(ps, esps);
		esClinicalNotesService.addPS(esps);
		Response<PS> response = new Response<PS>();
		response.setData(ps);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PROCEDURE_NOTE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PROCEDURE_NOTE, notes = PathProxy.ClinicalNotesUrls.ADD_PROCEDURE_NOTE)
	public Response<ProcedureNote> addEditProcedureNote(@RequestBody ProcedureNote request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getProcedureNote())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ProcedureNote procedureNote = clinicalNotesService.addEditProcedureNote(request);
		transactionalManagementService.addResource(new ObjectId(procedureNote.getId()), Resource.PROCEDURE_NOTE, false);
		ESProcedureNoteDocument esProcedureNoteDocument = new ESProcedureNoteDocument();
		BeanUtil.map(procedureNote, esProcedureNoteDocument);
		esClinicalNotesService.addProcedureNote(esProcedureNoteDocument);
		Response<ProcedureNote> response = new Response<ProcedureNote>();
		response.setData(procedureNote);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_X_RAY_DETAILS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_X_RAY_DETAILS, notes = PathProxy.ClinicalNotesUrls.ADD_X_RAY_DETAILS)
	public Response<XRayDetails> addEditXRayDetails(@RequestBody XRayDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getxRayDetails())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		XRayDetails xRayDetails = clinicalNotesService.addEditXRayDetails(request);

		transactionalManagementService.addResource(new ObjectId(xRayDetails.getId()), Resource.XRAY, false);
		ESXRayDetailsDocument esxRayDetails = new ESXRayDetailsDocument();
		BeanUtil.map(xRayDetails, esxRayDetails);
		esClinicalNotesService.addXRayDetails(esxRayDetails);
		Response<XRayDetails> response = new Response<XRayDetails>();
		response.setData(xRayDetails);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_ECG_DETAILS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_ECG_DETAILS, notes = PathProxy.ClinicalNotesUrls.ADD_ECG_DETAILS)
	public Response<ECGDetails> addEditECGDetails(@RequestBody ECGDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getEcgDetails())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ECGDetails ecgDetails = clinicalNotesService.addEditECGDetails(request);

		transactionalManagementService.addResource(new ObjectId(ecgDetails.getId()), Resource.ECG, false);
		ESECGDetailsDocument esecgDetails = new ESECGDetailsDocument();
		BeanUtil.map(ecgDetails, esecgDetails);
		esClinicalNotesService.addECGDetails(esecgDetails);
		Response<ECGDetails> response = new Response<ECGDetails>();
		response.setData(ecgDetails);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_ECHO)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_ECHO, notes = PathProxy.ClinicalNotesUrls.ADD_ECHO)
	public Response<Echo> addEditEcho(@RequestBody Echo request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getEcho())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Echo echo = clinicalNotesService.addEditEcho(request);

		transactionalManagementService.addResource(new ObjectId(echo.getId()), Resource.ECHO, false);
		ESEchoDocument esEcho = new ESEchoDocument();
		BeanUtil.map(echo, esEcho);
		esClinicalNotesService.addEcho(esEcho);
		Response<Echo> response = new Response<Echo>();
		response.setData(echo);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_HOLTER)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_HOLTER, notes = PathProxy.ClinicalNotesUrls.ADD_HOLTER)
	public Response<Holter> addEditHolter(@RequestBody Holter request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getHolter())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Holter holter = clinicalNotesService.addEditHolter(request);

		transactionalManagementService.addResource(new ObjectId(holter.getId()), Resource.HOLTER, false);
		ESHolterDocument esHolter = new ESHolterDocument();
		BeanUtil.map(holter, esHolter);
		esClinicalNotesService.addHolter(esHolter);
		Response<Holter> response = new Response<Holter>();
		response.setData(holter);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_NOSE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_NOSE, notes = PathProxy.ClinicalNotesUrls.ADD_PC_NOSE)
	public Response<PresentingComplaintNose> addEditPCNose(@RequestBody PresentingComplaintNose request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcNose())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintNose presentingComplaintNose = clinicalNotesService.addEditPCNose(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_NOSE, false);
		ESPresentingComplaintNoseDocument esPresentingComplaintNose = new ESPresentingComplaintNoseDocument();
		BeanUtil.map(presentingComplaintNose, esPresentingComplaintNose);
		esClinicalNotesService.addPCNose(esPresentingComplaintNose);
		Response<PresentingComplaintNose> response = new Response<PresentingComplaintNose>();
		response.setData(presentingComplaintNose);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_EARS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_EARS, notes = PathProxy.ClinicalNotesUrls.ADD_PC_EARS)
	public Response<PresentingComplaintEars> addEditPCEars(@RequestBody PresentingComplaintEars request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcEars())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintEars presentingComplaintEars = clinicalNotesService.addEditPCEars(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_EARS, false);
		ESPresentingComplaintEarsDocument esPresentingComplaintEars = new ESPresentingComplaintEarsDocument();
		BeanUtil.map(presentingComplaintEars, esPresentingComplaintEars);
		esClinicalNotesService.addPCEars(esPresentingComplaintEars);
		Response<PresentingComplaintEars> response = new Response<PresentingComplaintEars>();
		response.setData(presentingComplaintEars);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_THROAT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_THROAT, notes = PathProxy.ClinicalNotesUrls.ADD_PC_THROAT)
	public Response<PresentingComplaintThroat> addEditPCThroat(@RequestBody PresentingComplaintThroat request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcThroat())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintThroat presentingComplaintThroat = clinicalNotesService.addEditPCThroat(request);
		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_THROAT, false);
		ESPresentingComplaintThroatDocument esPresentingComplaintThroat = new ESPresentingComplaintThroatDocument();
		BeanUtil.map(presentingComplaintThroat, esPresentingComplaintThroat);
		esClinicalNotesService.addPCThroat(esPresentingComplaintThroat);
		Response<PresentingComplaintThroat> response = new Response<PresentingComplaintThroat>();
		response.setData(presentingComplaintThroat);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PC_ORAL_CAVITY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PC_ORAL_CAVITY, notes = PathProxy.ClinicalNotesUrls.ADD_PC_ORAL_CAVITY)
	public Response<PresentingComplaintOralCavity> addEditPCOralCavity(@RequestBody PresentingComplaintOralCavity request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPcOralCavity())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentingComplaintOralCavity presentingComplaintOralCavity = clinicalNotesService.addEditPCOralCavity(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.PC_ORAL_CAVITY, false);
		ESPresentingComplaintOralCavityDocument esPresentingComplaintOralCavity = new ESPresentingComplaintOralCavityDocument();
		BeanUtil.map(presentingComplaintOralCavity, esPresentingComplaintOralCavity);
		esClinicalNotesService.addPCOralCavity(esPresentingComplaintOralCavity);
		Response<PresentingComplaintOralCavity> response = new Response<PresentingComplaintOralCavity>();
		response.setData(presentingComplaintOralCavity);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_NOSE_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NOSE_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_NOSE_EXAM)
	public Response<NoseExamination> addEditNoseExam(@RequestBody NoseExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNoseExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		NoseExamination noseExamination = clinicalNotesService.addEditNoseExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.NOSE_EXAM, false);
		ESNoseExaminationDocument esNoseExaminationDocument = new ESNoseExaminationDocument();
		BeanUtil.map(noseExamination, esNoseExaminationDocument);
		esClinicalNotesService.addNoseExam(esNoseExaminationDocument);
		Response<NoseExamination> response = new Response<NoseExamination>();
		response.setData(noseExamination);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_NECK_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NECK_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_NECK_EXAM)
	public Response<NeckExamination> addEditNeckExam(@RequestBody NeckExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNeckExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		NeckExamination neckExamination = clinicalNotesService.addEditNeckExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.NECK_EXAM, false);
		ESNeckExaminationDocument esNeckExaminationDocument = new ESNeckExaminationDocument();
		BeanUtil.map(neckExamination, esNeckExaminationDocument);
		esClinicalNotesService.addNeckExam(esNeckExaminationDocument);
		Response<NeckExamination> response = new Response<NeckExamination>();
		response.setData(neckExamination);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_EARS_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_EARS_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_EARS_EXAM)
	public Response<EarsExamination> addEditEarsExam(@RequestBody EarsExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getEarsExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		EarsExamination earsExamination = clinicalNotesService.addEditEarsExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.EARS_EXAM, false);
		ESEarsExaminationDocument esEarsExaminationDocument = new ESEarsExaminationDocument();
		BeanUtil.map(earsExamination, esEarsExaminationDocument);
		esClinicalNotesService.addEarsExam(esEarsExaminationDocument);
		Response<EarsExamination> response = new Response<EarsExamination>();
		response.setData(earsExamination);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_ORAL_CAVITY_THROAT_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_ORAL_CAVITY_THROAT_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_ORAL_CAVITY_THROAT_EXAM)
	public Response<OralCavityAndThroatExamination> addEditOralCavityThroatExam(
			@RequestBody OralCavityAndThroatExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getOralCavityThroatExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		OralCavityAndThroatExamination oralCavityAndThroatExamination = clinicalNotesService
				.addEditOralCavityThroatExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.ORAL_CAVITY_THROAT_EXAM,
				false);
		ESOralCavityAndThroatExaminationDocument esOralCavityAndThroatExaminationDocument = new ESOralCavityAndThroatExaminationDocument();
		BeanUtil.map(oralCavityAndThroatExamination, esOralCavityAndThroatExaminationDocument);
		esClinicalNotesService.addOralCavityThroatExam(esOralCavityAndThroatExaminationDocument);
		Response<OralCavityAndThroatExamination> response = new Response<OralCavityAndThroatExamination>();
		response.setData(oralCavityAndThroatExamination);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_INDIRECT_LARYGOSCOPY_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INDIRECT_LARYGOSCOPY_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_INDIRECT_LARYGOSCOPY_EXAM)
	public Response<IndirectLarygoscopyExamination> addEditIndirectLarygosccopyExam(
			@RequestBody IndirectLarygoscopyExamination request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getIndirectLarygoscopyExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		IndirectLarygoscopyExamination indirectLarygoscopyExamination = clinicalNotesService
				.addEditIndirectLarygoscopyExam(request);

		transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.INDIRECT_LARYGOSCOPY_EXAM,
				false);
		ESIndirectLarygoscopyExaminationDocument esIndirectLarygoscopyExaminationDocument = new ESIndirectLarygoscopyExaminationDocument();
		BeanUtil.map(indirectLarygoscopyExamination, esIndirectLarygoscopyExaminationDocument);
		esClinicalNotesService.addIndirectLarygoscopyExam(esIndirectLarygoscopyExaminationDocument);
		Response<IndirectLarygoscopyExamination> response = new Response<IndirectLarygoscopyExamination>();
		response.setData(indirectLarygoscopyExamination);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PROVISIONAL_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.DELETE_PROVISIONAL_DIAGNOSIS)
	public Response<ProvisionalDiagnosis> deleteProvisionalDiagnosis(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
		    @RequestParam(required = false, value ="discarded", defaultValue="true")Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ProvisionalDiagnosis provisionalDiagnosis = clinicalNotesService.deleteProvisionalDiagnosis(id, doctorId,
				locationId, hospitalId, discarded);

		if (provisionalDiagnosis != null) {
			transactionalManagementService.addResource(new ObjectId(provisionalDiagnosis.getId()),
					Resource.PROVISIONAL_DIAGNOSIS, false);
			ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
			BeanUtil.map(provisionalDiagnosis, esProvisionalDiagnosis);
			esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
		}
		Response<ProvisionalDiagnosis> response = new Response<ProvisionalDiagnosis>();
		response.setData(provisionalDiagnosis);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT)
	public Response<PresentComplaint> deletePresentComplaint(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded",defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentComplaint presentComplaint = clinicalNotesService.deletePresentComplaint(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentComplaint != null) {
			transactionalManagementService.addResource(new ObjectId(presentComplaint.getId()),
					Resource.PRESENT_COMPLAINT, false);
			ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
			BeanUtil.map(presentComplaint, esPresentComplaint);
			esClinicalNotesService.addPresentComplaint(esPresentComplaint);
		}
		Response<PresentComplaint> response = new Response<PresentComplaint>();
		response.setData(presentComplaint);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT_HISTORY)
	public Response<PresentComplaintHistory> deletePresentComplaintHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentComplaintHistory presentComplaintHistory = clinicalNotesService.deletePresentComplaintHistory(id,
				doctorId, locationId, hospitalId, discarded);

		if (presentComplaintHistory != null) {
			transactionalManagementService.addResource(new ObjectId(presentComplaintHistory.getId()),
					Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
			ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
			BeanUtil.map(presentComplaintHistory, esPresentComplaintHistory);
			esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
		}
		Response<PresentComplaintHistory> response = new Response<PresentComplaintHistory>();
		response.setData(presentComplaintHistory);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_GENERAL_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_GENERAL_EXAM)
	public Response<GeneralExam> deleteGeneralExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		GeneralExam generalExam = clinicalNotesService.deleteGeneralExam(id, doctorId, locationId, hospitalId,
				discarded);

		if (generalExam != null) {
			transactionalManagementService.addResource(new ObjectId(generalExam.getId()), Resource.GENERAL_EXAMINATION,
					false);
			ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
			BeanUtil.map(generalExam, esGeneralExam);
			esClinicalNotesService.addGeneralExam(esGeneralExam);
		}
		Response<GeneralExam> response = new Response<GeneralExam>();
		response.setData(generalExam);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_SYSTEM_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_SYSTEM_EXAM)
	public Response<SystemExam> deleteSystemExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		SystemExam systemExam = clinicalNotesService.deleteSystemExam(id, doctorId, locationId, hospitalId, discarded);

		if (systemExam != null) {
			transactionalManagementService.addResource(new ObjectId(systemExam.getId()), Resource.SYSTEMIC_EXAMINATION,
					false);
			ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
			BeanUtil.map(systemExam, esSystemExam);
			esClinicalNotesService.addSystemExam(esSystemExam);
		}
		Response<SystemExam> response = new Response<SystemExam>();
		response.setData(systemExam);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_MENSTRUAL_HISTORY, notes = PathProxy.ClinicalNotesUrls.DELETE_MENSTRUAL_HISTORY)
	public Response<MenstrualHistory> deleteMenstrualHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		MenstrualHistory menstrualHistory = clinicalNotesService.deleteMenstrualHistory(id, doctorId, locationId,
				hospitalId, discarded);

		if (menstrualHistory != null) {
			transactionalManagementService.addResource(new ObjectId(menstrualHistory.getId()),
					Resource.MENSTRUAL_HISTORY, false);
			ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
			BeanUtil.map(menstrualHistory, esMenstrualHistory);
			esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
		}
		Response<MenstrualHistory> response = new Response<MenstrualHistory>();
		response.setData(menstrualHistory);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INDICATION_OF_USG, notes = PathProxy.ClinicalNotesUrls.DELETE_INDICATION_OF_USG)
	public Response<IndicationOfUSG> deleteIndicationOfUSG(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		IndicationOfUSG indicationOfUSG = clinicalNotesService.deleteIndicationOfUSG(id, doctorId, locationId,
				hospitalId, discarded);

		if (indicationOfUSG != null) {
			transactionalManagementService.addResource(new ObjectId(indicationOfUSG.getId()),
					Resource.INDICATION_OF_USG, false);
			ESIndicationOfUSGDocument esIndicationOfUSG = new ESIndicationOfUSGDocument();
			BeanUtil.map(indicationOfUSG, esIndicationOfUSG);
			esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG);
		}
		Response<IndicationOfUSG> response = new Response<IndicationOfUSG>();
		response.setData(indicationOfUSG);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PA)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PA, notes = PathProxy.ClinicalNotesUrls.DELETE_PA)
	public Response<PA> deletePA(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PA pa = clinicalNotesService.deletePA(id, doctorId, locationId, hospitalId, discarded);

		if (pa != null) {
			transactionalManagementService.addResource(new ObjectId(pa.getId()), Resource.PA, false);
			ESPADocument espa = new ESPADocument();
			BeanUtil.map(pa, espa);
			esClinicalNotesService.addPA(espa);
		}
		Response<PA> response = new Response<PA>();
		response.setData(pa);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PV)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PV, notes = PathProxy.ClinicalNotesUrls.DELETE_PV)
	public Response<PV> deletePV(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PV pv = clinicalNotesService.deletePV(id, doctorId, locationId, hospitalId, discarded);

		if (pv != null) {
			transactionalManagementService.addResource(new ObjectId(pv.getId()), Resource.PV, false);
			ESPVDocument espv = new ESPVDocument();
			BeanUtil.map(pv, espv);
			esClinicalNotesService.addPV(espv);
		}
		Response<PV> response = new Response<PV>();
		response.setData(pv);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PS, notes = PathProxy.ClinicalNotesUrls.DELETE_PS)
	public Response<PS> deletePS(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PS ps = clinicalNotesService.deletePS(id, doctorId, locationId, hospitalId, discarded);

		if (ps != null) {
			transactionalManagementService.addResource(new ObjectId(ps.getId()), Resource.PS, false);
			ESPSDocument esps = new ESPSDocument();
			BeanUtil.map(ps, esps);
			esClinicalNotesService.addPS(esps);
		}
		Response<PS> response = new Response<PS>();
		response.setData(ps);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_OBSTETRIC_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_OBSTETRIC_HISTORY, notes = PathProxy.ClinicalNotesUrls.DELETE_OBSTETRIC_HISTORY)
	public Response<ObstetricHistory> deleteObstetricHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ObstetricHistory obstetricHistory = clinicalNotesService.deleteObstetricHistory(id, doctorId, locationId,
				hospitalId, discarded);

		if (obstetricHistory != null) {
			transactionalManagementService.addResource(new ObjectId(obstetricHistory.getId()),
					Resource.OBSTETRIC_HISTORY, false);
			ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
			BeanUtil.map(obstetricHistory, esObstetricHistory);
			esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
		}
		Response<ObstetricHistory> response = new Response<ObstetricHistory>();
		response.setData(obstetricHistory);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_X_RAY_DETAILS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_X_RAY_DETAILS, notes = PathProxy.ClinicalNotesUrls.DELETE_X_RAY_DETAILS)
	public Response<XRayDetails> deleteXRayDetails(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		XRayDetails xRayDetails = clinicalNotesService.deleteXRayDetails(id, doctorId, locationId, hospitalId,
				discarded);

		if (xRayDetails != null) {
			transactionalManagementService.addResource(new ObjectId(xRayDetails.getId()), Resource.XRAY, false);
			ESXRayDetailsDocument esxRayDetails = new ESXRayDetailsDocument();
			BeanUtil.map(xRayDetails, esxRayDetails);
			esClinicalNotesService.addXRayDetails(esxRayDetails);
		}
		Response<XRayDetails> response = new Response<XRayDetails>();
		response.setData(xRayDetails);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_ECG_DETAILS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_ECG_DETAILS, notes = PathProxy.ClinicalNotesUrls.DELETE_ECG_DETAILS)
	public Response<ECGDetails> deleteECGDetails(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ECGDetails ecgDetails = clinicalNotesService.deleteECGDetails(id, doctorId, locationId, hospitalId, discarded);

		if (ecgDetails != null) {
			transactionalManagementService.addResource(new ObjectId(ecgDetails.getId()), Resource.ECG, false);
			ESECGDetailsDocument esecgDetails = new ESECGDetailsDocument();
			BeanUtil.map(ecgDetails, esecgDetails);
			esClinicalNotesService.addECGDetails(esecgDetails);
		}
		Response<ECGDetails> response = new Response<ECGDetails>();
		response.setData(ecgDetails);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_ECHO)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_ECHO, notes = PathProxy.ClinicalNotesUrls.DELETE_ECHO)
	public Response<Echo> deleteEcho(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Echo echo = clinicalNotesService.deleteEcho(id, doctorId, locationId, hospitalId, discarded);

		if (echo != null) {
			transactionalManagementService.addResource(new ObjectId(echo.getId()), Resource.ECHO, false);
			ESEchoDocument esEcho = new ESEchoDocument();
			BeanUtil.map(echo, esEcho);
			esClinicalNotesService.addEcho(esEcho);
		}
		Response<Echo> response = new Response<Echo>();
		response.setData(echo);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_HOLTER)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_HOLTER, notes = PathProxy.ClinicalNotesUrls.DELETE_HOLTER)
	public Response<Holter> deleteHolter(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Holter holter = clinicalNotesService.deleteHolter(id, doctorId, locationId, hospitalId, discarded);

		if (holter != null) {
			transactionalManagementService.addResource(new ObjectId(holter.getId()), Resource.HOLTER, false);
			ESHolterDocument esHolter = new ESHolterDocument();
			BeanUtil.map(holter, esHolter);
			esClinicalNotesService.addHolter(esHolter);
		}
		Response<Holter> response = new Response<Holter>();
		response.setData(holter);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_EDIT_EYE_OBSERVATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_EDIT_EYE_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.ADD_EDIT_EYE_OBSERVATION)
	public Response<EyeObservation> addEditEyeObservation(@RequestBody EyeObservation eyeObservation) {
		if (eyeObservation == null) {
			throw new BusinessException(ServiceError.InvalidInput);
		}
		eyeObservation = ophthalmologyService.addEditEyeObservation(eyeObservation);
		Response<EyeObservation> response = new Response<>();
		response.setData(eyeObservation);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_EYE_OBSERVATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_EYE_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.DELETE_EYE_OBSERVATION)

	public Response<EyeObservation> deleteEyeObservation(@PathVariable("id") String id,
			@RequestParam(required = false, value ="discarded", defaultValue="true")Boolean discarded) {
		EyeObservation eyeObservation = null;
		if (id == null || id.isEmpty() || discarded == null) {
			throw new BusinessException(ServiceError.InvalidInput);
		}
		eyeObservation = ophthalmologyService.deleteEyeObservation(id, discarded);
		Response<EyeObservation> response = new Response<>();
		response.setData(eyeObservation);
		return response;
	}

	
	@GetMapping(value = PathProxy.ClinicalNotesUrls.GET_EYE_OBSERVATIONS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_EYE_OBSERVATIONS, notes = PathProxy.ClinicalNotesUrls.GET_EYE_OBSERVATIONS)
	public Response<EyeObservation> getEyeObservations(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "patientId") String patientId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@DefaultValue("false") @RequestParam(value = "isOTPVerified") Boolean isOTPVerified) {
		List<EyeObservation> eyeObservations = null;
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input.");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
		}
		eyeObservations = ophthalmologyService.getEyeObservations(page, size, doctorId, locationId, hospitalId,
				patientId, updatedTime, discarded, isOTPVerified);
		Response<EyeObservation> response = new Response<>();
		response.setDataList(eyeObservations);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PROCEDURE_NOTE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PROCEDURE_NOTE, notes = PathProxy.ClinicalNotesUrls.DELETE_PROCEDURE_NOTE)
	public Response<ProcedureNote> deleteProcedureNote(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ProcedureNote procedureNote = clinicalNotesService.deleteProcedureNote(id, doctorId, locationId, hospitalId,
				discarded);

		if (procedureNote != null) {
			transactionalManagementService.addResource(new ObjectId(procedureNote.getId()), Resource.PROCEDURE_NOTE,
					false);
			ESProcedureNoteDocument esProcedureNoteDocument = new ESProcedureNoteDocument();
			BeanUtil.map(procedureNote, esProcedureNoteDocument);
			esClinicalNotesService.addProcedureNote(esProcedureNoteDocument);
		}
		Response<ProcedureNote> response = new Response<ProcedureNote>();
		response.setData(procedureNote);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_NOSE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_NOSE, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_NOSE)
	public Response<PresentingComplaintNose> deletePCNose(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintNose presentingComplaintNose = clinicalNotesService.deletePCNose(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentingComplaintNose != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintNose.getId()), Resource.PC_NOSE,
					false);
			ESPresentingComplaintNoseDocument esPresentingComplaintNoseDocument = new ESPresentingComplaintNoseDocument();
			BeanUtil.map(presentingComplaintNose, esPresentingComplaintNoseDocument);
			esClinicalNotesService.addPCNose(esPresentingComplaintNoseDocument);
		}
		Response<PresentingComplaintNose> response = new Response<PresentingComplaintNose>();
		response.setData(presentingComplaintNose);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_EARS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_EARS, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_EARS)
	public Response<PresentingComplaintEars> deletePCEars(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintEars presentingComplaintEars = clinicalNotesService.deletePCEars(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentingComplaintEars != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintEars.getId()), Resource.PC_EARS,
					false);
			ESPresentingComplaintEarsDocument esPresentingComplaintEarsDocument = new ESPresentingComplaintEarsDocument();
			BeanUtil.map(presentingComplaintEars, esPresentingComplaintEarsDocument);
			esClinicalNotesService.addPCEars(esPresentingComplaintEarsDocument);
		}
		Response<PresentingComplaintEars> response = new Response<PresentingComplaintEars>();
		response.setData(presentingComplaintEars);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_ORAL_CAVITY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_ORAL_CAVITY, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_ORAL_CAVITY)
	public Response<PresentingComplaintOralCavity> deletePCOralCavity(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintOralCavity presentingComplaintOralCavity = clinicalNotesService.deletePCOralCavity(id,
				doctorId, locationId, hospitalId, discarded);

		if (presentingComplaintOralCavity != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintOralCavity.getId()),
					Resource.PC_ORAL_CAVITY, false);
			ESPresentingComplaintOralCavityDocument esPresentingComplaintOralCavityDocument = new ESPresentingComplaintOralCavityDocument();
			BeanUtil.map(presentingComplaintOralCavity, esPresentingComplaintOralCavityDocument);
			esClinicalNotesService.addPCOralCavity(esPresentingComplaintOralCavityDocument);
		}
		Response<PresentingComplaintOralCavity> response = new Response<PresentingComplaintOralCavity>();
		response.setData(presentingComplaintOralCavity);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_NOSE_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NOSE_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_NOSE_EXAM)
	public Response<NoseExamination> deleteNoseExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		NoseExamination noseExamination = clinicalNotesService.deleteNoseExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (noseExamination != null) {
			transactionalManagementService.addResource(new ObjectId(noseExamination.getId()), Resource.NOSE_EXAM,
					false);
			ESNoseExaminationDocument esNoseExaminationDocument = new ESNoseExaminationDocument();
			BeanUtil.map(noseExamination, esNoseExaminationDocument);
			esClinicalNotesService.addNoseExam(esNoseExaminationDocument);
		}
		Response<NoseExamination> response = new Response<NoseExamination>();
		response.setData(noseExamination);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_NECK_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NECK_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_NECK_EXAM)
	public Response<NeckExamination> deleteNeckExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		NeckExamination neckExamination = clinicalNotesService.deleteNeckExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (neckExamination != null) {
			transactionalManagementService.addResource(new ObjectId(neckExamination.getId()), Resource.NECK_EXAM,
					false);
			ESNeckExaminationDocument esNeckExaminationDocument = new ESNeckExaminationDocument();
			BeanUtil.map(neckExamination, esNeckExaminationDocument);
			esClinicalNotesService.addNeckExam(esNeckExaminationDocument);
		}
		Response<NeckExamination> response = new Response<NeckExamination>();
		response.setData(neckExamination);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_EARS_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_EARS_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_EARS_EXAM)
	public Response<EarsExamination> deleteEarsExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		EarsExamination EarsExamination = clinicalNotesService.deleteEarsExam(id, doctorId, locationId, hospitalId,
				discarded);
		if (EarsExamination != null) {
			transactionalManagementService.addResource(new ObjectId(EarsExamination.getId()), Resource.EARS_EXAM,
					false);
			ESEarsExaminationDocument esEarsExaminationDocument = new ESEarsExaminationDocument();
			BeanUtil.map(EarsExamination, esEarsExaminationDocument);
			esClinicalNotesService.addEarsExam(esEarsExaminationDocument);
		}
		Response<EarsExamination> response = new Response<EarsExamination>();
		response.setData(EarsExamination);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_ORAL_CAVITY_THROAT_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_ORAL_CAVITY_THROAT_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_ORAL_CAVITY_THROAT_EXAM)
	public Response<OralCavityAndThroatExamination> deleteOralCavityThroatExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		OralCavityAndThroatExamination oralCavityAndThroatExamination = clinicalNotesService
				.deleteOralCavityThroatExam(id, doctorId, locationId, hospitalId, discarded);
		if (oralCavityAndThroatExamination != null) {
			transactionalManagementService.addResource(new ObjectId(oralCavityAndThroatExamination.getId()),
					Resource.ORAL_CAVITY_THROAT_EXAM, false);
			ESOralCavityAndThroatExaminationDocument esOralCavityAndThroatExaminationDocument = new ESOralCavityAndThroatExaminationDocument();
			BeanUtil.map(oralCavityAndThroatExamination, esOralCavityAndThroatExaminationDocument);
			esClinicalNotesService.addOralCavityThroatExam(esOralCavityAndThroatExaminationDocument);
		}
		Response<OralCavityAndThroatExamination> response = new Response<OralCavityAndThroatExamination>();
		response.setData(oralCavityAndThroatExamination);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_INDIRECT_LARYGOSCOPY_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INDIRECT_LARYGOSCOPY_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_INDIRECT_LARYGOSCOPY_EXAM)
	public Response<IndirectLarygoscopyExamination> deleteIndirectlarygoscopyExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		IndirectLarygoscopyExamination indirectLarygoscopyExamination = clinicalNotesService
				.deleteIndirectLarygoscopyExam(id, doctorId, locationId, hospitalId, discarded);
		if (indirectLarygoscopyExamination != null) {
			transactionalManagementService.addResource(new ObjectId(indirectLarygoscopyExamination.getId()),
					Resource.INDIRECT_LARYGOSCOPY_EXAM, false);
			ESIndirectLarygoscopyExaminationDocument esIndirectLarygoscopyExaminationDocument = new ESIndirectLarygoscopyExaminationDocument();
			BeanUtil.map(indirectLarygoscopyExamination, esIndirectLarygoscopyExaminationDocument);
			esClinicalNotesService.addIndirectLarygoscopyExam(esIndirectLarygoscopyExaminationDocument);
		}
		Response<IndirectLarygoscopyExamination> response = new Response<IndirectLarygoscopyExamination>();
		response.setData(indirectLarygoscopyExamination);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PC_THROAT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PC_THROAT, notes = PathProxy.ClinicalNotesUrls.DELETE_PC_THROAT)
	public Response<PresentingComplaintThroat> deletePCThroat(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentingComplaintThroat presentingComplaintThroat = clinicalNotesService.deletePCThroat(id, doctorId,
				locationId, hospitalId, discarded);
		if (presentingComplaintThroat != null) {
			transactionalManagementService.addResource(new ObjectId(presentingComplaintThroat.getId()),
					Resource.PC_THROAT, false);
			ESPresentingComplaintThroatDocument esPresentingComplaintThroatDocument = new ESPresentingComplaintThroatDocument();
			BeanUtil.map(presentingComplaintThroat, esPresentingComplaintThroatDocument);
			esClinicalNotesService.addPCThroat(esPresentingComplaintThroatDocument);
		}
		Response<PresentingComplaintThroat> response = new Response<PresentingComplaintThroat>();
		response.setData(presentingComplaintThroat);
		return response;
	}

	
	@GetMapping(value = PathProxy.ClinicalNotesUrls.GET_DIAGNOSES_BY_SPECIALITY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_DIAGNOSES_BY_SPECIALITY, notes = PathProxy.ClinicalNotesUrls.GET_DIAGNOSES_BY_SPECIALITY)
	public Response<Diagnoses> getServicesBySpeciality(@RequestParam("speciality") String speciality,
			@RequestParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(speciality)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<Diagnoses> diagnoses = clinicalNotesService.getDiagnosesListBySpeciality(speciality, searchTerm);

		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setDataList(diagnoses);
		return response;

	}
	
	
	@GetMapping(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES_WEB)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES_WEB, notes = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES_WEB)
	public Response<Boolean> emailClinicalNotesForWeb(@PathVariable(value = "clinicalNotesId") String clinicalNotesId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, emailAddress)) {
			logger.warn(
					"Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		clinicalNotesService.emailClinicalNotes(clinicalNotesId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.DOWNLOAD_MULTIPLE_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DOWNLOAD_MULTIPLE_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.DOWNLOAD_MULTIPLE_CLINICAL_NOTES)
	public Response<String> downloadMultipleClinicalNotes(@RequestBody ListIdrequest request) {
		
		if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Response<String> response = new Response<String>();
		response.setData(clinicalNotesService.downloadMultipleClinicalNotes(request.getIds()));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.ClinicalNotesUrls.EMAIL_MULTIPLE_CLINICAL_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.EMAIL_MULTIPLE_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.EMAIL_MULTIPLE_CLINICAL_NOTES)
	public Response<Boolean> emailMultipleClinicalNotes(@RequestBody ListIdrequest request) {

		if (request == null || request.getIds() == null || request.getIds().isEmpty() || DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,"Invalid Input");
		}
		clinicalNotesService.emailMultipleClinicalNotes(request.getIds(), request.getEmailAddress());

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

}
