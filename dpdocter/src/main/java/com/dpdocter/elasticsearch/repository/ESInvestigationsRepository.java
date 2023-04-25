package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;

public interface ESInvestigationsRepository extends ElasticsearchRepository<ESInvestigationsDocument, String> {
}
