package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrComplaintsDocument;

public interface SolrComplaintsRepository extends SolrCrudRepository<SolrComplaintsDocument, String> {
	@Query("complaint:*?0*")
	public List<SolrComplaintsDocument> findByQueryAnnotation(String searchTerm);
}
