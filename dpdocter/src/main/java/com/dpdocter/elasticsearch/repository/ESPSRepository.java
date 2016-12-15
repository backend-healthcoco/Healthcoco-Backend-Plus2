package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPSDocument;

public interface ESPSRepository extends ElasticsearchRepository<ESPSDocument, String>{

}
