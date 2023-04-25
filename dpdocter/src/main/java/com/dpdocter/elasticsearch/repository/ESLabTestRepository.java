package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESLabTestDocument;

public interface ESLabTestRepository extends ElasticsearchRepository<ESLabTestDocument, String> {

}
