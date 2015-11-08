package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.TagsCollection;

@Repository
public interface TagsRepository extends MongoRepository<TagsCollection, String> {
	@Query("{'doctorId':?0}")
    List<TagsCollection> findByDoctorId(String doctorId);

    @Query("{'doctorId':?0,'locationId':?1}")
    List<TagsCollection> findByDoctorIdAndlocationId(String doctorId, String locationId);

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2}")
    List<TagsCollection> findByDoctorIdAndlocationIdAndHospitalId(String doctorId, String locationId, String hospitalId);
}
