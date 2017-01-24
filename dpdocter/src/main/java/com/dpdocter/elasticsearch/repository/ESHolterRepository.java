package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESHolterDocument;

public interface ESHolterRepository extends ElasticsearchRepository<ESHolterDocument, String>{

}
