package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;

public interface ESTreatmentServiceRepository extends ElasticsearchRepository<ESTreatmentServiceDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"name\": \"?0*\"}},{\"missing\": {\"field\":\"doctorId\" }},{\"missing\": {\"field\":\"locationId\" }},{\"missing\": {\"field\":\"hospitalId\"}}]}}")
	List<ESTreatmentServiceDocument> findByName(String searchTerm);

}
