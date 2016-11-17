package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;

public interface ESProvisionalDiagnosisRepository extends ElasticsearchRepository<ESProvisionalDiagnosisDocument, String> {

}
