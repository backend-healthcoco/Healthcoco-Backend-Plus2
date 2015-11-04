package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;

public interface SolrPrescriptionService {

    boolean addDrug(SolrDrugDocument request);

    boolean editDrug(SolrDrugDocument request);

    boolean deleteDrug(String id);

    List<SolrDrugDocument> searchDrug(String searchTerm);

    boolean addLabTest(SolrLabTestDocument request);

    boolean editLabTest(SolrLabTestDocument request);

    boolean deleteLabTest(String labTestId);

    List<SolrLabTestDocument> searchLabTest(String searchTerm);

}
