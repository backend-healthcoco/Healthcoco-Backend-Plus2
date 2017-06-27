package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;

public interface ESBabyNoteRepository extends ElasticsearchRepository<ESBabyNoteDocument, String> {

}
