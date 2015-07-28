package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrPatientDocument;

public interface SolrPatientRepository extends SolrCrudRepository<SolrPatientDocument, String> {
    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND firstName:*?3* OR middleName:*?3* OR lastName:*?3* OR PID:*?3* OR mobileNumber:*?3* OR emailAddress:*?3* OR userName:*?3* OR city:*?3* OR locality:*?3* OR bloodGroup:*?3* OR referredBy:*?3* OR profession:*?3* OR postalCode:*?3* OR gender:*?3*")
    List<SolrPatientDocument> find(String doctorId, String locationId, String hospitalId, String searchTerm);

}
