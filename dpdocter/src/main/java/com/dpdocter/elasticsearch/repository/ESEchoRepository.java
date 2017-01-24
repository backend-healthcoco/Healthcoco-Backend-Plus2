package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESEchoDocument;

public interface ESEchoRepository extends ElasticsearchRepository<ESEchoDocument, String> {

}
