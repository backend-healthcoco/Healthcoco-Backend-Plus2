package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrPatientDocument;

public interface SolrPatientRepository extends SolrCrudRepository<SolrPatientDocument, String> {
    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND firstName:*?3* OR middleName:*?3* OR lastName:*?3* OR PID:*?3* OR mobileNumber:*?3* OR emailAddress:*?3* OR userName:*?3* OR city:*?3* OR locality:*?3* OR bloodGroup:*?3* OR referredBy:*?3* OR profession:*?3* OR postalCode:*?3* OR gender:*?3*")
    List<SolrPatientDocument> find(String doctorId, String locationId, String hospitalId, String searchTerm);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND firstName:*?3*")
    List<SolrPatientDocument> findByFirstName(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND middleName:*?3*")
    List<SolrPatientDocument> findByMiddleName(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND lastName:*?3*")
    List<SolrPatientDocument> findByLastName(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND PID:*?3*")
    List<SolrPatientDocument> findByPID(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND mobileNumber:*?3*")
    List<SolrPatientDocument> findByMobileNumber(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND emailAddress:*?3*")
    List<SolrPatientDocument> findByEmailAddress(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND userName:*?3*")
    List<SolrPatientDocument> findByUserName(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND city:*?3*")
    List<SolrPatientDocument> findByCity(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND locality:*?3*")
    List<SolrPatientDocument> findByLocality(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND bloodGroup:*?3*")
    List<SolrPatientDocument> findByBloodGroup(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND referredBy:*?3*")
    List<SolrPatientDocument> findByReferredBy(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND profession:*?3*")
    List<SolrPatientDocument> findByProfession(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND postalCode:*?3*")
    List<SolrPatientDocument> findByPostalCode(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND gender:?3")
    List<SolrPatientDocument> findByGender(String doctorId, String locationId, String hospitalId, String searchValue);

    @Query("userName:*?1*")
	SolrPatientDocument findByUserName(String username);

}
