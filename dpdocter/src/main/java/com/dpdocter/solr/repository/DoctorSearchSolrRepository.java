package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
public interface DoctorSearchSolrRepository extends SolrCrudRepository<SearchDoctorSolrDocument, String>{
	
	
	//List<SearchDoctorSolrDocument> findByDoctorNameContainsOrLocationNameContains
	
	@Query("name:*?0* OR title:*?0* OR doctorSpecification:*?0*")
    public List<SearchDoctorSolrDocument> findByQueryAnnotation(String searchTerm);
}
