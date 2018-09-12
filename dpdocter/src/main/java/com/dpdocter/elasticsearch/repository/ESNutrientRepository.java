package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESNutrientDocument;

public interface ESNutrientRepository extends ElasticsearchRepository<ESNutrientDocument, String>{

}
