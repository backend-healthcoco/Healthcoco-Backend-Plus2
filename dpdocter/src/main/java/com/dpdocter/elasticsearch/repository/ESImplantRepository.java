package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESImplantDocument;

public interface ESImplantRepository extends ElasticsearchRepository<ESImplantDocument, String> {

}
