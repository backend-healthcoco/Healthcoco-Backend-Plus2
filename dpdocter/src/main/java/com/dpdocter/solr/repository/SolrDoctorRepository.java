package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDoctorDocument;

public interface SolrDoctorRepository extends SolrCrudRepository<SolrDoctorDocument, String> {
    @Override
    @Query("id : *?0*")
    SolrDoctorDocument findOne(String id);

//    @Query("city : *?0* AND firstName : *?1* OR middleName : *?1* OR lastName : *?1* OR emailAddress : *?1* OR specialization : *?1*")
//    List<SolrDoctorDocument> findAll(String city, String doctor);
//
//    @Query("city : *?0* AND locations : *?1* AND firstName : *?2* OR middleName : *?2* OR lastName : *?2* OR emailAddress : *?2* OR specialization : *?2*")
//    List<SolrDoctorDocument> findAll(String city, String location, String searchTerm);

    @Query("userId : ?0 AND locationId : ?1")
    SolrDoctorDocument findByUserIdAndLocationId(String userId, String locationId);

    @Query("userId : ?0")
    List<SolrDoctorDocument> findByUserId(String doctorId);

    @Query("locationId : ?0")
	List<SolrDoctorDocument> findByLocationId(String locationId);

    @Query("firstName : ?0*")
	List<SolrDoctorDocument> findAll(String searchTerm);

    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10 AND firstName : ?2*")
	List<SolrDoctorDocument> findByLatitudeLongitude(String latitude, String longitude, String searchTerm);

    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND firstName : ?2*")
	List<SolrDoctorDocument> findByCityLocation(String city, String location, String searchTerm);

    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10")
	List<SolrDoctorDocument> findByLatitudeLongitude(String latitude, String longitude);

    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*)")
	List<SolrDoctorDocument> findByCityLocation(String city, String location);

    @Query("locationName : ?0*")
	List<SolrDoctorDocument> findByLocationName(String searchTerm);

    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10 AND locationName : ?2*")
	List<SolrDoctorDocument> findByLatitudeLongitudeLocation(String latitude, String longitude, String searchTerm);

    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND locationName : ?2*")
	List<SolrDoctorDocument> findByCityLocationName(String city, String location, String searchTerm);
    
    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND locationId : ?2 AND isLab : ?3")
	SolrDoctorDocument findLabByCityLocationName(String city, String location, String locationId, boolean isLab);

    @Query("city : ?0* AND locationId : ?1 AND isLab : ?2")
	SolrDoctorDocument findLabByCity(String city, String locationId, boolean isLab);

    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND isLab : ?2")
	SolrDoctorDocument findLabByCityLocationName(String city, String location, boolean isLab);

    @Query("city : ?0* AND isLab : ?1")
	SolrDoctorDocument findLabByCity(String city, boolean isLab);
}
