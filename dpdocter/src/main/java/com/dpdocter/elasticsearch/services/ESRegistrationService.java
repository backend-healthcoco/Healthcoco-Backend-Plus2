package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.response.SolrPatientResponseDetails;

public interface ESRegistrationService {
    boolean addPatient(ESPatientDocument request);

    SolrPatientResponseDetails searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm, int page, int size);

    SolrPatientResponseDetails searchPatient(AdvancedSearch request);

    boolean addDoctor(ESDoctorDocument request);

    void editLocation(DoctorLocation doctorLocation);

	void addEditReference(ESReferenceDocument esReferenceDocument);
	
	void activateUser(String userId);

}
