package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;

public interface ESPresentingComplaintNoseRepository extends ElasticsearchRepository<ESPresentingComplaintNoseDocument, String>{

}
