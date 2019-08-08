package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.LabTestCollection;

public interface LabTestRepository extends MongoRepository<LabTestCollection, ObjectId>, PagingAndSortingRepository<LabTestCollection, ObjectId> {

	List<LabTestCollection> findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId hospitalId, ObjectId locationId, Date date, List<Boolean> discards, Pageable pageable);

    List<LabTestCollection> findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(ObjectId hospitalId, ObjectId locationId, Date date, List<Boolean> discards, Sort sort);

    List<LabTestCollection> findByUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Pageable pageable);

    List<LabTestCollection> findByUpdatedTimeGreaterThanAndDiscardedIn(Date date, boolean[] discards, Sort sort);
}
