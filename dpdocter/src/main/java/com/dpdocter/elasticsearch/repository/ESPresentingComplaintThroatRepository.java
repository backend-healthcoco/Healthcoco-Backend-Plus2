package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;

public interface ESPresentingComplaintThroatRepository extends ElasticsearchRepository<ESPresentingComplaintThroatDocument, String>{

}
