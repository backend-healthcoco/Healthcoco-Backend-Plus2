package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESTrendingDocument;

public interface ESTrendingRepository extends ElasticsearchRepository<ESTrendingDocument, String>{

}
