package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DrugStrengthUnitCollection;
import com.dpdocter.collections.LocationCollection;

@Repository
public interface LocationRepository extends MongoRepository<LocationCollection, String> {

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findClinics(String hospitalId, boolean isClinic, Pageable pageable);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findClinics(String hospitalId, boolean isClinic, Sort sort);

	@Query("{'isClinic':?0}")
	List<LocationCollection> findClinics(boolean isClinic, Pageable pageable);

	@Query("{'isClinic':?0}")
	List<LocationCollection> findClinics(boolean isClinic, Sort sort);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findLabs(String hospitalId, boolean isLab, Pageable pageable);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findLabs(String hospitalId, boolean isLab, Sort sort);

	@Query("{'isLab':?0}")
	List<LocationCollection> findLabs(boolean isLab, Pageable pageable);

	@Query("{'isLab':?0}")
	List<LocationCollection> findLabs(boolean isLab, Sort sort);

	@Query("{'isClinic':?0, 'isLab':?1}")
	List<LocationCollection> findClinicsAndLabs(Boolean isClinic, Boolean isLab, Pageable pageable);

	@Query("{'isClinic':?0, 'isLab':?1}")
	List<LocationCollection> findClinicsAndLabs(Boolean isClinic, Boolean isLab, Sort sort);

	@Query("{'hospitalId':?0, 'isClinic':?1, 'isLab':?2}")
	List<LocationCollection> findClinicsAndLabs(String hospitalId, Boolean isClinic, Boolean isLab, Pageable pageable);

	@Query("{'hospitalId':?0, 'isClinic':?1, 'isLab':?2}")
	List<LocationCollection> findClinicsAndLabs(String hospitalId, Boolean isClinic, Boolean isLab, Sort sort);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findByHospitalId(String hospitalId, Pageable pageable);

	@Query("{'hospitalId':?0}")
	List<LocationCollection> findByHospitalId(String hospitalId, Sort sort);

	@Query("{'$or': [{'isClinic':?0, 'isLab':?1, 'locationName' : {$regex : '^?2*', $options : 'i'}},{'isClinic':?0, 'isLab':?1, 'locationEmailAddress' : {$regex : '^?2*', $options : 'i'}}]}")
	List<LocationCollection> findClinicsAndLabs(Boolean isClinic, Boolean isLab, String searchTerm,	Pageable pageable);

	@Query("{'$or': [{'isClinic':?0, 'isLab':?1, 'locationName' : {$regex : '^?2*', $options : 'i'}},{'isClinic':?0, 'isLab':?1, 'locationEmailAddress' : {$regex : '^?2*', $options : 'i'}}]}")
	List<LocationCollection> findClinicsAndLabs(Boolean isClinic, Boolean isLab, String searchTerm, Sort sort);

	@Query("{'$or': [{'isClinic':?0, 'locationName' : {$regex : '^?1*', $options : 'i'}},{'isClinic':?0, 'locationEmailAddress' : {$regex : '^?1*', $options : 'i'}}]}")
	List<LocationCollection> findClinics(Boolean isClinic, String searchTerm, Pageable pageable);

	@Query("{'$or': [{'isClinic':?0, 'locationName' : {$regex : '^?1*', $options : 'i'}},{'isClinic':?0, 'locationEmailAddress' : {$regex : '^?1*', $options : 'i'}}]}")
	List<LocationCollection> findClinics(Boolean isClinic, String searchTerm, Sort sort);

	@Query("{'$or': [{'isLab':?0, 'locationName' : {$regex : '^?1*', $options : 'i'}},{'isLab':?0, 'locationEmailAddress' : {$regex : '^?1*', $options : 'i'}}]}")
	List<LocationCollection> findLabs(Boolean isLab, String searchTerm, Pageable pageable);

	@Query("{'$or': [{'isLab':?0, 'locationName' : {$regex : '^?1*', $options : 'i'}},{'isLab':?0, 'locationEmailAddress' : {$regex : '^?1*', $options : 'i'}}]}")
	List<LocationCollection> findLabs(Boolean isLab, String searchTerm, Sort sort);

