package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrObservations;

public interface SolrObservationsRepository extends SolrCrudRepository<SolrObservations, String> {
	@Query("observation:*?0*")
	public List<SolrObservations> find(String searchTerm);
}
