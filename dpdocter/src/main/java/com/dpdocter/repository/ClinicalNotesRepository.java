package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ClinicalNotesCollection;

public interface ClinicalNotesRepository extends MongoRepository<ClinicalNotesCollection, String>, PagingAndSortingRepository<ClinicalNotesCollection, String> {

//    @Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}", count = true)
//    Integer getClinicalNotesCount(String doctorId, String hospitalId, String locationId);
//
//    @Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}")
//    List<ClinicalNotesCollection> getClinicalNotes(String doctorId, String hospitalId, String locationId);
//
    @Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
    Integer getClinicalNotesCountForOtherDoctors(String doctorId, String id, String hospitalId, String locationId);

    @Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gt' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
	List<ClinicalNotesCollection> getClinicalNotes(String doctorId, String patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

    @Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gt' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
	List<ClinicalNotesCollection> getClinicalNotes(String doctorId, String patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

	@Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gt' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
	List<ClinicalNotesCollection> getClinicalNotes(String doctorId, String hospitalId, String locationId, String patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

	@Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gt' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
	List<ClinicalNotesCollection> getClinicalNotes(String doctorId, String hospitalId, String locationId, String patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

	@Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
	List<ClinicalNotesCollection> getClinicalNotes(String patientId, Date date, boolean[] discards,	boolean[] inHistorys, Pageable pageable);

	@Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
	List<ClinicalNotesCollection> getClinicalNotes(String patientId, Date date, boolean[] discards,	boolean[] inHistorys, Sort sort);

	@Query(value = "{'patientId': ?0, 'discarded' : ?1}", count = true)
	Integer getClinicalNotesCount(String patientId, boolean discarded);

	@Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'discarded' : ?4}", count = true)
	Integer getClinicalNotesCount(String doctorId, String patientId, String hospitalId, String locationId, boolean discarded);

}
