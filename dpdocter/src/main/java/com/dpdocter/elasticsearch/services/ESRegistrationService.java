package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.beans.AdvancedSearch;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.response.ESPatientResponseDetails;

public interface ESRegistrationService {
    boolean addPatient(ESPatientDocument request);

    ESPatientResponseDetails searchPatient(String locationId, String hospitalId, String searchTerm, long page, int size, String doctorId, String role);

    ESPatientResponseDetails searchPatient(AdvancedSearch request);

    boolean addDoctor(ESDoctorDocument request);

    void editLocation(DoctorLocation doctorLocation);

	void addEditReference(ESReferenceDocument esReferenceDocument);
	
	void activateUser(String userId);

	boolean addCollectionBoy(ESCollectionBoyDocument request);

	List<ESPatientDocument> searchDeletedPatient(String doctorId, String locationId, String hospitalId, int page, int size,
			String searchTerm, String sortBy);

}