	@Query("{'$or': [{'locationName' : {$regex : '^?0*', $options : 'i'}},{'locationEmailAddress' : {$regex : '^?0*', $options : 'i'}}]}")
	List<LocationCollection> findByNameOrEmailAddress(String searchTerm, Pageable pageable);

	@Query("{'$or': [{'locationName' : {$regex : '^?0*', $options : 'i'}},{'locationEmailAddress' : {$regex : '^?0*', $options : 'i'}}]}")
	List<LocationCollection> findByNameOrEmailAddress(String searchTerm, Sort sort);

	@Query("{'$or': [{'hospitalId':?0, 'isClinic':?1, 'isLab':?2, 'locationName' : {$regex : '^?3*', $options : 'i'}},{'hospitalId':?0, 'isClinic':?1, 'isLab':?2, 'locationEmailAddress' : {$regex : '^?3*', $options : 'i'}}]}")
	List<LocationCollection> findClinicsAndLabs(String hospitalId, Boolean isClinic, Boolean isLab, String searchTerm, Pageable pageable);

	@Query("{'$or': [{'hospitalId':?0, 'isClinic':?1, 'isLab':?2, 'locationName' : {$regex : '^?3*', $options : 'i'}},{'hospitalId':?0, 'isClinic':?1, 'isLab':?2, 'locationEmailAddress' : {$regex : '^?3*', $options : 'i'}}]}")
	List<LocationCollection> findClinicsAndLabs(String hospitalId, Boolean isClinic, Boolean isLab, String searchTerm, Sort sort);

	@Query("{'$or': [{'hospitalId':?0, 'isClinic':?1, 'locationName' : {$regex : '^?2*', $options : 'i'}},{'hospitalId':?0, 'isClinic':?1, 'locationEmailAddress' : {$regex : '^?2*', $options : 'i'}}]}")
	List<LocationCollection> findClinics(String hospitalId, Boolean isClinic, String searchTerm, Pageable pageable);

	@Query("{'$or': [{'hospitalId':?0, 'isClinic':?1, 'locationName' : {$regex : '^?2*', $options : 'i'}},{'hospitalId':?0, 'isClinic':?1, 'locationEmailAddress' : {$regex : '^?2*', $options : 'i'}}]}")
	List<LocationCollection> findClinics(String hospitalId, Boolean isClinic, String searchTerm, Sort sort);

	@Query("{'$or': [{'hospitalId':?0, 'isLab':?1, 'locationName' : {$regex : '^?2*', $options : 'i'}},{'hospitalId':?0, 'isLab':?1, 'locationEmailAddress' : {$regex : '^?2*', $options : 'i'}}]}")
	List<LocationCollection> findLabs(String hospitalId, Boolean isLab, String searchTerm, Pageable pageable);

	@Query("{'$or': [{'hospitalId':?0, 'isLab':?1, 'locationName' : {$regex : '^?2*', $options : 'i'}},{'hospitalId':?0, 'isLab':?1, 'locationEmailAddress' : {$regex : '^?2*', $options : 'i'}}]}")
	List<LocationCollection> findLabs(String hospitalId, Boolean isLab, String searchTerm, Sort sort);

	@Query("{'$or': [{'hospitalId':?0, 'locationName' : {$regex : '^?1*', $options : 'i'}},{'hospitalId':?0, 'locationEmailAddress' : {$regex : '^?1*', $options : 'i'}}]}")
	List<LocationCollection> findByNameOrEmailAddressAndHospitalId(String hospitalId, String searchTerm, Pageable pageable);

	@Query("{'$or': [{'hospitalId':?0, 'locationName' : {$regex : '^?1*', $options : 'i'}},{'hospitalId':?0, 'locationEmailAddress' : {$regex : '^?1*', $options : 'i'}}]}")
	List<LocationCollection> findByNameOrEmailAddressAndHospitalId(String hospitalId, String searchTerm, Sort sort);

	@Override
    Page<LocationCollection> findAll(Pageable pageable);

}
