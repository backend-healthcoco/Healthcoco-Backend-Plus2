package com.dpdocter.solr.webservices;

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

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.solr.document.SolrComplaints;
import com.dpdocter.solr.document.SolrDiagnoses;
import com.dpdocter.solr.document.SolrDiagrams;
import com.dpdocter.solr.document.SolrInvestigations;
import com.dpdocter.solr.document.SolrNotes;
import com.dpdocter.solr.document.SolrObservations;
import com.dpdocter.solr.services.SolrClinicalNotesService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrClinicalNotesApi {
	@Autowired
	private SolrClinicalNotesService solrClinicalNotesService;

	@Path(value = PathProxy.SolrClinicalNotesUrls.ADD_COMPLAINTS)
	@POST
	public Response<Boolean> addComplaints(SolrComplaints request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addComplaintsResponse = solrClinicalNotesService.addComplaints(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addComplaintsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_COMPLAINTS)
	@POST
	public Response<Boolean> editComplaints(SolrComplaints request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editComplaintsResponse = solrClinicalNotesService.editComplaints(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editComplaintsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_COMPLAINTS)
	@GET
	public Response<Boolean> deleteComplaints(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteComplaintsResponse = solrClinicalNotesService.deleteComplaints(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteComplaintsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_COMPLAINTS)
	@GET
	public Response<SolrComplaints> searchComplaints(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrComplaints> complaints = solrClinicalNotesService.searchComplaints(searchTerm);
		Response<SolrComplaints> response = new Response<SolrComplaints>();
		response.setDataList(complaints);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.ADD_DIAGNOSES)
	@POST
	public Response<Boolean> addDiagnoses(SolrDiagnoses request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addDiagnosesResponse = solrClinicalNotesService.addDiagnoses(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addDiagnosesResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_DIAGNOSES)
	@POST
	public Response<Boolean> editDiagnoses(SolrDiagnoses request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editDiagnosesResponse = solrClinicalNotesService.editDiagnoses(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editDiagnosesResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_DIAGNOSES)
	@GET
	public Response<Boolean> deleteDiagnoses(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteDiagnosesResponse = solrClinicalNotesService.deleteDiagnoses(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteDiagnosesResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGNOSES)
	@GET
	public Response<SolrDiagnoses> searchDiagnoses(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrDiagnoses> diagnoses = solrClinicalNotesService.searchDiagnoses(searchTerm);
		Response<SolrDiagnoses> response = new Response<SolrDiagnoses>();
		response.setDataList(diagnoses);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.ADD_NOTES)
	@POST
	public Response<Boolean> addNotes(SolrNotes request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addNotesResponse = solrClinicalNotesService.addNotes(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addNotesResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_NOTES)
	@POST
	public Response<Boolean> editNotes(SolrNotes request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editNotesResponse = solrClinicalNotesService.editNotes(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editNotesResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_NOTES)
	@GET
	public Response<Boolean> deleteNotes(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteNotesResponse = solrClinicalNotesService.deleteNotes(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteNotesResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_NOTES)
	@GET
	public Response<SolrNotes> searchNotes(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrNotes> notes = solrClinicalNotesService.searchNotes(searchTerm);
		Response<SolrNotes> response = new Response<SolrNotes>();
		response.setDataList(notes);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.ADD_DIAGRAMS)
	@POST
	public Response<Boolean> addDiagrams(SolrDiagrams request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addDiagramsResponse = solrClinicalNotesService.addDiagrams(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addDiagramsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_DIAGRAMS)
	@POST
	public Response<Boolean> editDiagrams(SolrDiagrams request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editDiagramsResponse = solrClinicalNotesService.editDiagrams(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editDiagramsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_DIAGRAMS)
	@GET
	public Response<Boolean> deleteDiagrams(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteDiagramsResponse = solrClinicalNotesService.deleteDiagrams(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteDiagramsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_DIAGRAMS)
	@GET
	public Response<SolrDiagrams> searchDiagrams(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrDiagrams> diagrams = solrClinicalNotesService.searchDiagrams(searchTerm);
		Response<SolrDiagrams> response = new Response<SolrDiagrams>();
		response.setDataList(diagrams);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.ADD_INVESTIGATIONS)
	@POST
	public Response<Boolean> addInvestigations(SolrInvestigations request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addInvestigationsResponse = solrClinicalNotesService.addInvestigations(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addInvestigationsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_INVESTIGATIONS)
	@POST
	public Response<Boolean> editInvestigations(SolrInvestigations request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editInvestigationsResponse = solrClinicalNotesService.editInvestigations(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editInvestigationsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_INVESTIGATIONS)
	@GET
	public Response<Boolean> deleteInvestigations(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteInvestigationsResponse = solrClinicalNotesService.deleteInvestigations(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteInvestigationsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_INVESTIGATIONS)
	@GET
	public Response<SolrInvestigations> searchInvestigations(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrInvestigations> investigations = solrClinicalNotesService.searchInvestigations(searchTerm);
		Response<SolrInvestigations> response = new Response<SolrInvestigations>();
		response.setDataList(investigations);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.ADD_INVESTIGATIONS)
	@POST
	public Response<Boolean> addInvestigations(SolrObservations request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean addObservationsResponse = solrClinicalNotesService.addObservations(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addObservationsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.EDIT_OBSERVATIONS)
	@POST
	public Response<Boolean> editObservations(SolrObservations request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean editObservationsResponse = solrClinicalNotesService.editObservations(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(editObservationsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.DELETE_OBSERVATIONS)
	@GET
	public Response<Boolean> deleteObservations(@PathParam(value = "id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		boolean deleteObservationsResponse = solrClinicalNotesService.deleteObservations(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteObservationsResponse);
		return response;
	}

	@Path(value = PathProxy.SolrClinicalNotesUrls.SEARCH_OBSERVATIONS)
	@GET
	public Response<SolrObservations> searchObservations(@PathParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<SolrObservations> observations = solrClinicalNotesService.searchObservations(searchTerm);
		Response<SolrObservations> response = new Response<SolrObservations>();
		response.setDataList(observations);
		return response;
	}
}
