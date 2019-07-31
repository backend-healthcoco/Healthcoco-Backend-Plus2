package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InventorySettingsCollection;

public interface InventorySettingRepository extends MongoRepository<InventorySettingsCollection, ObjectId>{

	public InventorySettingsCollection findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
}
