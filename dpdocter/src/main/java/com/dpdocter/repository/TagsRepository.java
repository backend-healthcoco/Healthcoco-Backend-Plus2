package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.TagsCollection;

@Repository
public interface TagsRepository extends MongoRepository<TagsCollection, ObjectId> {
    @Query("{'doctorId':?0}")
    List<TagsCollection> findByDoctorId(ObjectId doctorId);

    @Query("{'doctorId':?0,'locationId':?1}")
    List<TagsCollection> findByDoctorIdAndlocationId(ObjectId doctorId, ObjectId locationId);

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2}")
    List<TagsCollection> findByDoctorIdAndlocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
}
