package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESMedicalCouncilDocument;

public interface ESMedicalCouncilRepository extends ElasticsearchRepository<ESMedicalCouncilDocument, String> {

//    @Query("updatedTime: {?0 TO *}")
//    List<SolrMedicalCouncilDocument> find(Date date, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *}")
//    List<SolrMedicalCouncilDocument> find(Date date, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND medicalCouncil: ?1*")
//    List<SolrMedicalCouncilDocument> find(Date date, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND medicalCouncil: ?1*")
//    List<SolrMedicalCouncilDocument> find(Date date, String searchTerm, Sort sort);

}
