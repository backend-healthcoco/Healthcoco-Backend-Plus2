package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;

public interface ESUserLocaleRepository extends ElasticsearchRepository<ESUserLocaleDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"localeName\": \"?0*\"}}]}}")
	List<ESUserLocaleDocument> findByLocaleName(String searchTerm);

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"city\": \"?0*\"}}, "
    		+ "{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmarkDetails\": \"?1*\"}}, {\"match_phrase_prefix\": {\"streetAddress\": \"?1*\"}}, {\"match_phrase_prefix\": {\"locality\": \"?1*\"}}]}},"
    		+ "{\"match_phrase_prefix\": {\"locationName\": \"?2*\"}}, {\"match\": {\"isLocationListed\": \"?3\"}}]}}")
	List<ESUserLocaleDocument> findByCityLocationName(String city, String location, String searchTerm, Boolean isLocaleListed, Pageable pageRequest);

	List<ESUserLocaleDocument> findByCityLocationName(String city, String searchTerm, Boolean isLocaleListed, Pageable pageRequest);

	List<ESUserLocaleDocument> findByLocationLocationName(String location, String searchTerm, Boolean isLocaleListed, Pageable pageRequest);

	List<ESUserLocaleDocument> findLocationByCityLocation(String city, String location, Boolean isLocaleListed, Pageable pageRequest);

	List<ESUserLocaleDocument> findLocationByCity(String city, Boolean isLocaleListed, Pageable pageRequest);

	List<ESUserLocaleDocument> findLocationByLocation(String location, Boolean isLocaleListed, Pageable pageRequest);

}
