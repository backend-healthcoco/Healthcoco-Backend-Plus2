package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;

public interface ESLandmarkLocalityRepository extends ElasticsearchRepository<ESLandmarkLocalityDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"cityId\": \"?0\"}}]}}")
    List<ESLandmarkLocalityDocument> findByCityId(String cityId);
	
//
//    @Query("cityId:*?0* AND landmark:?1*")
//    List<ESLandmarkLocalityDocument> findByCityIdAndLandmark(String cityId, String searchTerm);
//
//    @Query("cityId:*?0* AND locality:?1*")
//    List<ESLandmarkLocalityDocument> findByCityIdAndLocality(String cityId, String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"prefix\": {\"landmark\": \"?0\"}}]}}")
    List<ESLandmarkLocalityDocument> findByLandmark(String searchTerm, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"prefix\": {\"locality\": \"?0\"}}]}}")
    List<ESLandmarkLocalityDocument> findByLocality(String searchTerm, Pageable pageable);

//    @Query("landmark:?0* AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
//    List<ESLandmarkLocalityDocument> findByLandmark(String searchTerm, double latitude, double longitude);
//
//    @Query("locality:?0* AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
//    List<ESLandmarkLocalityDocument> findByLocality(String searchTerm, double latitude, double longitude);
//
//    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10")
//    List<ESLandmarkLocalityDocument> findByLandmarkANDLocality(double latitude, double longitude);

   
    @Query("{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmark\": \"?0*\"}},{\"match_phrase_prefix\": {\"locality\": \"?0*\"}}], \"minimum_should_match\": 1}}")
    List<ESLandmarkLocalityDocument> findByQueryAnnotation(String searchTerm);
}
