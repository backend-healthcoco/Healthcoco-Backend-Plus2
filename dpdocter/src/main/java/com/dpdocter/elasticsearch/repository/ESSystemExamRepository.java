package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESSystemExamDocument;

public interface ESSystemExamRepository extends ElasticsearchRepository<ESSystemExamDocument, String> {

}
