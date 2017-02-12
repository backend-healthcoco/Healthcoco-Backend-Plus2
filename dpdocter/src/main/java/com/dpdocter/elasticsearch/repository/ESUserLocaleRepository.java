package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;

public interface ESUserLocaleRepository extends ElasticsearchRepository<ESUserLocaleDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"localeName\": \"?0*\"}}]}}")
	List<ESUserLocaleDocument> findByLocaleName(String searchTerm);
}
