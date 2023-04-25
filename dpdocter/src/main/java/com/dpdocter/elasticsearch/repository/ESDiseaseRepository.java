package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDiseasesDocument;

public interface ESDiseaseRepository extends ElasticsearchRepository<ESDiseasesDocument, String> {


}
