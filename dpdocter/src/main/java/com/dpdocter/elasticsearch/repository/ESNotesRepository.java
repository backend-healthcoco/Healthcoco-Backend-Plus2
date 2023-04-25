package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESNotesDocument;

public interface ESNotesRepository extends ElasticsearchRepository<ESNotesDocument, String> {
}
