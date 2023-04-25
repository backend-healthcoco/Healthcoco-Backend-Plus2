package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;

public interface ESComplaintsRepository extends ElasticsearchRepository<ESComplaintsDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"complaint\": \"?0*\"}},{\"missing\": {\"field\":\"doctorId\" }},{\"missing\": {\"field\":\"locationId\" }},{\"missing\": {\"field\":\"hospitalId\"}}]}}")
	List<ESComplaintsDocument> findByComplaint(String searchTerm);

}
