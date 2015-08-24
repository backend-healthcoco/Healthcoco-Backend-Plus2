package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDoctorDocument;

public interface SolrDoctorRepository extends SolrCrudRepository<SolrDoctorDocument, String> {
    @Query("id : *?0*")
    SolrDoctorDocument findOne(String id);

    @Query("firstName : *?0* OR middleName : *?0* OR lastName : *?0* OR emailAddress : *?0* OR specialization : *?0* AND locations : *?1*")
    List<SolrDoctorDocument> findAll(String doctor, String city);

    @Query("firstName : *?0* OR middleName : *?0* OR lastName : *?0* OR emailAddress : *?0* OR specialization : *?0* AND locations : *?1* AND locations : *?2*")
    List<SolrDoctorDocument> findAll(String searchTerm, String location, String city);
}
