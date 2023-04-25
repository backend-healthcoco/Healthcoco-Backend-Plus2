package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESCityDocument;

public interface ESCityRepository extends ElasticsearchRepository<ESCityDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"city\": \"?0\"}}]}}")
	List<ESCityDocument> findByQueryAnnotation(String searchTerm);

	@Query("{\"bool\": {\"must\": [{\"prefix\": {\"city\": \"?0\"}}]}}")
	ESCityDocument findByName(String city);

	@Query("{\"bool\": {\"must\": [{\"termsQuery\": {\"isActivated\": \"?0\"}}}}")
	public long count(Boolean isActivated);
}