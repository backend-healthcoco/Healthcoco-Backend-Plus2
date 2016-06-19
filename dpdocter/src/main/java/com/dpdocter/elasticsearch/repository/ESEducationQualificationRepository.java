package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;

public interface ESEducationQualificationRepository extends ElasticsearchRepository<ESEducationQualificationDocument, String> {

//    @Query("updatedTime: {?0 TO *}")
//    List<SolrEducationQualificationDocument> find(Date date, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *}")
//    List<SolrEducationQualificationDocument> find(Date date, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND name: ?1*")
//    List<SolrEducationQualificationDocument> find(Date date, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND name: ?1*")
//    List<SolrEducationQualificationDocument> find(Date date, String searchTerm, Sort sort);

}
