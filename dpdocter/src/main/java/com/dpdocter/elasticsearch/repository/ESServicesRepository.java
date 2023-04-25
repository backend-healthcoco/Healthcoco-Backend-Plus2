package com.dpdocter.elasticsearch.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESServicesDocument;

public interface ESServicesRepository extends ElasticsearchRepository<ESServicesDocument, String> {
	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"service\": \"?0*\"}}]}}")
	List<ESServicesDocument> findByQueryAnnotation(String service);

	@Query("{\"bool\": {\"must\": [{\"termsQuery\": {\"id\": \"?0\"}}}}")
	List<ESServicesDocument> findById(Collection<List<String>> serviceIds);

}
