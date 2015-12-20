package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;

public interface SolrLocalityLandmarkRepository extends SolrCrudRepository<SolrLocalityLandmarkDocument, String> {

    @Query("cityId:*?0* OR landmark:*?1* OR locality:*?1*")
    List<SolrLocalityLandmarkDocument> findByCityId(String cityId, String searchTerm);

    @Query("cityId:*?0* AND landmark:*?1*")
    List<SolrLocalityLandmarkDocument> findByCityIdAndLandmark(String cityId, String searchTerm);

    @Query("cityId:*?0* AND locality:*?1*")
    List<SolrLocalityLandmarkDocument> findByCityIdAndLocality(String cityId, String searchTerm);

    @Query("landmark:*?0*")
    List<SolrLocalityLandmarkDocument> findByLandmark(String searchTerm);

    @Query("locality:*?0*")
    List<SolrLocalityLandmarkDocument> findByLocality(String searchTerm);

    @Query("landmark:*?0* AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
    List<SolrLocalityLandmarkDocument> findByLandmark(String searchTerm, double latitude, double longitude);

    @Query("locality:*?0* AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
    List<SolrLocalityLandmarkDocument> findByLocality(String searchTerm, double latitude, double longitude);

    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10")
    List<SolrLocalityLandmarkDocument> findByLandmarkANDLocality(double latitude, double longitude);

}
