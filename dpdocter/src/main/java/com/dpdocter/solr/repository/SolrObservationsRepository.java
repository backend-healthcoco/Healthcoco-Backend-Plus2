package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrObservationsDocument;

public interface SolrObservationsRepository extends SolrCrudRepository<SolrObservationsDocument, String> {
    @Query("observation:*?0*")
    public List<SolrObservationsDocument> find(String searchTerm);
}
