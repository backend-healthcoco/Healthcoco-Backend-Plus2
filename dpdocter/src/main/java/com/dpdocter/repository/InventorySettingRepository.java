package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.InventorySettingsCollection;

public interface InventorySettingRepository extends MongoRepository<InventorySettingsCollection, ObjectId>{

	@Query("{'doctorId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
    public InventorySettingsCollection findByDoctorIdPatientIdHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
}
