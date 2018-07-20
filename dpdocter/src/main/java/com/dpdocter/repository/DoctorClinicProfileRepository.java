package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorClinicProfileCollection;

public interface DoctorClinicProfileRepository extends MongoRepository<DoctorClinicProfileCollection, ObjectId> {@Query("{'userLocationId': ?0}")
	DoctorClinicProfileCollection findByUserLocationId(ObjectId userLocationId);
	
	@Query("{'locationId': ?0}")
	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId);
	
	@Query(value = "{'locationId' : ?0}", fields = "{'doctorId' : 1 }")
	List<DoctorClinicProfileCollection> findDoctorIdsByLocationId(ObjectId locationId);
	
	@Query("{'doctorId' : ?0, 'locationId': ?1}")
	DoctorClinicProfileCollection findByDoctorIdLocationId(ObjectId doctorId, ObjectId locationId);
	
	@Query(value = "{'doctorId' : ?0, 'isActivate' : true}", fields = "{'locationId' : 1 }")
	List<DoctorClinicProfileCollection> findLocationIdByDoctorIdAndIsActivate(ObjectId doctorId);
	
	@Query("{'doctorId' : ?0, 'isActivate' : true}")
	List<DoctorClinicProfileCollection> findByDoctorIdAndIsActivate(ObjectId doctorId);
	
	@Query("{'doctorId' : ?0}")
	List<DoctorClinicProfileCollection> findByDoctorId(ObjectId doctorId);
	
	@Query("{'locationId' : ?0,'isActivate' : ?1}")
	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId, Boolean isActivate);
	
	@Query("{'locationId' : ?0}")
	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId, Pageable pageRequest);
	
	@Query("{'locationId' : ?0}")
	List<DoctorClinicProfileCollection> findByLocationId(ObjectId locationId, Sort sort);
	
	@Query(value = "{'doctorId': ?0, 'locationId': ?1}", count = true)
	Integer countByUserIdAndLocationId(ObjectId id, ObjectId objectId);
}
