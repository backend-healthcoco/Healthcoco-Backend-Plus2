package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;

public interface ESDiagnosesRepository extends ElasticsearchRepository<ESDiagnosesDocument, String> {
}
