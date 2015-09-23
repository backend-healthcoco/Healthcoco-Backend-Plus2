package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.response.SolrPatientResponse;

public interface SolrRegistrationService {
    boolean addPatient(SolrPatientDocument request);

    boolean editPatient(SolrPatientDocument request);

    boolean deletePatient(String id);

    List<SolrPatientResponse> searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm);

    List<SolrPatientResponse> searchPatient(AdvancedSearch request);

    List<SolrPatientDocument> searchPatientByFirstName(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByMiddleName(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByLastName(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByPID(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByMobileNumber(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByEmailAddress(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByUserName(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByCity(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByLocality(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByBloodGroup(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByReferredBy(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByProfession(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByPostalCode(String doctorId, String locationId, String hospitalId, String searchValue);

    List<SolrPatientDocument> searchPatientByGender(String doctorId, String locationId, String hospitalId, String searchValue);

		void patientProfilePicChange(String username, String imageUrl);

}
