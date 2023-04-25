package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESProfessionDocument;

public interface ESProfessionRepository extends ElasticsearchRepository<ESProfessionDocument, String> {

}
