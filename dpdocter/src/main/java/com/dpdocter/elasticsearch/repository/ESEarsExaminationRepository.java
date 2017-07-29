package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;

public interface ESEarsExaminationRepository extends ElasticsearchRepository<ESEarsExaminationDocument, String>{

}
