package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;

public interface ESPresentingComplaintEarsRepository extends ElasticsearchRepository<ESPresentingComplaintEarsDocument, String>{

}
