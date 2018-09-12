package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESRecipeDocument;

public interface ESRecipeRepository extends ElasticsearchRepository<ESRecipeDocument, String> {

}
