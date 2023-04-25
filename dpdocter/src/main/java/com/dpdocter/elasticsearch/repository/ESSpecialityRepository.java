package com.dpdocter.elasticsearch.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESSpecialityDocument;

public interface ESSpecialityRepository extends ElasticsearchRepository<ESSpecialityDocument, String> {
    @Query("{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"speciality\": \"?0*\"}}, {\"match_phrase_prefix\": {\"superSpeciality\": \"?0*\"}}], \"minimum_should_match\": 1}}")
    List<ESSpecialityDocument> findByQueryAnnotation(String speciality);

    @Query("{\"bool\": {\"must\": [{\"termsQuery\": {\"id\": \"?0\"}}}}")
	List<ESSpecialityDocument> findById(Collection<List<String>> specialityIds);

}
