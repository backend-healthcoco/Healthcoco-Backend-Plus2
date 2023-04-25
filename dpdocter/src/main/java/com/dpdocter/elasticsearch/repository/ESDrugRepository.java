package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDrugDocument;

public interface ESDrugRepository extends ElasticsearchRepository<ESDrugDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match\": {\"drugTypeId\": \"?0\"}}]}}")
	List<ESDrugDocument> findBydrugTypeId(String drugTypeId);
}
