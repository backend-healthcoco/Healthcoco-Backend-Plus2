package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrPatientDocument;

public interface SolrRegistrationService {
    boolean addPatient(SolrPatientDocument request);

    boolean editPatient(SolrPatientDocument request);

    boolean deletePatient(String id);

    List<SolrPatientDocument> searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm);

}
