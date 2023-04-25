package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;

public interface ESLandmarkLocalityRepository extends ElasticsearchRepository<ESLandmarkLocalityDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"cityId\": \"?0\"}}]}}")
	List<ESLandmarkLocalityDocument> findByCityId(String cityId);

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"landmark\": \"?0\"}}]}}")
	List<ESLandmarkLocalityDocument> findByLandmark(String searchTerm, Pageable pageable);

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"locality\": \"?0\"}}]}}")
	List<ESLandmarkLocalityDocument> findByLocality(String searchTerm, Pageable pageable);

	@Query("{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"landmark\": \"?0*\"}},{\"match_phrase_prefix\": {\"locality\": \"?0*\"}}], \"minimum_should_match\": 1}}")
	List<ESLandmarkLocalityDocument> findByQueryAnnotation(String searchTerm);
}
