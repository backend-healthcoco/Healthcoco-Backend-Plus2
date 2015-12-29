package com.dpdocter.solr.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrStateDocument;

public interface SolrStateRepository extends SolrCrudRepository<SolrStateDocument, String> {

}
