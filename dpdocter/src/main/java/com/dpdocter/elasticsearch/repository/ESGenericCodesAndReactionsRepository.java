package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESGenericCodesAndReactions;

public interface ESGenericCodesAndReactionsRepository extends ElasticsearchRepository<ESGenericCodesAndReactions, String> {

}
