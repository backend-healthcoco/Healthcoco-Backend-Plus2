package com.dpdocter.solr.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrLabTestDocument;

public interface SolrLabTestRepository extends SolrCrudRepository<SolrLabTestDocument, String> {

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Collection<String> testIds, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
    List<SolrLabTestDocument> getCustomGlobalLabTests(Date date, boolean discarded, Collection<String> testIds, Sort sort);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageRequest);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4}) OR (((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4})")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds,
	    Pageable pageRequest);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4}) OR (((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4})")
    List<SolrLabTestDocument> getCustomGlobalLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds,
	    Sort sort);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Pageable pageRequest);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Sort sort);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Collection<String> testIds, Pageable pageRequest);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testId:{$in: ?2}")
    List<SolrLabTestDocument> getGlobalLabTests(Date date, Boolean discarded, Collection<String> testIds, Sort sort);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
    List<SolrLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageRequest);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
    List<SolrLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:{$in: ?4}")
    List<SolrLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds,
	    Pageable pageRequest);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testId:{$in: ?4}")
    List<SolrLabTestDocument> getCustomLabTests(String locationId, String hospitalId, Date date, boolean discarded, Collection<String> testIds, Sort sort);

    @Query("testId: ?0")
    List<SolrLabTestDocument> findByTestId(String testId);

    @Query("testId:  ?0")
    List<SolrLabTestDocument> findByTestIds(String string);

}
