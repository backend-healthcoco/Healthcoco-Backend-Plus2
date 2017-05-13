package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;

public interface ESCollectionBoyRepository extends ElasticsearchRepository<ESCollectionBoyDocument, String>{

}
