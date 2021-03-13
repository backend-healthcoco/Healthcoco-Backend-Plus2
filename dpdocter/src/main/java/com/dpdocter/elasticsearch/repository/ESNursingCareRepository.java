package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESNursingCareExaminationDocument;

public interface ESNursingCareRepository extends ElasticsearchRepository<ESNursingCareExaminationDocument, String> {

}
