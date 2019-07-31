package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.GroupCollection;

@Repository
public interface GroupRepository extends MongoRepository<GroupCollection, ObjectId>, PagingAndSortingRepository<GroupCollection, ObjectId> {

    public List<GroupCollection> findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, Sort sort);

    public GroupCollection findByNameAndDoctorIdAndLocationIdAndHospitalIdAndDiscarded(String name, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, boolean discarded);

}
