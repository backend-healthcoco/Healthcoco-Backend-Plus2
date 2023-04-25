package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESReferenceDocument;

public interface ESReferenceRepository extends ElasticsearchRepository<ESReferenceDocument, String> {

}
