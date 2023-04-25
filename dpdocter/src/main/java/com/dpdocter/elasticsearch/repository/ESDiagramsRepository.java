package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDiagramsDocument;

public interface ESDiagramsRepository extends ElasticsearchRepository<ESDiagramsDocument, String> {

}
