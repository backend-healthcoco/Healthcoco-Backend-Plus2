package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ClinicalNotesCollection;

public interface ClinicalNotesRepository extends MongoRepository<ClinicalNotesCollection, ObjectId>,
		PagingAndSortingRepository<ClinicalNotesCollection, ObjectId> {

	// @Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}",
	// count = true)
	// Integer getClinicalNotesCount(ObjectId doctorId, ObjectId hospitalId,
	// ObjectId locationId);
	//
	// @Query(value = "{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2}")
	// List<ClinicalNotesCollection> getClinicalNotes(ObjectId doctorId,
	// ObjectId hospitalId, ObjectId locationId);
	//
	@Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
	Integer getClinicalNotesCountForOtherDoctors(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId,
			ObjectId locationId);

	@Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
	Integer getClinicalNotesCountForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

	@Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gt' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
	List<ClinicalNotesCollection> getClinicalNotes(ObjectId doctorId, ObjectId patientId, Date date, boolean[] discards,
			boolean[] inHistorys, Pageable pageable);

	@Query("{'doctorId' : ?0, 'patientId' : ?1, 'updatedTime' : {'$gt' : ?2}, 'discarded' : {$in: ?3}, 'inHistory' : {$in: ?4}}")
	List<ClinicalNotesCollection> getClinicalNotes(ObjectId doctorId, ObjectId patientId, Date date, boolean[] discards,
			boolean[] inHistorys, Sort sort);

	@Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gt' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
	List<ClinicalNotesCollection> getClinicalNotes(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId,
			ObjectId patientId, Date date, boolean[] discards, boolean[] inHistorys, Pageable pageable);

	@Query("{'doctorId' : ?0, 'hospitalId' : ?1, 'locationId' : ?2, 'patientId' : ?3, 'updatedTime' : {'$gt' : ?4}, 'discarded' : {$in: ?5}, 'inHistory' : {$in: ?6}}")
	List<ClinicalNotesCollection> getClinicalNotes(ObjectId doctorId, ObjectId hospitalId, ObjectId locationId,
			ObjectId patientId, Date date, boolean[] discards, boolean[] inHistorys, Sort sort);

	@Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
	List<ClinicalNotesCollection> getClinicalNotes(ObjectId patientId, Date date, boolean[] discards,
			boolean[] inHistorys, Pageable pageable);

	@Query("{'patientId' : ?0, 'updatedTime' : {'$gt' : ?1}, 'discarded' : {$in: ?2}, 'inHistory' : {$in: ?3}}")
	List<ClinicalNotesCollection> getClinicalNotes(ObjectId patientId, Date date, boolean[] discards,
			boolean[] inHistorys, Sort sort);

	@Query(value = "{'patientId': ?0, 'discarded' : ?1}", count = true)
	Integer getClinicalNotesCount(ObjectId patientId, boolean discarded);

	@Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'discarded' : ?4}", count = true)
	Integer getClinicalNotesCount(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId,
			boolean discarded);

	@Query("{'id': {$in: ?0}}")
	List<ClinicalNotesCollection> getClinicalNotesByIds(List<ObjectId> ids);

	@Query("{'doctorId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2, 'patientId' : ?3, 'createdTime' : ?4}")
	ClinicalNotesCollection find(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId,
			ObjectId userId, Date createdTime);
}
