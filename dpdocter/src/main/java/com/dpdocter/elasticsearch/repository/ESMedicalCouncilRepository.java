package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESMedicalCouncilDocument;

public interface ESMedicalCouncilRepository extends ElasticsearchRepository<ESMedicalCouncilDocument, String> {

}
