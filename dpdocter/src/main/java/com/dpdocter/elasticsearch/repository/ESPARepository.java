package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPADocument;

public interface ESPARepository extends ElasticsearchRepository<ESPADocument, String>{

}
