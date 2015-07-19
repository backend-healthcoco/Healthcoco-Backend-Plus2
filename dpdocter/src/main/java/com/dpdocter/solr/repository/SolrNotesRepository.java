package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrNotes;

public interface SolrNotesRepository extends SolrCrudRepository<SolrNotes, String> {
	@Query("notes:*?0*")
	public List<SolrNotes> find(String searchTerm);
}
