package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESCityDocument;

public interface ESCityRepository extends ElasticsearchRepository<ESCityDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"city\": \"?0\"}}]}}")
	List<ESCityDocument> findByQueryAnnotation(String searchTerm);

//    @Query("city:?0* AND isActivated:true AND !geofilt sfield='geoLocation'  pt=?1,?2 d=10")
//    List<ESCityDocument> findByQueryAnnotation(String searchTerm, double latitude, double longitude);
//
//    @Query("isActivated:true AND !geofilt sfield='geoLocation'  pt=?0,?1 d=10")
//    List<ESCityDocument> findByQueryAnnotation(double latitude, double longitude);
//
	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0\"}}]}}")
    ESCityDocument findByName(String city);
	
	@Query("{\"bool\": {\"must\": [{\"termQuery\": {\"city\": \"?0\"}}, {\"match\": {\"isActivated\": \"?1\"}}]}}")
    ESCityDocument findByNameAndActivated(String city, Boolean isActivated);
}
