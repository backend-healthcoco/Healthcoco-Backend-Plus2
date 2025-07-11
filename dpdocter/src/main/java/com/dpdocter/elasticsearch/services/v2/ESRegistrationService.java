package com.dpdocter.elasticsearch.services.v2;

import com.dpdocter.elasticsearch.beans.AdvancedSearch;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.response.v2.ESPatientResponseDetails;

public interface ESRegistrationService {
    boolean addPatient(ESPatientDocument request);

    ESPatientResponseDetails searchPatient(String locationId, String hospitalId, String searchTerm, int page, int size, String doctorId, String role);

    ESPatientResponseDetails searchPatient(AdvancedSearch request);

    boolean addDoctor(ESDoctorDocument request);

    void editLocation(DoctorLocation doctorLocation);

	void addEditReference(ESReferenceDocument esReferenceDocument);
	
	void activateUser(String userId);

	boolean addCollectionBoy(ESCollectionBoyDocument request);

}
