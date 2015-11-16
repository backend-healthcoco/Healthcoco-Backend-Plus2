package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrLabTestDocument;

public interface SolrLabTestRepository extends SolrCrudRepository<SolrLabTestDocument, String> {

    @Query("testName:*?0*")
    List<SolrLabTestDocument> find(String searchTerm);

    @Query("updatedTime: {'$gte': ?0} AND discarded: {$in: ?1} AND testName:?2}")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean[] discards, Pageable pageRequest);

    @Query("updatedTime: {'$gte': ?0} AND discarded: {$in: ?1} AND testName:?2}")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean[] discards, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:*?2*")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:*?2*")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, Date date, boolean discarded, Pageable pageRequest);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId:\"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId:\"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageRequest);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND testName:*?3*) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND testName:*?3*)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND testName:*?3*) OR (doctorId:\"\" AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND testName:*?3*)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND testName:*?5*) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId: \"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND testName:*?5*)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND testName:*?5*) OR (doctorId:\"\" AND locationId:\"\" AND hospitalId: \"\" AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND testName:*?5*)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Sort sort);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Pageable pageRequest);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Sort sort);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:*?2*")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId: \"\" AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:*?2*")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, Date date, boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, Date date, boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND testName:*?3*")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, Date date, boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND testName:*?3*")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND testName:*?5*")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND testName:*?5*")
    List<SolrLabTestDocument> getCustomLabTests(String doctorId, String hospitalId, String locationId, Date date, boolean discarded, String searchTerm,
	    Sort sort);

}
