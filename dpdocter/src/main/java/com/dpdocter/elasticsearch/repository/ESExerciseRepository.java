package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESExerciseDocument;

public interface ESExerciseRepository extends ElasticsearchRepository<ESExerciseDocument, String> {

}
