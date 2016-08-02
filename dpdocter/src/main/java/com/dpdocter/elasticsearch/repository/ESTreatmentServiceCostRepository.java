package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;

public interface ESTreatmentServiceCostRepository extends ElasticsearchRepository<ESTreatmentServiceCostDocument, String> {

}
