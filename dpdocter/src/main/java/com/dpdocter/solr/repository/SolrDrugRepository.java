package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDrug;

public interface SolrDrugRepository extends SolrCrudRepository<SolrDrug, String> {
	@Query("drugName:*?0*")
	public List<SolrDrug> find(String searchTerm);
}
