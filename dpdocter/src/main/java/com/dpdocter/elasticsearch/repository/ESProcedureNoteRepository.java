package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;

public interface ESProcedureNoteRepository extends ElasticsearchRepository<ESProcedureNoteDocument, String> {

}
