package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;

public interface ESNoseExaminationRepository extends ElasticsearchRepository<ESNoseExaminationDocument, String>{

}
