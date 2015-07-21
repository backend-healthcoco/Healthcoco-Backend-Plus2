package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagramsDocument;

public interface SolrDiagramsRepository extends SolrCrudRepository<SolrDiagramsDocument, String> {
	@Query("diagramUrl:*?0* OR tags:*?0*")
	public List<SolrDiagramsDocument> find(String searchTerm);
}
