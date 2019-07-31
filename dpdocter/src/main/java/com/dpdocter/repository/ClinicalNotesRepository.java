package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ClinicalNotesCollection;

public interface ClinicalNotesRepository extends MongoRepository<ClinicalNotesCollection, ObjectId>,
		PagingAndSortingRepository<ClinicalNotesCollection, ObjectId> {

	@Query(value = "{'doctorId' : {'$ne' : ?0}, 'patientId': ?1, 'hospitalId' : {'$ne' : ?2}, 'locationId' : {'$ne' : ?3}}", count = true)
	Integer getClinicalNotesCountForOtherDoctors(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId,
			ObjectId locationId);

	@Query(value = "{'patientId': ?0, 'hospitalId' : {'$ne' : ?1}, 'locationId' : {'$ne' : ?2}}", count = true)
	Integer getClinicalNotesCountForOtherLocations(ObjectId patientId, ObjectId hospitalId, ObjectId locationId);

	@Query(value = "{'patientId': ?0, 'discarded' : ?1}", count = true)
	Integer getClinicalNotesCount(ObjectId patientId, boolean discarded);

	@Query(value = "{'doctorId' : ?0, 'patientId': ?1, 'hospitalId' : ?2, 'locationId' : ?3, 'discarded' : ?4}", count = true)
	Integer getClinicalNotesCount(ObjectId doctorId, ObjectId patientId, ObjectId hospitalId, ObjectId locationId,
			boolean discarded);

	List<ClinicalNotesCollection> findAllByIds(List<ObjectId> ids);

	ClinicalNotesCollection findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndCreatedTime(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId,
			ObjectId userId, Date createdTime);
}
