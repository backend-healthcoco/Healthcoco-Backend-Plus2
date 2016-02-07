package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrSymptomsDocument;

public interface SolrSymptomsRepository extends SolrCrudRepository<SolrSymptomsDocument, String> {

    @Query("symptom : ?0*")
    List<SolrSymptomsDocument> findAll(String searchTerm);

}
