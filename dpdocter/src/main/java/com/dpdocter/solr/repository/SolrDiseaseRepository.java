package com.dpdocter.solr.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiseasesDocument;

public interface SolrDiseaseRepository extends SolrCrudRepository<SolrDiseasesDocument, String> {

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND disease:?3*")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageable);

    @Query("doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND disease:?3*")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND disease:?5*")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
	    Pageable pageable);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND disease:?5*")
    List<SolrDiseasesDocument> findCustomDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
	    Sort sort);

    @Query(" (doctorId: \"\" OR doctorId:null) AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiseasesDocument> findGlobalDiseases(Date date, Boolean discarded, Pageable pageable);

    @Query(" (doctorId: \"\" OR doctorId:null) AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiseasesDocument> findGlobalDiseases(Date date, Boolean discarded, Sort sort);

    @Query(" (doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND disease:?2*")
    List<SolrDiseasesDocument> findGlobalDiseases(Date date, Boolean discarded, String searchTerm, Pageable pageable);

    @Query(" (doctorId: null OR doctorId: \"\") AND updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND disease:?2*")
    List<SolrDiseasesDocument> findGlobalDiseases(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(Date date, Boolean discarded, Pageable pageable);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(Date date, Boolean discarded, Sort sort);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, Date date, Boolean discarded, Pageable pageable);

    @Query("( doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false))")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Pageable pageable);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false)) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false))")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND disease:?2*")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(Date date, Boolean discarded, String searchTerm, Pageable pageable);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND disease:?2*")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND disease:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND disease:?3*)")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, Date date, Boolean discarded, String searchTerm, Pageable pageable);

    @Query("(doctorId:*?0* AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND disease:?3*) OR ((doctorId: null OR doctorId: \"\") AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND disease:?3*)")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND disease:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND disease:?5*)")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
	    Pageable pageable);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND disease:?5*) OR ( (doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND updatedTime: {?3 TO *} AND ( discarded: ?4 OR discarded:false) AND disease:?5*)")
    List<SolrDiseasesDocument> findCustomGlobalDiseases(String doctorId, String locationId, String hospitalId, Date date, Boolean discarded, String searchTerm,
	    Sort sort);

}
