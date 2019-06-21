package com.dpdocter.elasticsearch.repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESIngredientDocument;

public interface ESIngredientRepository extends ElasticsearchRepository<ESIngredientDocument, String> {

}
