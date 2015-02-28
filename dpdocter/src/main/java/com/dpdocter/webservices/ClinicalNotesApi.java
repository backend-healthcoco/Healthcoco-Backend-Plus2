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
	
	@Path(value=PathProxy.ClinicalNotesUrls.ADD_NOTES)
	@POST
	public Response<ClinicalNotes> addNotes(ClinicalNotesAddRequest request){
		ClinicalNotes clinicalNotes = clinicalNotesService.addNotes(request);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.EDIT_NOTES)
	@POST
	public Response<ClinicalNotes> editNotes(ClinicalNotesEditRequest request){
		ClinicalNotes clinicalNotes = clinicalNotesService.editNotes(request);
		Response<ClinicalNotes> response = new Response<ClinicalNotes>();
		response.setData(clinicalNotes);
		return response;
	}
	
	@Path(value=PathProxy.ClinicalNotesUrls.DELETE_NOTES)
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
	

}
