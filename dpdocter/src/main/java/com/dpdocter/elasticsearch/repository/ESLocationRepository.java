package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.elasticsearch.document.ESLocationDocument;

public interface ESLocationRepository extends ElasticsearchRepository<ESLocationDocument, String>, PagingAndSortingRepository<ESLocationDocument, String> {
    
    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"locationName\": \"?1*\"}}, {\"match\": {\"isLocationListed\": \"?2\"}}]}}")
    List<ESLocationDocument> findByLocationLocationName(String location, String searchTerm, Boolean isLocationListed, Pageable pageRequest);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
    		+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}}, {\"match\": {\"isLocationListed\": \"?2\"}}]}}")
    List<ESLocationDocument> findLocationByCityLocation(String city, String location, Boolean isLocationListed, Pageable pageRequest);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match\": {\"isLocationListed\": \"?1\"}}]}}")
    List<ESLocationDocument> findLocationByCity(String city, Boolean isLocationListed, Pageable pageRequest);

    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?0*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?0*\"}}]}}, {\"match\": {\"isLocationListed\": \"?1\"}}]}")
    List<ESLocationDocument> findLocationByLocation(String location, Boolean isLocationListed, Pageable pageRequest);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"locationName\": \"?0*\"}}]}}")
    List<ESLocationDocument> findByLocationName(String searchTerm);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
    		+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"locationName\": \"?2*\"}}, {\"match\": {\"isLocationListed\": \"?3\"}}]}}")
    List<ESLocationDocument> findByCityLocationName(String city, String location, String searchTerm, Boolean isLocationListed, Pageable pageRequest);

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, {\"match_phrase_prefix\": {\"locationName\": \"?1*\"}}, {\"match\": {\"isLocationListed\": \"?2\"}}]}}")
    List<ESLocationDocument> findByCityLocationName(String city, String searchTerm, Boolean isLocationListed, Pageable pageRequest);

    
}
