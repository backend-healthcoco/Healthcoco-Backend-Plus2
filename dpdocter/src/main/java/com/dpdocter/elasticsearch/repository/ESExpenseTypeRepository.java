package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;

public interface ESExpenseTypeRepository extends ElasticsearchRepository<ESExpenseTypeDocument, String> {

}
