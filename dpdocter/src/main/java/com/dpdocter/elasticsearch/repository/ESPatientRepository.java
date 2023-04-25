package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPatientDocument;

public interface ESPatientRepository extends ElasticsearchRepository<ESPatientDocument, String>{

	@Query("{\"bool\": {\"must\": [{\"match\": {\"doctorId\": \"?0\"}}]}}")
	List<ESPatientDocument> findByDoctorId(String doctorId);

}
