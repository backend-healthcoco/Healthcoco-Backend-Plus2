package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;

public interface ESEducationInstituteRepository extends ElasticsearchRepository<ESEducationInstituteDocument, String> {

//    @Query("updatedTime: {?0 TO *}")
//    List<SolrEducationInstituteDocument> find(Date date, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *}")
//    List<SolrEducationInstituteDocument> find(Date date, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND name: ?1*")
//    List<SolrEducationInstituteDocument> find(Date date, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND name: ?1*")
//    List<SolrEducationInstituteDocument> find(Date date, String searchTerm, Sort sort);

}
