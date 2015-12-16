package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDoctorDocument;

public interface SolrDoctorRepository extends SolrCrudRepository<SolrDoctorDocument, String> {
    @Override
    @Query("id : *?0*")
    SolrDoctorDocument findOne(String id);

    @Query("city : *?0* AND firstName : *?1* OR middleName : *?1* OR lastName : *?1* OR emailAddress : *?1* OR specialization : *?1*")
    List<SolrDoctorDocument> findAll(String city, String doctor);

    @Query("city : *?0* AND locations : *?1* AND firstName : *?2* OR middleName : *?2* OR lastName : *?2* OR emailAddress : *?2* OR specialization : *?2*")
    List<SolrDoctorDocument> findAll(String city, String location, String searchTerm);

    @Query("userId : ?0 AND locationId : ?1")
	SolrDoctorDocument findByUserIdAndLocationId(String userId, String locationId);

    @Query("userId : ?0 ")
    List<SolrDoctorDocument> findByUserId(String doctorId);
}
