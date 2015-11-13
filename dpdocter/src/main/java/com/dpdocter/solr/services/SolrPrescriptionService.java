package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;

public interface SolrPrescriptionService {

    boolean addDrug(SolrDrugDocument request);

    boolean editDrug(SolrDrugDocument request);

    boolean deleteDrug(String id);

    boolean addLabTest(SolrLabTestDocument request);

    boolean editLabTest(SolrLabTestDocument request);

    boolean deleteLabTest(String labTestId);

	List<SolrDrugDocument> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<SolrLabTestDocument> searchLabTest(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

    

}
