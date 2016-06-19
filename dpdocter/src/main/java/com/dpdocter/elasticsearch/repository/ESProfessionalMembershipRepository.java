package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;

public interface ESProfessionalMembershipRepository extends ElasticsearchRepository<ESProfessionalMembershipDocument, String> {

//    @Query("updatedTime: {?0 TO *}")
//    List<SolrProfessionalMembershipDocument> find(Date date, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *}")
//    List<SolrProfessionalMembershipDocument> find(Date date, Sort sort);
//
//    @Query("updatedTime: {?0 TO *} AND membership: ?1*")
//    List<SolrProfessionalMembershipDocument> find(Date date, String searchTerm, Pageable pageable);
//
//    @Query("updatedTime: {?0 TO *} AND membership: ?1*")
//    List<SolrProfessionalMembershipDocument> find(Date date, String searchTerm, Sort sort);

}
