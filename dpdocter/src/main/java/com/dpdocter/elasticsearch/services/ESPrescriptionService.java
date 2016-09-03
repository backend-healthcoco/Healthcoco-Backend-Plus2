package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;

public interface ESPrescriptionService {

    boolean addDrug(ESDrugDocument request);

    boolean addLabTest(ESLabTestDocument request);

    List<?> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm, String category);

    List<LabTest> searchLabTest(String range, int page, int size, String locationId, String hospitalId, String updatedTime, Boolean discarded,
	    String searchTerm);

    Boolean addEditDiagnosticTest(ESDiagnosticTestDocument solrDiagnosticTestDocument);

    List<ESDiagnosticTestDocument> searchDiagnosticTest(String range, int page, int size, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    Boolean editDrugTypeInDrugs(String drugTypeId);

	void addDoctorDrug(ESDoctorDrugDocument esDoctorDrugDocument);

}
