package com.dpdocter.solr.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrCountryDocument;

public interface SolrCountryRepository extends SolrCrudRepository<SolrCountryDocument, String> {

}
