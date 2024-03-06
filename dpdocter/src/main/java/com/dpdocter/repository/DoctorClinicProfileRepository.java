package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorClinicProfileCollection;

public interface DoctorClinicProfileRepository extends MongoRepository<DoctorClinicProfileCollection, ObjectId> {

	DoctorClinicProfileCollection findByUserLocationId(ObjectId userLocationId);

	List<DoctorClinicProfileCollection> findByDoctorId(ObjectId doctorId);

	List<DoctorClinicProfileCollection> findByLocationIdAndIsActivate(ObjectId locationId, Boolean isActivate);

	DoctorClinicProfileCollection findByDoctorIdAndLocationId(ObjectId doctorObjectId, ObjectId locationObjectId);

	List<DoctorClinicProfileCollection> findByLocationId(ObjectId objectId);

	DoctorClinicProfileCollection findByDoctorIdAndConsultationType(ObjectId doctorObjectId, String consultationType);

	DoctorClinicProfileCollection findByLocationIdAndIsSuperAdmin(ObjectId id, boolean b);
}
