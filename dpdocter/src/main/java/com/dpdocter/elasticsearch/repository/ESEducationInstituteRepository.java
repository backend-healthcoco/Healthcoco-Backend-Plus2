package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;

public interface ESEducationInstituteRepository extends ElasticsearchRepository<ESEducationInstituteDocument, String> {


}
