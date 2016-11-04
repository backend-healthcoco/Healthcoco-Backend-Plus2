package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;

public interface ESMenstrualHistoryRepository extends ElasticsearchRepository<ESMenstrualHistoryDocument, String>{

}
