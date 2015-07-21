package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrDrugDocument;

public interface SolrPrescriptionService {

	boolean addDrug(SolrDrugDocument request);

	boolean editDrug(SolrDrugDocument request);

	boolean deleteDrug(String id);

	List<SolrDrugDocument> searchDrug(String searchTerm);

}
