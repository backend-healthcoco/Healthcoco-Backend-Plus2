package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagnosticTestDocument;

public interface SolrDiagnosticTestRepository extends SolrCrudRepository<SolrDiagnosticTestDocument, String> {

	@Query("testName : ?0*")
	List<SolrDiagnosticTestDocument> findAll(String searchTerm);

}
