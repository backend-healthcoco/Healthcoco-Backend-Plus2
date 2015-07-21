package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDrugDocument;

public interface SolrDrugRepository extends SolrCrudRepository<SolrDrugDocument, String> {
	@Query("drugName:*?0*")
	public List<SolrDrugDocument> find(String searchTerm);
}
