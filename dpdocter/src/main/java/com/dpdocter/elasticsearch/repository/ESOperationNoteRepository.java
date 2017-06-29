package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;

public interface ESOperationNoteRepository extends ElasticsearchRepository<ESOperationNoteDocument, String> {

}
