package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrintSettingsCollection;

public interface PrintSettingsRepository extends MongoRepository<PrintSettingsCollection, ObjectId>,
		PagingAndSortingRepository<PrintSettingsCollection, ObjectId> {

	List<PrintSettingsCollection> findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, Date date, List<Boolean> discards, Pageable pageable);

	List<PrintSettingsCollection> findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, Date date, List<Boolean> discards, Sort sort);

	List<PrintSettingsCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			List<Boolean> discards, Sort sort);

	List<PrintSettingsCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			List<Boolean> discards, Pageable pageable);

	List<PrintSettingsCollection> findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId locationId, ObjectId hospitalId, Date date, List<Boolean> discards,
			Sort sort);

	PrintSettingsCollection findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type, String printSettingType);
	
	List<PrintSettingsCollection> findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type, String printSettingType,Sort sort);
	
	PrintSettingsCollection findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	PrintSettingsCollection findByLocationIdAndHospitalId(ObjectId locationId, ObjectId hospitalId);
	
	PrintSettingsCollection findByDoctorIdAndLocationIdAndHospitalIdAndPrintSettingType(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,String printSettingType);

	List<PrintSettingsCollection> findByLocationId(ObjectId locationId);

	PrintSettingsCollection findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeAndDiscardedAndPrintSettingTypeIn(
			ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, Boolean discarded, String printSettingType);

}
