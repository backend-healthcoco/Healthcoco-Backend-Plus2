package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrComplaints;

public interface SolrComplaintsRepository extends SolrCrudRepository<SolrComplaints, String> {
	@Query("complaint:*?0*")
	public List<SolrComplaints> find(String searchTerm);
}
