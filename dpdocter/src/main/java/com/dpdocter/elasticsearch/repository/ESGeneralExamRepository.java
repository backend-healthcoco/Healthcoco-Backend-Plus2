package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;

public interface ESGeneralExamRepository extends ElasticsearchRepository<ESGeneralExamDocument, String> {

}
