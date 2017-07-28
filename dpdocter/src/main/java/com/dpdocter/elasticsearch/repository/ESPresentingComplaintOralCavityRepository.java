package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;

public interface ESPresentingComplaintOralCavityRepository extends ElasticsearchRepository<ESPresentingComplaintOralCavityDocument, String>{

}
