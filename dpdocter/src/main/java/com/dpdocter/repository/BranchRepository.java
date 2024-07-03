package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.BranchCollection;

@Repository
public interface BranchRepository extends MongoRepository<BranchCollection, ObjectId>, PagingAndSortingRepository<BranchCollection, ObjectId> {

    public List<BranchCollection> findByDoctorId(ObjectId doctorId, Sort sort);

    public List<BranchCollection> findByDoctorIdAndDiscarded(ObjectId doctorId, boolean discarded, Sort sort);

    public List<BranchCollection> findByDoctorIdAndUpdatedTimeGreaterThan(ObjectId doctorId, Date date, Sort sort);

    public List<BranchCollection> findByDoctorIdAndUpdatedTimeGreaterThanAndDiscarded(ObjectId doctorId, Date date, boolean discarded, Sort sort);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Pageable pageRequest);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDiscarded(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded,
	    Pageable pageRequest);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThan(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, Pageable pageRequest);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDiscardedAndUpdatedTimeGreaterThan(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded, Date date,
	    Pageable pageRequest);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Sort sort);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDiscarded(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded, Sort sort);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThan(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Date date, Sort sort);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDiscardedAndUpdatedTimeGreaterThan(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded, Date date,
	    Sort sort);

    public BranchCollection findByNameAndDoctorIdAndLocationIdAndHospitalIdAndDiscarded(String name, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded);

    public List<BranchCollection> findByDoctorIdAndDiscardedInAndUpdatedTimeGreaterThan(ObjectId doctorId, boolean[] discarded, Date date, Pageable pageRequest);

    public List<BranchCollection> findByDoctorIdAndDiscardedInAndUpdatedTimeGreaterThan(ObjectId doctorId, boolean[] discarded, Date date, Sort sort);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDiscardedInAndUpdatedTimeGreaterThan(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean[] discarded, Date date, Pageable pageRequest);

    public List<BranchCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDiscardedInAndUpdatedTimeGreaterThan(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean[] discarded, Date date, Sort sort);

	public List<BranchCollection> findByIdInAndDoctorIdAndLocationIdAndHospitalIdAndDiscarded(Collection<ObjectId> groupIds, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded);

	public List<BranchCollection> findByIdInAndDoctorIdAndDiscarded(Collection<ObjectId> groupIds, ObjectId doctorId, boolean discarded);

}
