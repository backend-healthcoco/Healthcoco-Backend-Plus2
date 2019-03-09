package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESOfferDocument;

public interface ESOfferRepository extends ElasticsearchRepository<ESOfferDocument, String> {

}
