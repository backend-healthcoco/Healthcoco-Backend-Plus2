package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagnoses;

public interface SolrDiagnosesRepository extends SolrCrudRepository<SolrDiagnoses, String> {
	@Query("diagnosis:*?0*")
	public List<SolrDiagnoses> find(String searchTerm);
}
