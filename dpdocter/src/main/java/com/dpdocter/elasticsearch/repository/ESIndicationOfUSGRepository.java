package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;

public interface ESIndicationOfUSGRepository extends ElasticsearchRepository<ESIndicationOfUSGDocument, String> {

}
