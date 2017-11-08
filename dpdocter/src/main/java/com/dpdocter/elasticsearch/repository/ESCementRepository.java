package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESCementDocument;

public interface ESCementRepository extends ElasticsearchRepository<ESCementDocument, String> {

}
