package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrInvestigations;

public interface SolrInvestigationsRepository extends SolrCrudRepository<SolrInvestigations, String> {
	@Query("diagramUrl:*?0* OR tags:*?0*")
	public List<SolrInvestigations> find(String searchTerm);
}
