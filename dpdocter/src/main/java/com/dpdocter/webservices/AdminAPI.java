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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.Speciality;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.ADMIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "Endpoint for admin")
public class AdminAPI {

	private static Logger logger = Logger.getLogger(AdminAPI.class.getName());
	
	@Autowired
	AdminServices adminServices;
	
	@Autowired
	ClinicalNotesService clinicalNotesService;

	@Autowired
	HistoryServices historyServices;

	@Autowired
	ESMasterService esMasterService;

    @Autowired
    private ESClinicalNotesService esClinicalNotesService;

    @Autowired
    private TransactionalManagementService transactionalManagementService;
    
    @Autowired
    private ESPrescriptionService esPrescriptionService;

    @Autowired
    private PrescriptionServices prescriptionServices;
    
    @Autowired
    private DoctorContactUsService doctorContactUsService;

    @Value(value = "${image.path}")
    private String imagePath;
	
	@Path(value = PathProxy.AdminUrls.GET_HOSPITALS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_HOSPITALS, notes = PathProxy.AdminUrls.GET_HOSPITALS)
	public Response<Hospital> getHospitals(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<Hospital> hospitals = adminServices.getHospitals(page, size);
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(hospitals);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_CLINICS_AND_LABS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_CLINICS_AND_LABS, notes = PathProxy.AdminUrls.GET_CLINICS_AND_LABS)
	public Response<Location> getClinics(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "isClinic") @DefaultValue("false") Boolean isClinic, @QueryParam(value = "isLab") @DefaultValue("false") Boolean isLab,
			@QueryParam(value = "searchTerm") String searchTerm){
		
		List<Location> locations = adminServices.getClinics(page, size, hospitalId, isClinic, isLab, searchTerm);
		
		Response<Location> response = new Response<Location>();
		response.setDataList(locations);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_DOCTORS, notes = PathProxy.AdminUrls.GET_DOCTORS)
	public Response<DoctorResponse> getDoctors(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "state") String state, @QueryParam(value = "searchTerm") String searchTerm){
		
		List<DoctorResponse> doctorResponses = adminServices.getDoctors(page, size, locationId, state, searchTerm);
		
		Response<DoctorResponse> response = new Response<DoctorResponse>();
		response.setDataList(doctorResponses);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.ADD_RESUMES)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.ADD_RESUMES, notes = PathProxy.AdminUrls.ADD_RESUMES)
	public Response<Resume> addResumes(Resume request){
	    if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getName(), request.getMobileNumber()) || request.getFile() == null) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	    }

		Resume resume = adminServices.addResumes(request);
		resume.setPath(getFinalImageURL(resume.getPath()));
		Response<Resume> response = new Response<Resume>();
		response.setData(resume);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_RESUMES)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_RESUMES, notes = PathProxy.AdminUrls.GET_RESUMES)
	public Response<Resume> getResumes(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "type") String type){
		
		List<Resume> resumes = adminServices.getResumes(page, size, type);
		Response<Resume> response = new Response<Resume>();
		response.setDataList(resumes);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.ADD_CONTACT_US)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CONTACT_US, notes = PathProxy.AdminUrls.ADD_CONTACT_US)
	public Response<ContactUs> addContactUs(ContactUs request){
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getName(), request.getMobileNumber(), request.getMessage())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	    }
		ContactUs contactUs = adminServices.addContactUs(request);
		Response<ContactUs> response = new Response<ContactUs>();
		response.setData(contactUs);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_CONTACT_US)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_CONTACT_US, notes = PathProxy.AdminUrls.GET_CONTACT_US)
	public Response<ContactUs> getContactUs(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<ContactUs> contactUs = adminServices.getContactUs(page, size);
		Response<ContactUs> response = new Response<ContactUs>();
		response.setDataList(contactUs);
		return response;
	}
	
