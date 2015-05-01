package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.GroupCollection;

@Repository
public interface GroupRepository extends MongoRepository<GroupCollection, String> {
	public List<GroupCollection> findByDoctorId(String doctorId);

	@Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2}")
	public List<GroupCollection> findByDoctorIdPatientIdHospitalId(String doctorId, String locationId, String hospitalId);
}
