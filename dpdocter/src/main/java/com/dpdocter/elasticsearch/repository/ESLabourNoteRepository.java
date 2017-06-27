package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;

public interface ESLabourNoteRepository extends ElasticsearchRepository<EsLabourNoteDocument, String>{

}
