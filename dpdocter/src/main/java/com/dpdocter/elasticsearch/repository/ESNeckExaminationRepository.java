package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;

public interface ESNeckExaminationRepository extends ElasticsearchRepository<ESNeckExaminationDocument, String>{

}
