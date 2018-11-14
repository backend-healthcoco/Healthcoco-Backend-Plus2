package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESExerciseTypeDocument;

public interface ESExerciseRepository extends ElasticsearchRepository<ESExerciseTypeDocument, String>{

}
