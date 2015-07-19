package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrDrug;

public interface SolrPrescriptionService {

	boolean addDrug(SolrDrug request);

	boolean editDrug(SolrDrug request);

	boolean deleteDrug(String id);

	List<SolrDrug> searchDrug(String searchTerm);

}
