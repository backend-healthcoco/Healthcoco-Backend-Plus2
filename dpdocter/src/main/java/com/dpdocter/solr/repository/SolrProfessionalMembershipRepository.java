package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrProfessionalMembershipDocument;

public interface SolrProfessionalMembershipRepository extends SolrCrudRepository<SolrProfessionalMembershipDocument, String> {

    @Query("updatedTime: {?0 TO *}")
    List<SolrProfessionalMembershipDocument> find(Date date, Pageable pageable);

    @Query("updatedTime: {?0 TO *}")
    List<SolrProfessionalMembershipDocument> find(Date date, Sort sort);

    @Query("updatedTime: {?0 TO *} AND membership: ?1*")
    List<SolrProfessionalMembershipDocument> find(Date date, String searchTerm, Pageable pageable);

    @Query("updatedTime: {?0 TO *} AND membership: ?1*")
    List<SolrProfessionalMembershipDocument> find(Date date, String searchTerm, Sort sort);

}
