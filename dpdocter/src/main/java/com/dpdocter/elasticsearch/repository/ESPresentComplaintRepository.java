package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;

public interface ESPresentComplaintRepository extends ElasticsearchRepository<ESPresentComplaintDocument, String> {

}
