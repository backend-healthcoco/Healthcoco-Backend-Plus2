package com.dpdocter.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDoctorDocument;

public interface SolrDoctorRepository extends SolrCrudRepository<SolrDoctorDocument, String> {
    @Override
    @Query("id : *?0*")
    SolrDoctorDocument findOne(String id);

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
	List<SolrDoctorDocument> findLabByCityLocationName(String city, String location, String locationId, boolean isLab);

    @Query("city : ?0* AND locationId : ?1 AND isLab : ?2")
	List<SolrDoctorDocument> findLabByCity(String city, String locationId, boolean isLab);

    @Query("city : ?0* AND (landmarkDetails: ?1* OR streetAddress: ?1* OR locality: ?1*) AND isLab : ?2")
	SolrDoctorDocument findLabByCityLocationName(String city, String location, boolean isLab);

    @Query("city : ?0* AND isLab : ?1")
	SolrDoctorDocument findLabByCity(String city, boolean isLab);

    @Query("city : *?0* AND firstName : *?1*")
	List<SolrDoctorDocument> findByCity(String city, String searchTerm);

    @Query("(landmarkDetails: ?0* OR streetAddress: ?0* OR locality: ?0*) AND firstName : ?1*")
	List<SolrDoctorDocument> findByLocation(String location, String searchTerm);

    @Query("city : ?0*")
	List<SolrDoctorDocument> findByCity(String city);

    @Query("(landmarkDetails: ?0* OR streetAddress: ?0* OR locality: ?0*)")
	List<SolrDoctorDocument> findByLocation(String location);

    @Query("city : *?0* AND locationName : *?1*")
	List<SolrDoctorDocument> findByCityLocationName(String city, String searchTerm);

    @Query("(landmarkDetails: ?0* OR streetAddress: ?0* OR locality: ?0*) AND locationName : ?1*")
	List<SolrDoctorDocument> findByLocationLocationName(String location, String searchTerm);

    @Query("!geofilt sfield='geoLocation'  pt=?0,?1 d=10 AND locationId : ?2 AND isLab : ?3 ")
	List<SolrDoctorDocument> findLabByLatLong(String latitude, String longitude, String locationId, boolean isLab);
}
