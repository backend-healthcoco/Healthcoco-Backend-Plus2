package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;

public interface ESXRayDetailsRepository extends ElasticsearchRepository<ESXRayDetailsDocument, String> {

}
