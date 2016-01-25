package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrLocationDocument;

public interface SolrLocationRepository extends SolrCrudRepository<SolrLocationDocument, String> {
    @Query("(locationName : ?0* OR landmarkDetails : ?0* OR locality : ?0*) AND city : ?1*")
    List<SolrLocationDocument> findAll(String location, String city);

    @Query("city : ?0*")
    List<SolrLocationDocument> findAll(String city);
}
