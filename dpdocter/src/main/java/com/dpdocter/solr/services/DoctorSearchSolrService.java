package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.repository.DoctorCoreDemoDocument;
import com.dpdocter.solr.repository.SearchDoctorSolrDocument;

public interface DoctorSearchSolrService {
	public void addToIndex(SearchDoctorSolrDocument searchDoctorSolrDocument);
	
	public void addToIndex(DoctorCoreDemoDocument doctorCoreDemoDocument);
	 
    public void deleteFromIndex(String id);
    
    List<SearchDoctorSolrDocument> findByQueryName(String text);
    
    List<DoctorCoreDemoDocument> findByQueryName1(String text);
}
