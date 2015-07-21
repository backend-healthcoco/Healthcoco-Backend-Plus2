package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrNotesDocument;

public interface SolrNotesRepository extends SolrCrudRepository<SolrNotesDocument, String> {
	@Query("notes:*?0*")
	public List<SolrNotesDocument> find(String searchTerm);
}
