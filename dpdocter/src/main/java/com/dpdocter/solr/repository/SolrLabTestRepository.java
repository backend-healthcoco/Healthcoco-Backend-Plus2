package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrLabTestDocument;

public interface SolrLabTestRepository extends SolrCrudRepository<SolrLabTestDocument, String> {

    @Query("testName:*?0*")
    List<SolrLabTestDocument> find(String searchTerm);

}
