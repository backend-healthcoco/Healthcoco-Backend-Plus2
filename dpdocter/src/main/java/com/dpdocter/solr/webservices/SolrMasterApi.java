package com.dpdocter.solr.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Speciality;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.solr.services.SolrMasterService;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_MASTER_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrMasterApi {

    @Autowired
    SolrMasterService solrMasterService;

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
    @GET
    public Response<Reference> searchReference(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded, @QueryParam(value = "searchTerm") String searchTerm) {

	List<Reference> searchResonse = solrMasterService.searchReference(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<Reference> response = new Response<Reference>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
    @GET
    public Response<DiseaseListResponse> searchDisease(@PathParam("range") String range, @QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded,
	    @QueryParam(value = "searchTerm") String searchTerm) {

	List<DiseaseListResponse> searchResonse = solrMasterService.searchDisease(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
		searchTerm);
	Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
    @GET
    public Response<BloodGroup> searchBloodGroup(@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @QueryParam(value = "searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<BloodGroup> searchResonse = solrMasterService.searchBloodGroup(searchTerm, updatedTime, page, size);
	Response<BloodGroup> response = new Response<BloodGroup>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
    @GET
    public Response<Profession> searchProfession(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<Profession> searchResonse = solrMasterService.searchProfession(searchTerm, updatedTime, page, size);
	Response<Profession> response = new Response<Profession>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
    @GET
    public Response<ProfessionalMembership> searchProfessionalMembership(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<ProfessionalMembership> searchResonse = solrMasterService.searchProfessionalMembership(searchTerm, updatedTime, page, size);
	Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
    @GET
    public Response<EducationInstitute> searchEducationInstitute(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<EducationInstitute> searchResonse = solrMasterService.searchEducationInstitute(searchTerm, updatedTime, page, size);
	Response<EducationInstitute> response = new Response<EducationInstitute>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
    @GET
    public Response<EducationQualification> searchEducationQualification(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<EducationQualification> searchResonse = solrMasterService.searchEducationQualification(searchTerm, updatedTime, page, size);
	Response<EducationQualification> response = new Response<EducationQualification>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
    @GET
    public Response<MedicalCouncil> searchMedicalCouncil(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<MedicalCouncil> searchResonse = solrMasterService.searchMedicalCouncil(searchTerm, updatedTime, page, size);
	Response<MedicalCouncil> response = new Response<MedicalCouncil>();
	response.setDataList(searchResonse);
	return response;
    }

    @Path(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
    @GET
    public Response<Speciality> searchSpeciality(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<Speciality> searchResonse = solrMasterService.searchSpeciality(searchTerm, updatedTime, page, size);
	Response<Speciality> response = new Response<Speciality>();
	response.setDataList(searchResonse);
	return response;
    }
}
