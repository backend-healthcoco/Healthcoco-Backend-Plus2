package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;

public interface ESDentalWorksRepository extends ElasticsearchRepository<ESDentalWorksDocument, String>{

}
