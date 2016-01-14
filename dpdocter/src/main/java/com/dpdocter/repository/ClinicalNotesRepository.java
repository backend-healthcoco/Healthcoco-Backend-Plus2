package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ClinicalNotesCollection;

public interface ClinicalNotesRepository extends MongoRepository<ClinicalNotesCollection, String>, PagingAndSortingRepository<ClinicalNotesCollection, String> {

    @Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}", count = true)
    Integer getClinicalNotesCount(String doctorId, String hospitalId, String locationId);

    @Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}")
    List<ClinicalNotesCollection> getClinicalNotes(String doctorId, String hospitalId, String locationId);

    @Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
    Integer getClinicalNotesCountForOtherDoctors(String doctorId, String id, String hospitalId, String locationId);

}
