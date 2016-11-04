package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;

public interface ESObstetricHistoryRepository extends ElasticsearchRepository<ESObstetricHistoryDocument, String> {

}
