package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;

public interface ESOralCavityThroatExaminationRepository extends ElasticsearchRepository<ESOralCavityAndThroatExaminationDocument, String>{

}