//	@Path(value = PathProxy.AdminUrls.GET_PATIENT)
//	@GET
//	public Response<Resume> getResumes(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "type") String type){
//		
//		List<Resume> resumes = adminServices.getResumes(page, size, type);
//		Response<Resume> response = new Response<Resume>();
//		response.setDataList(resumes);
//		return response;
//	}
	
	@Path(value = PathProxy.AdminUrls.IMPORT_DRUG)
    @GET
    public Response<Boolean> importDrug() {
	adminServices.importDrug();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_CITY)
    @GET
    public Response<Boolean> importCity() {
		adminServices.importCity();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_DIAGNOSTIC_TEST)
    @GET
    public Response<Boolean> importDiagnosticTest() {
	adminServices.importDiagnosticTest();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_EDUCATION_INSTITUTE)
    @GET
    public Response<Boolean> importEducationInstitute() {
		adminServices.importEducationInstitute();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_EDUCATION_QUALIFICATION)
    @GET
    public Response<Boolean> importEducationQualification() {
	adminServices.importEducationQualification();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY)
    @GET
    @ApiOperation(value = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY, notes = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY)
    public Response<Speciality> getUniqueSpecialities(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<Speciality> searchResonse = adminServices.getUniqueSpecialities(searchTerm, updatedTime, page, size);
	Response<Speciality> response = new Response<Speciality>();
	response.setDataList(searchResonse);
	return response;
    }
    
    @Path(value = PathProxy.AdminUrls.GET_CINICAL_ITEMS)
    @GET
    @ApiOperation(value = PathProxy.AdminUrls.GET_CINICAL_ITEMS, notes = PathProxy.AdminUrls.GET_CINICAL_ITEMS)
    public Response<Object> getClinicalItems(@PathParam("type") String type, @PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	if (DPDoctorUtils.anyStringEmpty(type, range)) {
	    logger.warn("Invalid Input. Type or Range Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type or Range Cannot Be Empty");
	}
	List<?> clinicalItems = clinicalNotesService.getClinicalItems(type, range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, true, searchTerm);
	if (clinicalItems != null && !clinicalItems.isEmpty() && ClinicalItems.DIAGRAMS.getType().equalsIgnoreCase(type)) {
	    for (Object clinicalItem : clinicalItems) {
		((DiagramsCollection) clinicalItem).setDiagramUrl(getFinalImageURL(((DiagramsCollection) clinicalItem).getDiagramUrl()));
	    }
	}
	Response<Object> response = new Response<Object>();
	response.setDataList(clinicalItems);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_COMPLAINT)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_COMPLAINT, notes = PathProxy.AdminUrls.ADD_COMPLAINT)
    public Response<Complaint> addComplaint(Complaint request) {
    	
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getComplaint())) {
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

    @Path(value = PathProxy.AdminUrls.ADD_OBSERVATION)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_OBSERVATION, notes = PathProxy.AdminUrls.ADD_OBSERVATION)
    public Response<Observation> addObservation(Observation request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getObservation())) {
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

    @Path(value = PathProxy.AdminUrls.ADD_INVESTIGATION)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_INVESTIGATION, notes = PathProxy.AdminUrls.ADD_INVESTIGATION)
    public Response<Investigation> addInvestigation(Investigation request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getInvestigation())) {
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

    @Path(value = PathProxy.AdminUrls.ADD_DIAGNOSIS)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DIAGNOSIS, notes = PathProxy.AdminUrls.ADD_DIAGNOSIS)
    public Response<Diagnoses> addDiagnosis(Diagnoses request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getDiagnosis())) {
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

    @Path(value = PathProxy.AdminUrls.ADD_NOTES)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_NOTES, notes = PathProxy.AdminUrls.ADD_NOTES)
    public Response<Notes> addNotes(Notes request) {
    if (request == null || DPDoctorUtils.anyStringEmpty(request.getNote())) {
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

    @Path(value = PathProxy.AdminUrls.ADD_DIAGRAM)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DIAGRAM, notes = PathProxy.AdminUrls.ADD_DIAGRAM)
    public Response<Diagram> addDiagram(Diagram request) {
    if (request == null) {
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

    @Path(value = PathProxy.AdminUrls.DELETE_COMPLAINT)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_COMPLAINT, notes = PathProxy.AdminUrls.DELETE_COMPLAINT)
    public Response<Complaint> deleteComplaint(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	
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

    @Path(value = PathProxy.AdminUrls.DELETE_OBSERVATION)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_OBSERVATION, notes = PathProxy.AdminUrls.DELETE_OBSERVATION)
    public Response<Observation> deleteObservation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
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

    @Path(value = PathProxy.AdminUrls.DELETE_INVESTIGATION)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_INVESTIGATION, notes = PathProxy.AdminUrls.DELETE_INVESTIGATION)
    public Response<Investigation> deleteInvestigation(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
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

    @Path(value = PathProxy.AdminUrls.DELETE_DIAGNOSIS)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DIAGNOSIS, notes = PathProxy.AdminUrls.DELETE_DIAGNOSIS)
    public Response<Diagnoses> deleteDiagnosis(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
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

    @Path(value = PathProxy.AdminUrls.DELETE_NOTE)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_NOTE, notes = PathProxy.AdminUrls.DELETE_NOTE)
    public Response<Notes> deleteNote(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
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

    @Path(value = PathProxy.AdminUrls.DELETE_DIAGRAM)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DIAGRAM, notes = PathProxy.AdminUrls.DELETE_DIAGRAM)
    public Response<Diagram> deleteDiagram(@PathParam(value = "id") String id, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
    	Diagram diagram = clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId, discarded);
    	if(diagram != null){
    		transactionalManagementService.addResource(new ObjectId(diagram.getId()), Resource.DIAGRAM, false);
			ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
			BeanUtil.map(diagram, esDiagrams);
			esClinicalNotesService.addDiagrams(esDiagrams);		
		}
	Response<Diagram> response = new Response<Diagram>();
	response.setData(diagram);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_DISEASE)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DISEASE, notes = PathProxy.AdminUrls.ADD_DISEASE)
    public Response<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	List<DiseaseAddEditResponse> diseases = historyServices.addDiseases(request);
	for(DiseaseAddEditResponse addEditResponse : diseases){
		transactionalManagementService.addResource(new ObjectId(addEditResponse.getId()), Resource.DISEASE, false);
		ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
		BeanUtil.map(addEditResponse, esDiseasesDocument);
		esMasterService.addEditDisease(esDiseasesDocument);
	}
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setDataList(diseases);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_DISEASE)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_DISEASE, notes = PathProxy.AdminUrls.EDIT_DISEASE)
    public Response<DiseaseAddEditResponse> editDisease(@PathParam(value = "diseaseId") String diseaseId, DiseaseAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(diseaseId);
	DiseaseAddEditResponse diseases = historyServices.editDiseases(request);
	transactionalManagementService.addResource(new ObjectId(diseases.getId()), Resource.DISEASE, false);
	ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
	BeanUtil.map(diseases, esDiseasesDocument);
	esMasterService.addEditDisease(esDiseasesDocument);
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setData(diseases);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_DISEASE)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DISEASE, notes = PathProxy.AdminUrls.DELETE_DISEASE)
    public Response<DiseaseAddEditResponse> deleteDisease(@PathParam(value = "diseaseId") String diseaseId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (DPDoctorUtils.anyStringEmpty(diseaseId)) {
	    logger.warn("Disease Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Disease Id Cannot Be Empty");
	}
	DiseaseAddEditResponse diseaseDeleteResponse = historyServices.deleteDisease(diseaseId, doctorId, hospitalId, locationId, discarded);
	Response<DiseaseAddEditResponse> response = new Response<DiseaseAddEditResponse>();
	response.setData(diseaseDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.GET_DISEASES)
    @GET
    @ApiOperation(value = PathProxy.AdminUrls.GET_DISEASES, notes = PathProxy.AdminUrls.GET_DISEASES)
    public Response<DiseaseListResponse> getDiseases(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {
    if (DPDoctorUtils.anyStringEmpty(range)) {
    	    logger.warn("Range Cannot Be Empty");
    	    throw new BusinessException(ServiceError.InvalidInput, "Range Cannot Be Empty");
    	}
	List<DiseaseListResponse> diseaseListResponse = historyServices.getDiseases(range, page, size, doctorId, hospitalId, locationId, updatedTime, discarded, true, searchTerm);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(diseaseListResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_DRUG)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG, notes = PathProxy.AdminUrls.ADD_DRUG)
    public Response<DrugAddEditResponse> addDrug(DrugAddEditRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDrugName())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	DrugAddEditResponse drugAddEditResponse = prescriptionServices.addDrug(request);

	transactionalManagementService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
	if (drugAddEditResponse != null) {
	    ESDrugDocument esDrugDocument = new ESDrugDocument();
	    BeanUtil.map(drugAddEditResponse, esDrugDocument);
	    if (drugAddEditResponse.getDrugType() != null) {
		esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
		esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
	    }
	    esPrescriptionService.addDrug(esDrugDocument);
	}

	Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
	response.setData(drugAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_DRUG)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG, notes = PathProxy.AdminUrls.EDIT_DRUG)
    public Response<DrugAddEditResponse> editDrug(@PathParam(value = "drugId") String drugId, DrugAddEditRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(drugId, request.getDrugName())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	request.setId(drugId);
	DrugAddEditResponse drugAddEditResponse = prescriptionServices.editDrug(request);

	transactionalManagementService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
	if (drugAddEditResponse != null) {
	    ESDrugDocument esDrugDocument = new ESDrugDocument();
	    BeanUtil.map(drugAddEditResponse, esDrugDocument);
	    if (drugAddEditResponse.getDrugType() != null) {
		esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
		esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
	    }
	    esPrescriptionService.addDrug(esDrugDocument);
	}
	Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
	response.setData(drugAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_GLOBAL_DRUG)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_GLOBAL_DRUG, notes = PathProxy.AdminUrls.DELETE_GLOBAL_DRUG)
    public Response<Drug> deleteDrug(@PathParam(value = "drugId") String drugId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugId)) {
	    logger.warn("Drug Id, Doctor Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id Cannot Be Empty");
	}
	Drug drugDeleteResponse = prescriptionServices.deleteDrug(drugId, discarded);
	transactionalManagementService.addResource(new ObjectId(drugId), Resource.DRUG, false);
	if (drugDeleteResponse != null) {
	    ESDrugDocument esDrugDocument = new ESDrugDocument();
	    BeanUtil.map(drugDeleteResponse, esDrugDocument);
	    if (drugDeleteResponse.getDrugType() != null) {
		esDrugDocument.setDrugTypeId(drugDeleteResponse.getDrugType().getId());
		esDrugDocument.setDrugType(drugDeleteResponse.getDrugType().getType());
	    }
	    esPrescriptionService.addDrug(esDrugDocument);
	}
	Response<Drug> response = new Response<Drug>();
	response.setData(drugDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_LAB_TEST)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_LAB_TEST, notes = PathProxy.AdminUrls.ADD_LAB_TEST)
    public Response<LabTest> addLabTest(LabTest request) {
	if (request == null || request.getTest() == null) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	LabTest labTestResponse = prescriptionServices.addLabTest(request);
	transactionalManagementService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
	ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
	BeanUtil.map(labTestResponse, esLabTestDocument);
	if (labTestResponse.getTest() != null)esLabTestDocument.setTestId(labTestResponse.getTest().getId());
	esPrescriptionService.addLabTest(esLabTestDocument);
	Response<LabTest> response = new Response<LabTest>();
	response.setData(labTestResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_LAB_TEST)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_LAB_TEST, notes = PathProxy.AdminUrls.EDIT_LAB_TEST)
    public Response<LabTest> editLabTest(@PathParam(value = "labTestId") String labTestId, LabTest request) {
    	if (request == null|| request.getTest() == null) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	request.setId(labTestId);
	LabTest labTestResponse = prescriptionServices.editLabTest(request);
	transactionalManagementService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
	ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
	BeanUtil.map(labTestResponse, esLabTestDocument);
	if (labTestResponse.getTest() != null)esLabTestDocument.setTestId(labTestResponse.getTest().getId());
	esPrescriptionService.addLabTest(esLabTestDocument);
	Response<LabTest> response = new Response<LabTest>();
	response.setData(labTestResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_GLOBAL_LAB_TEST)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_GLOBAL_LAB_TEST, notes = PathProxy.AdminUrls.DELETE_GLOBAL_LAB_TEST)
    public Response<LabTest> deleteLabTest(@PathParam(value = "labTestId") String labTestId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(labTestId)) {
	    logger.warn("Lab Test Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Lab Test Id Cannot Be Empty");
	}
	LabTest labTestDeleteResponse = prescriptionServices.deleteLabTest(labTestId, discarded);
	transactionalManagementService.addResource(new ObjectId(labTestDeleteResponse.getId()), Resource.LABTEST, false);
	ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
	BeanUtil.map(labTestDeleteResponse, esLabTestDocument);
	if (labTestDeleteResponse.getTest() != null)esLabTestDocument.setTestId(labTestDeleteResponse.getTest().getId());
	esPrescriptionService.addLabTest(esLabTestDocument);

	Response<LabTest> response = new Response<LabTest>();
	response.setData(labTestDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_DRUG_TYPE)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_TYPE, notes = PathProxy.AdminUrls.ADD_DRUG_TYPE)
    public Response<DrugTypeAddEditResponse> addDrugType(DrugTypeAddEditRequest request) {
    	if (request == null || DPDoctorUtils.anyStringEmpty(request.getType())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	DrugTypeAddEditResponse drugTypeAddEditResponse = prescriptionServices.addDrugType(request);

	Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
	response.setData(drugTypeAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_DRUG_TYPE)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_TYPE, notes = PathProxy.AdminUrls.EDIT_DRUG_TYPE)
    public Response<DrugTypeAddEditResponse> editDrugType(@PathParam(value = "drugTypeId") String drugTypeId, DrugTypeAddEditRequest request) {
    	if (request == null|| DPDoctorUtils.anyStringEmpty(request.getType())) {
    	    logger.warn("Invalid Input");
    	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
    	}
	request.setId(drugTypeId);
	DrugTypeAddEditResponse drugTypeAddEditResponse = prescriptionServices.editDrugType(request);

	transactionalManagementService.addResource(new ObjectId(drugTypeId), Resource.DRUGSDRUGTYPE, false);
	if (drugTypeAddEditResponse != null) {
	    esPrescriptionService.editDrugTypeInDrugs(drugTypeId);
	}
	Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
	response.setData(drugTypeAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_DRUG_TYPE)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_TYPE, notes = PathProxy.AdminUrls.DELETE_DRUG_TYPE)
    public Response<DrugTypeAddEditResponse> deleteDrugType(@PathParam(value = "drugTypeId") String drugTypeId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugTypeId)) {
	    logger.warn("Drug Type Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Type Id Cannot Be Empty");
	}
	DrugTypeAddEditResponse drugTypeDeleteResponse = prescriptionServices.deleteDrugType(drugTypeId, discarded);

	Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
	response.setData(drugTypeDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_DRUG_DOSAGE)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_DOSAGE, notes = PathProxy.AdminUrls.ADD_DRUG_DOSAGE)
    public Response<DrugDosageAddEditResponse> addDrugDosage(DrugDosageAddEditRequest request) {
	if (request == null || request.getDosage() == null || DPDoctorUtils.anyStringEmpty(request.getDosage().getDosage())) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.addDrugDosage(request);

	Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
	response.setData(drugDosageAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_DRUG_DOSAGE)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_DOSAGE, notes = PathProxy.AdminUrls.EDIT_DRUG_DOSAGE)
    public Response<DrugDosageAddEditResponse> editDrugDosage(@PathParam(value = "drugDosageId") String drugDosageId, DrugDosageAddEditRequest request) {
	if (request == null || request.getDosage() == null || DPDoctorUtils.anyStringEmpty(drugDosageId, request.getDosage().getDosage())) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugDosageId);
	DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.editDrugDosage(request);

	Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
	response.setData(drugDosageAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_DRUG_DOSAGE)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_DOSAGE, notes = PathProxy.AdminUrls.DELETE_DRUG_DOSAGE)
    public Response<DrugDosageAddEditResponse> deleteDrugDosage(@PathParam(value = "drugDosageId") String drugDosageId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (DPDoctorUtils.anyStringEmpty(drugDosageId)) {
	    logger.warn("Drug Dosage Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Dosage Id Cannot Be Empty");
	}
	DrugDosageAddEditResponse drugDosageDeleteResponse = prescriptionServices.deleteDrugDosage(drugDosageId, discarded);

	Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
	response.setData(drugDosageDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_DRUG_DIRECTION)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_DIRECTION, notes = PathProxy.AdminUrls.ADD_DRUG_DIRECTION)
    public Response<DrugDirectionAddEditResponse> addDrugDirection(DrugDirectionAddEditRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getDirection())) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.addDrugDirection(request);

	Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
	response.setData(drugDirectionAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_DRUG_DIRECTION)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_DIRECTION, notes = PathProxy.AdminUrls.EDIT_DRUG_DIRECTION)
    public Response<DrugDirectionAddEditResponse> editDrugDirection(@PathParam(value = "drugDirectionId") String drugDirectionId, DrugDirectionAddEditRequest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(drugDirectionId, request.getDirection())) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugDirectionId);
	DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.editDrugDirection(request);

	Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
	response.setData(drugDirectionAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_DRUG_DIRECTION)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_DIRECTION, notes = PathProxy.AdminUrls.DELETE_DRUG_DIRECTION)
    public Response<DrugDirectionAddEditResponse> deleteDrugDirection(@PathParam(value = "drugDirectionId") String drugDirectionId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (DPDoctorUtils.anyStringEmpty(drugDirectionId)) {
	    logger.warn("Drug Direction Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Direction Id Cannot Be Empty");
	}
	DrugDirectionAddEditResponse drugDirectionDeleteResponse = prescriptionServices.deleteDrugDirection(drugDirectionId, discarded);

	Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
	response.setData(drugDirectionDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_DRUG_DURATION_UNIT)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_DRUG_DURATION_UNIT, notes = PathProxy.AdminUrls.ADD_DRUG_DURATION_UNIT)
    public Response<DrugDurationUnitAddEditResponse> addDrugDurationUnit(DrugDurationUnitAddEditRequest request) {
	if (request == null  || DPDoctorUtils.anyStringEmpty(request.getUnit())) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices.addDrugDurationUnit(request);

	Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
	response.setData(drugDurationUnitAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.EDIT_DRUG_DURATION_UNIT)
    @PUT
    @ApiOperation(value = PathProxy.AdminUrls.EDIT_DRUG_DURATION_UNIT, notes = PathProxy.AdminUrls.EDIT_DRUG_DURATION_UNIT)
    public Response<DrugDurationUnitAddEditResponse> editDrugDurationUnit(@PathParam(value = "drugDurationUnitId") String drugDurationUnitId,
	    DrugDurationUnitAddEditRequest request) {
	if (request == null  || DPDoctorUtils.anyStringEmpty(drugDurationUnitId, request.getUnit())) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugDurationUnitId);
	DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices.editDrugDurationUnit(request);

	Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
	response.setData(drugDurationUnitAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_DRUG_DURATION_UNIT)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_DRUG_DURATION_UNIT, notes = PathProxy.AdminUrls.DELETE_DRUG_DURATION_UNIT)
    public Response<DrugDurationUnitAddEditResponse> deleteDrugDurationUnit(@PathParam(value = "drugDurationUnitId") String drugDurationUnitId,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugDurationUnitId)) {
	    logger.warn("Drug Duration Unit Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Duration Unit Id Cannot Be Empty");
	}
	DrugDurationUnitAddEditResponse drugDurationUnitDeleteResponse = prescriptionServices.deleteDrugDurationUnit(drugDurationUnitId, discarded);

	Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
	response.setData(drugDurationUnitDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.ADD_EDIT_DIAGNOSTIC_TEST)
    @POST
    @ApiOperation(value = PathProxy.AdminUrls.ADD_EDIT_DIAGNOSTIC_TEST, notes = PathProxy.AdminUrls.ADD_EDIT_DIAGNOSTIC_TEST)
    public Response<DiagnosticTest> addEditDiagnosticTest(DiagnosticTest request) {
	if (request == null || DPDoctorUtils.anyStringEmpty(request.getTestName())) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	DiagnosticTest diagnosticTest = prescriptionServices.addEditDiagnosticTest(request);
	transactionalManagementService.addResource(new ObjectId(diagnosticTest.getId()), Resource.DIAGNOSTICTEST, false);

	ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
	BeanUtil.map(diagnosticTest, esDiagnosticTestDocument);
	esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
	Response<DiagnosticTest> response = new Response<DiagnosticTest>();
	response.setData(diagnosticTest);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.DELETE_GLOBAL_DIAGNOSTIC_TEST)
    @DELETE
    @ApiOperation(value = PathProxy.AdminUrls.DELETE_GLOBAL_DIAGNOSTIC_TEST, notes = PathProxy.AdminUrls.DELETE_GLOBAL_DIAGNOSTIC_TEST)
    public Response<DiagnosticTest> deleteDiagnosticTest(@PathParam(value = "diagnosticTestId") String diagnosticTestId, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
	if (DPDoctorUtils.anyStringEmpty(diagnosticTestId)) {
	    logger.warn("Diagnostic Test Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Diagnostic Test Id Cannot Be Empty");
	}
	DiagnosticTest testDeleteResponse = prescriptionServices.deleteDiagnosticTest(diagnosticTestId, discarded);
	ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
	BeanUtil.map(testDeleteResponse, esDiagnosticTestDocument);
	esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
	

	Response<DiagnosticTest> response = new Response<DiagnosticTest>();
	response.setData(testDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.GET_PRESCRIPTION_ITEMS)
    @GET
    @ApiOperation(value = PathProxy.AdminUrls.GET_PRESCRIPTION_ITEMS, notes = PathProxy.AdminUrls.GET_PRESCRIPTION_ITEMS)
    public Response<Object> getPrescriptionItems(@PathParam("type") String type, @PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	if (DPDoctorUtils.anyStringEmpty(type, range)) {
	    logger.warn("Invalid Input");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
	}
	List<?> clinicalItems = prescriptionServices.getPrescriptionItems(type, range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, true, searchTerm);

	Response<Object> response = new Response<Object>();
	response.setDataList(clinicalItems);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CONTACT_STATE)
    @GET
    @ApiOperation(value = PathProxy.AdminUrls.UPDATE_DOCTOR_CONTACT_STATE, notes = PathProxy.AdminUrls.UPDATE_DOCTOR_CONTACT_STATE)
    public Response<DoctorContactUs> getDoctorContactList(@PathParam(value = "contactId") String contactId, @PathParam(value = "contactState") DoctorContactStateType contactState)
    {
    	DoctorContactUs doctorContactUs = doctorContactUsService.updateDoctorContactState(contactId, contactState);
		Response<DoctorContactUs> response = new Response<DoctorContactUs>();
		response.setData(doctorContactUs);
		return response;
    }
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) return imagePath + imageURL;
		else return null;
	 }
}
