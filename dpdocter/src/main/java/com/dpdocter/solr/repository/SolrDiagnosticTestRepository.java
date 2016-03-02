package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagnosticTestDocument;

public interface SolrDiagnosticTestRepository extends SolrCrudRepository<SolrDiagnosticTestDocument, String> {

    @Query("testName : ?0*")
    List<SolrDiagnosticTestDocument> findAll(String searchTerm);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, Pageable pageable);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, Sort sort);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
    List<SolrDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, String searchTerm, Pageable pageable);

    @Query("(locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
    List<SolrDiagnosticTestDocument> getGlobalDiagnosticTests(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
    List<SolrDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
    List<SolrDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*")
    List<SolrDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm,
	    Pageable pageable);

    @Query("locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*")
    List<SolrDiagnosticTestDocument> getCustomDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, Pageable pageable);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, Sort sort);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Pageable pageable);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, String searchTerm, Pageable pageable);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND testName:?2*")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(Date date, boolean discarded, String searchTerm, Sort sort);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*)")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm,
	    Pageable pageable);

    @Query("(locationId:*?0* AND hospitalId:*?1* AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName:?4*) OR ((locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND testName: ?4*)")
    List<SolrDiagnosticTestDocument> getCustomGlobalDiagnosticTests(String locationId, String hospitalId, Date date, boolean discarded, String searchTerm,
	    Sort sort);
}
