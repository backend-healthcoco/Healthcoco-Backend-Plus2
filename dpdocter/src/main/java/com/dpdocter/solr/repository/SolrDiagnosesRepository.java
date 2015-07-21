package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagnosesDocument;

public interface SolrDiagnosesRepository extends SolrCrudRepository<SolrDiagnosesDocument, String> {
	@Query("diagnosis:*?0*")
	public List<SolrDiagnosesDocument> find(String searchTerm);
}
