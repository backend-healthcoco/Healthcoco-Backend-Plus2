package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESObservationsDocument;

public interface ESObservationsRepository extends ElasticsearchRepository<ESObservationsDocument, String> {
}
