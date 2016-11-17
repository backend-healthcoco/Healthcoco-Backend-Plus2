package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;

public interface ESPresentComplaintHistoryRepository extends ElasticsearchRepository<ESPresentComplaintHistoryDocument, String>{

}
