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

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.CLINICAL_NOTES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CLINICAL_NOTES_BASE_URL, description = "Endpoint for clinical notes")
public class ClinicalNotesApi {

    private static Logger logger = Logger.getLogger(ClinicalNotesApi.class.getName());

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

    @Value(value = "${image.path}")
    private String imagePath;

    @Path(value = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE, notes = PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
    public Response<ClinicalNotes> addNotes(ClinicalNotesAddRequest request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    }
	
    ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request);

	if (clinicalNotes != null) {
	    String visitId = patientTrackService.addRecord(clinicalNotes, VisitedFor.CLINICAL_NOTES, request.getVisitId());
	    clinicalNotes.setVisitId(visitId);
	}
	if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
	    clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
	}
	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
    @PUT
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
    public Response<ClinicalNotes> editNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId, ClinicalNotesEditRequest request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(clinicalNotesId, request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
    public Response<ClinicalNotes> deleteNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

    ClinicalNotes clinicalNotes = clinicalNotesService.deleteNote(clinicalNotesId, discarded);
	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
    @GET
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID, notes = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
    public Response<ClinicalNotes> getNotesById(@PathParam(value = "clinicalNotesId") String clinicalNotesId) {
	ClinicalNotes clinicalNotes = clinicalNotesService.getNotesById(clinicalNotesId);
	if (clinicalNotes.getDiagrams() != null && !clinicalNotes.getDiagrams().isEmpty()) {
	    clinicalNotes.setDiagrams(getFinalDiagrams(clinicalNotes.getDiagrams()));
	}
	Response<ClinicalNotes> response = new Response<ClinicalNotes>();
	response.setData(clinicalNotes);
	return response;
    }

    @GET
    @ApiOperation(value = "GET_CLINICAL_NOTES", notes = "GET_CLINICAL_NOTES")
    public Response<ClinicalNotes> getNotes(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
	    @QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
	    @QueryParam(value = "patientId") String patientId, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {
	
	    if (DPDoctorUtils.anyStringEmpty(doctorId)) {
	    	    logger.warn("Invalid Input");
	    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	    }
		
    	List<ClinicalNotes> clinicalNotes =  clinicalNotesService.getClinicalNotes(page, size, doctorId, locationId, hospitalId, patientId, updatedTime,
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

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_PATIENT_ID)
    @GET
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_PATIENT_ID, notes = PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_PATIENT_ID)
    public Response<ClinicalNotes> getNotes(@PathParam(value = "patientId") String patientId, @QueryParam("page") int page, @QueryParam("size") int size,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, 
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
    public Response<Complaint> addComplaint(Complaint request) {
    	
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getComplaint())) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
    public Response<Observation> addObservation(Observation request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getObservation())) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION, notes = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
    public Response<Investigation> addInvestigation(Investigation request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getInvestigation())) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
    public Response<Diagnoses> addDiagnosis(Diagnoses request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getDiagnosis())) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_NOTES)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NOTES, notes = PathProxy.ClinicalNotesUrls.ADD_NOTES)
    public Response<Notes> addNotes(Notes request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getNote())) {
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

    @Path(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
    @POST
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM, notes = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
    public Response<Diagram> addDiagram(Diagram request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId()) || request.getDiagram() == null) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    }
	
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

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
    public Response<Complaint> deleteComplaint(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
    	    logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	Complaint complaint = clinicalNotesService.deleteComplaint(id, doctorId, locationId, hospitalId, discarded);
	
    	if(complaint != null){
			transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaint, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
		}
	Response<Complaint> response = new Response<Complaint>();
	response.setData(complaint);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
    public Response<Observation> deleteObservation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
    	    logger.warn("Observation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Observation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	Observation observation = clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId, discarded);
    	if(observation != null){
			transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observation, esObservations);
			esClinicalNotesService.addObservations(esObservations);
		}
	    
	Response<Observation> response = new Response<Observation>();
	response.setData(observation);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION, notes = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
    public Response<Investigation> deleteInvestigation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
    	    logger.warn("Investigation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Investigation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	Investigation investigation = clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId, discarded);
    	if(investigation != null){
	    	transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION, false);
	    	ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
	    	BeanUtil.map(investigation, esInvestigations);
	    	esClinicalNotesService.addInvestigations(esInvestigations);
	    }

	Response<Investigation> response = new Response<Investigation>();
	response.setData(investigation);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
    public Response<Diagnoses> deleteDiagnosis(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
    	    logger.warn("Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	Diagnoses diagnoses = clinicalNotesService.deleteDiagnosis(id, doctorId, locationId, hospitalId, discarded);
    	if(diagnoses != null){
			transactionalManagementService.addResource(new ObjectId(diagnoses.getId()), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnoses, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
		}
	Response<Diagnoses> response = new Response<Diagnoses>();
	response.setData(diagnoses);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE, notes = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
    public Response<Notes> deleteNote(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
    	    logger.warn("Note Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Note Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	Notes notes = clinicalNotesService.deleteNotes(id, doctorId, locationId, hospitalId, discarded);
    	if(notes != null){
			transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
			ESNotesDocument esNotes = new ESNotesDocument();
			BeanUtil.map(notes, esNotes);
			esClinicalNotesService.addNotes(esNotes);
		}
	Response<Notes> response = new Response<Notes>();
	response.setData(notes);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
    @DELETE
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM, notes = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
    public Response<Diagram> deleteDiagram(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
    	    logger.warn("Diagram Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Diagram, Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
    	}
    	Diagram diagram = clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId, discarded);
    	if(diagram != null){
			ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
			BeanUtil.map(diagram, esDiagrams);
			esClinicalNotesService.addDiagrams(esDiagrams);		
		}
	Response<Diagram> response = new Response<Diagram>();
	response.setData(diagram);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS)
    @GET
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS, notes = PathProxy.ClinicalNotesUrls.GET_CINICAL_ITEMS)
    public Response<Object> getClinicalItems(@PathParam("type") String type, @PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(type, range, doctorId)) {
	    logger.warn("Invalid Input.");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
	}
	List<?> clinicalItems = clinicalNotesService.getClinicalItems(type, range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, null);
	if (clinicalItems != null && !clinicalItems.isEmpty() && ClinicalItems.DIAGRAMS.getType().equalsIgnoreCase(type)) {
	    for (Object clinicalItem : clinicalItems) {
		((Diagram) clinicalItem).setDiagramUrl(getFinalImageURL(((Diagram) clinicalItem).getDiagramUrl()));
	    }
	}
	Response<Object> response = new Response<Object>();
	response.setDataList(clinicalItems);
	return response;
    }

    @Path(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES)
    @GET
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.EMAIL_CLINICAL_NOTES)
    public Response<Boolean> emailClinicalNotes(@PathParam(value = "clinicalNotesId") String clinicalNotesId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "emailAddress") String emailAddress) {

	if (DPDoctorUtils.anyStringEmpty(clinicalNotesId, doctorId, locationId, hospitalId, emailAddress)) {
	    logger.warn("Invalid Input. Clinical Notes Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
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

    @Path(value = PathProxy.ClinicalNotesUrls.DOWNLOAD_CLINICAL_NOTES)
    @GET
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.DOWNLOAD_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.DOWNLOAD_CLINICAL_NOTES)
    public Response<String> downloadClinicalNotes(@PathParam("clinicalNotesId") String clinicalNotesId) {
    	
    	Response<String> response = new Response<String>();
    	response.setData(clinicalNotesService.getClinicalNotesFile(clinicalNotesId));
    	return response;
    }
    
    @Path(value = PathProxy.ClinicalNotesUrls.UPDATE_QUERY_CLINICAL_NOTES)
    @GET
    @ApiOperation(value = PathProxy.ClinicalNotesUrls.UPDATE_QUERY_CLINICAL_NOTES, notes = PathProxy.ClinicalNotesUrls.UPDATE_QUERY_CLINICAL_NOTES)
    public Response<Boolean> updateQuery() {

      	Response<Boolean> response = new Response<Boolean>();
		response.setData(clinicalNotesService.updateQuery());
		return response;
    }
}
