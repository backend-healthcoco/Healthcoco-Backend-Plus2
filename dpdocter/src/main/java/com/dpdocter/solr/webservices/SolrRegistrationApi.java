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
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.services.SolrRegistrationService;
import com.dpdocter.webservices.PathProxy;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.SOLR_REGISTRATION_BASEURL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrRegistrationApi {
    @Autowired
    private SolrRegistrationService solrRegistrationService;

    @Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT)
    @GET
    public Response<SolrPatientDocument> searchPatient(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "searchTerm") String searchTerm) {
	if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, searchTerm)) {
	    throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id and Search Term Cannot Be Empty");
	}
	List<SolrPatientDocument> patients = solrRegistrationService.searchPatient(doctorId, locationId, hospitalId, searchTerm);
	Response<SolrPatientDocument> response = new Response<SolrPatientDocument>();
	response.setDataList(patients);
	return response;
    }

    /*@Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV)
    @GET
    public Response<SolrPatientDocument> searchPatient(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
        @PathParam(value = "hospitalId") String hospitalId, @PathParam(value = "searchType") String searchType,
        @PathParam(value = "searchValue") String searchValue) {

    List<SolrPatientDocument> patients = null;
    if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, searchType, searchValue)) {
        throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, Location Id, Hospital Id, Search Type, Search Value Cannot Be Empty");
    }

    switch (searchType) {

    case "firstName":
        patients = solrRegistrationService.searchPatientByFirstName(doctorId, locationId, hospitalId, searchValue);
        break;
    case "middleName":
        patients = solrRegistrationService.searchPatientByMiddleName(doctorId, locationId, hospitalId, searchValue);
        break;
    case "lastName":
        patients = solrRegistrationService.searchPatientByLastName(doctorId, locationId, hospitalId, searchValue);
        break;
    case "PID":
        patients = solrRegistrationService.searchPatientByPID(doctorId, locationId, hospitalId, searchValue);
        break;
    case "mobileNumber":
        patients = solrRegistrationService.searchPatientByMobileNumber(doctorId, locationId, hospitalId, searchValue);
        break;
    case "emailAddress":
        patients = solrRegistrationService.searchPatientByEmailAddress(doctorId, locationId, hospitalId, searchValue);
        break;
    case "userName":
        patients = solrRegistrationService.searchPatientByUserName(doctorId, locationId, hospitalId, searchValue);
        break;
    case "city":
        patients = solrRegistrationService.searchPatientByCity(doctorId, locationId, hospitalId, searchValue);
        break;
    case "locality":
        patients = solrRegistrationService.searchPatientByLocality(doctorId, locationId, hospitalId, searchValue);
        break;
    case "bloodGroup":
        patients = solrRegistrationService.searchPatientByBloodGroup(doctorId, locationId, hospitalId, searchValue);
        break;
    case "referredBy":
        patients = solrRegistrationService.searchPatientByReferredBy(doctorId, locationId, hospitalId, searchValue);
        break;
    case "profession":
        patients = solrRegistrationService.searchPatientByProfession(doctorId, locationId, hospitalId, searchValue);
        break;
    case "postalCode":
        patients = solrRegistrationService.searchPatientByPostalCode(doctorId, locationId, hospitalId, searchValue);
        break;
    case "gender":
        patients = solrRegistrationService.searchPatientByGender(doctorId, locationId, hospitalId, searchValue);
        break;
    default:
        patients = solrRegistrationService.searchPatient(doctorId, locationId, hospitalId, searchValue);
        break;

    }

    Response<SolrPatientDocument> response = new Response<SolrPatientDocument>();
    response.setDataList(patients);
    return response;
    }*/

    @Path(value = PathProxy.SolrRegistrationUrls.SEARCH_PATIENT_ADV)
    @POST
    public Response<SolrPatientDocument> searchPatient(AdvancedSearch request) {
	List<SolrPatientDocument> patients = null;

	if (request == null) {
	    throw new BusinessException(ServiceError.InvalidInput, "Search Request Cannot Be Empty");
	}

	patients = solrRegistrationService.searchPatient(request);

	Response<SolrPatientDocument> response = new Response<SolrPatientDocument>();
	response.setDataList(patients);
	return response;
    }

}
