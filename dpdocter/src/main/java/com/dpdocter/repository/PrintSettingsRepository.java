package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PrintSettingsCollection;

public interface PrintSettingsRepository extends MongoRepository<PrintSettingsCollection, ObjectId>,
		PagingAndSortingRepository<PrintSettingsCollection, ObjectId> {

	List<PrintSettingsCollection> findByDoctorIdAndUpdatedTimegreaterThanAndDiscardedIn(ObjectId doctorId, Date date, boolean[] discards, Pageable pageable);

	List<PrintSettingsCollection> findByDoctorIdAndUpdatedTimegreaterThanAndDiscardedIn(ObjectId doctorId, Date date, boolean[] discards, Sort sort);

	List<PrintSettingsCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimegreaterThanAndDiscardedIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			boolean[] discards, Sort sort);

	List<PrintSettingsCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimegreaterThanAndDiscardedIn(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date,
			boolean[] discards, Pageable pageable);

	List<PrintSettingsCollection> findByLocationIdAndHospitalIdAndUpdatedTimegreaterThanAndDiscardedIn(ObjectId locationId, ObjectId hospitalId, Date date, boolean[] discards,
			Sort sort);

	PrintSettingsCollection findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type);

	PrintSettingsCollection findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	PrintSettingsCollection findByLocationIdAndHospitalId(ObjectId locationId, ObjectId hospitalId);

	List<PrintSettingsCollection> findByLocationId(ObjectId locationId);

}
