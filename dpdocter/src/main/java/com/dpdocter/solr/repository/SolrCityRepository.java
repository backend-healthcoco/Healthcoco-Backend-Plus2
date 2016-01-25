package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrCityDocument;

public interface SolrCityRepository extends SolrCrudRepository<SolrCityDocument, String> {
    @Query("city:?0* AND isActivated:true")
    List<SolrCityDocument> findByQueryAnnotation(String searchTerm);

    @Query("city:?0* AND isActivated:true AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
    List<SolrCityDocument> findByQueryAnnotation(String searchTerm, double latitude, double longitude);

    @Query("isActivated:true AND !geofilt sfield='geoLocation'  pt=?0,?1 d=10")
    List<SolrCityDocument> findByQueryAnnotation(double latitude, double longitude);
}
