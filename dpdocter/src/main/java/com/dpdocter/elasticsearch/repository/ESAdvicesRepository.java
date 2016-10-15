package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESAdvicesDocument;

public interface ESAdvicesRepository extends ElasticsearchRepository<ESAdvicesDocument, String> {
	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"advice\": \"?0*\"}}]}}")
	List<ESAdvicesDocument> findByComplaint(String searchTerm);
}
