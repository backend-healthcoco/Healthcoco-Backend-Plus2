package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESProfessionDocument;

public interface ESProfessionRepository extends ElasticsearchRepository<ESProfessionDocument, String> {
	
//	@Query("{\"range\" : {\"updatedTime\" : {\"from\" : ?0}}}")
//    List<ESProfessionDocument> find(Date date, Pageable pageable);
//
//	@Query("{\"bool\": {\"must\": [{\"range\" : {\"updatedTime\" : {\"gte\" : ?0}}}]}}")
//    List<ESProfessionDocument> find(Date date, Sort sort);
//
//    @Query("{\"bool\": {\"must\": [{\"range\" : {\"updatedTime\" : {\"from\" : null,\"to\" : ?0,\"include_lower\" : true,\"include_upper\" : true}}},{\"match\": {\"profession\": \"?1*\"}}]}}")
//    List<ESProfessionDocument> find(Date date, String searchTerm, Pageable pageable);
//
//    @Query("{\"bool\": {\"must\": [{\"range\" : {\"updatedTime\" : {\"from\" : null,\"to\" : ?0,\"include_lower\" : true,\"include_upper\" : true}}},{\"match\": {\"profession\": \"?1*\"}}]}}")
//    List<ESProfessionDocument> find(Date date, String searchTerm, Sort sort);

}
