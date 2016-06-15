package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.beans.LabTest;
import com.dpdocter.solr.document.SolrDiagnosticTestDocument;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;

public interface SolrPrescriptionService {

    boolean addDrug(SolrDrugDocument request);

    boolean editDrug(SolrDrugDocument request);

    boolean deleteDrug(String id, Boolean discarded);

    boolean addLabTest(SolrLabTestDocument request);

    boolean editLabTest(SolrLabTestDocument request);

    boolean deleteLabTest(String labTestId, Boolean discarded);

    List<SolrDrugDocument> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    List<LabTest> searchLabTest(String range, int page, int size, String locationId, String hospitalId, String updatedTime, Boolean discarded,
	    String searchTerm);

    Boolean addEditDiagnosticTest(SolrDiagnosticTestDocument solrDiagnosticTestDocument);

    boolean deleteDiagnosticTest(String diagnosticTestId, Boolean discarded);

    List<SolrDiagnosticTestDocument> searchDiagnosticTest(String range, int page, int size, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

}
