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

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnosis;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;

import common.util.web.Response;

@Component
@Path(PathProxy.CLINICAL_NOTES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalNotesApi {
	
	@Autowired
	private ClinicalNotesService clinicalNotesService;
	
	@Path(value=PathProxy.ClinicalNotesUrls.SAVE_CLINICAL_NOTE)
	@POST
	public Response<ClinicalNotes> addNotes(ClinicalNotesAddRequest request){
		ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}
	
	
	@Path(value=PathProxy.ClinicalNotesUrls.EDIT_CLINICAL_NOTES)
	@POST
	public Response<ClinicalNotes> editNotes(ClinicalNotesEditRequest request){
		ClinicalNotes clinicalNotes = clinicalNotesService.editNotes(request);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_CLINICAL_NOTES)
	@GET
	public Response<Boolean> deleteNotes(@PathParam(value="clinicalNotesId")String clinicalNotesId){
		clinicalNotesService.deleteNote(clinicalNotesId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES_ID)
	@GET
	public Response<ClinicalNotes> getNotesById(@PathParam(value="clinicalNotesId")String clinicalNotesId){
		ClinicalNotes clinicalNotes =clinicalNotesService.getNotesById(clinicalNotesId);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.GET_CLINICAL_NOTES)
	@GET
	public Response<ClinicalNotes> getNotes(@PathParam(value="doctorId")String doctorId,@PathParam(value="patientId")String patientId,@PathParam(value="isOTPVarified")boolean isOTPVarified){
		List<ClinicalNotes> clinicalNotes = null;
		if(isOTPVarified){
			clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithVarifiedOTP(patientId);
		}else{
			clinicalNotes = clinicalNotesService.getPatientsClinicalNotesWithoutVarifiedOTP(patientId, doctorId);
		}
		
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setDataList(clinicalNotes);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
	@POST
	public Response<Complaint> addComplaint(Complaint request){
		Complaint complaint = clinicalNotesService.addEditComplaint(request);
		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
	@POST
	public Response<Observation> addObservation(Observation request){
		Observation observation = clinicalNotesService.addEditObservation(request);
		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
	@POST
	public Response<Investigation> addInvestigation(Investigation request){
		Investigation investigation = clinicalNotesService.addEditInvestigation(request);
		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
	@POST
	public Response<Diagnosis> addDiagnosis(Diagnosis request){
		Diagnosis diagnosis = clinicalNotesService.addEditDiagnosis(request);
		Response<Diagnosis> response = new Response<Diagnosis>();
		response.setData(diagnosis);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_NOTES)
	@POST
	public Response<Notes> addNotes(Notes request){
		Notes notes = clinicalNotesService.addEditNotes(request);
		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
	@POST
	public Response<Diagram> addDiagram(Diagram request){
		Diagram diagram = clinicalNotesService.addEditDiagram(request);
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
	@GET
	public Response<Boolean> deleteComplaint(@PathParam(value="id")String id,@PathParam(value="doctorId")String doctorId){
		clinicalNotesService.deleteComplaint(id,doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
	@GET
	public Response<Boolean> deleteObservation(@PathParam(value="id")String id,@PathParam(value="doctorId")String doctorId){
		clinicalNotesService.deleteObservation(id,doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
	@GET
	public Response<Boolean> deleteInvestigation(@PathParam(value="id")String id,@PathParam(value="doctorId")String doctorId){
		clinicalNotesService.deleteInvestigation(id,doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
	@GET
	public Response<Boolean> deleteDiagnosis(@PathParam(value="id")String id,@PathParam(value="doctorId")String doctorId){
		clinicalNotesService.deleteDiagnosis(id,doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_NOTE)
	@GET
	public Response<Boolean> deleteNote(@PathParam(value="id")String id,@PathParam(value="doctorId")String doctorId){
		clinicalNotesService.deleteNotes(id,doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
	@GET
	public Response<Boolean> deleteDiagram(@PathParam(value="id")String id,@PathParam(value="doctorId")String doctorId){
		clinicalNotesService.deleteDiagram(id,doctorId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
}
