package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrLocationDocument;

public interface SolrLocationRepository extends SolrCrudRepository<SolrLocationDocument, String> {
//    @Query("(locationName : ?0* OR landmarkDetails : ?0* OR locality : ?0*) AND city : ?1*")
//    List<SolrLocationDocument> findAll(String location, String city);
//
//    @Query("city : ?0*")
//    List<SolrLocationDocument> findAll(String city);
    
    @Query("locationName : ?0*")
    List<SolrLocationDocument> findByLocationName(String searchTerm);
    
    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10 AND locationName : ?2*")
    List<SolrLocationDocument> findByLatitudeLongitudeLocation(String latitude, String longitude, String searchTerm);
    
    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND locationName : ?2*")
    List<SolrLocationDocument> findByCityLocationName(String city, String location, String searchTerm);

    @Query("city : *?0* AND locationName : *?1*")
    List<SolrLocationDocument> findByCityLocationName(String city, String searchTerm);

    @Query("(landmarkDetails: ?0* OR streetAddress: ?0* OR locality: ?0*) AND locationName : ?1*")
    List<SolrLocationDocument> findByLocationLocationName(String location, String searchTerm);

    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10")
    List<SolrLocationDocument> findByLatitudeLongitude(String latitude, String longitude);

    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*)")
    List<SolrLocationDocument> findByCityLocation(String city, String location);

    @Query("city : ?0*")
    List<SolrLocationDocument> findByCity(String city);

    @Query("(landmarkDetails: ?0* OR streetAddress: ?0* OR locality: ?0*)")
    List<SolrLocationDocument> findByLocation(String location);



}
