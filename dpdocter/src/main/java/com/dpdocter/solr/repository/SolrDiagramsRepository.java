package com.dpdocter.solr.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.dpdocter.solr.document.SolrDiagramsDocument;

public interface SolrDiagramsRepository extends SolrCrudRepository<SolrDiagramsDocument, String> {

    @Query("speciality:?0* OR tags:?0*")
    public List<SolrDiagramsDocument> find(String searchTerm);

    @Query("speciality:?0*")
    public List<SolrDiagramsDocument> findBySpeciality(String searchTerm);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(Date date, Boolean discarded, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false)")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(Date date, Boolean discarded, Sort sort);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND (speciality:?2* OR tags:?2*)")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("updatedTime: {?0 TO *} AND ( discarded: ?1 OR discarded:false) AND (speciality:?2* OR tags:?2*)")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("( doctorId:*?0* AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String specialities, Date date, Boolean discarded, Pageable pageRequest);

    @Query("( doctorId:*?0* AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String specialities, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded,
	    Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId:*?0* AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)  AND (speciality:?4* OR tags:?4*)) OR ((doctorId: null OR doctorId: \"\") AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)  AND (speciality:?4* OR tags:?4*))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String specialities, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)  AND (speciality:?4* OR tags:?4*)) OR ((doctorId: null OR doctorId: \"\") AND speciality : ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)  AND (speciality:?4* OR tags:?4*))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String specialities, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false) AND (speciality:?6* OR tags:?6*)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false) AND (speciality:?6* OR tags:?6*))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded,
	    String searchTerm, Pageable pageRequest);

    @Query("(doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false) AND (speciality:?6* OR tags:?6*)) OR ((doctorId: null OR doctorId: \"\") AND (locationId: null OR locationId: \"\") AND (hospitalId: null OR hospitalId: \"\") AND speciality : ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false) AND (speciality:?6* OR tags:?6*))")
    public List<SolrDiagramsDocument> findCustomGlobalDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded,
	    String searchTerm, Sort sort);

    @Query("(doctorId: null OR doctorId: \"\") AND speciality: ?0 AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrDiagramsDocument> findGlobalDiagrams(String specialities, Date date, Boolean discarded, Pageable pageRequest);

    @Query("(doctorId: null OR doctorId: \"\") AND speciality: ?0 AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false)")
    public List<SolrDiagramsDocument> findGlobalDiagrams(String specialities, Date date, Boolean discarded, Sort sort);

    @Query("(doctorId: null OR doctorId: \"\") AND speciality: ?0 AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (speciality:?3* OR tags:?3*)")
    public List<SolrDiagramsDocument> findGlobalDiagrams(String specialities, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("(doctorId: null OR doctorId: \"\") AND speciality: ?0 AND updatedTime: {?1 TO *} AND ( discarded: ?2 OR discarded:false) AND (speciality:?3* OR tags:?3*)")
    public List<SolrDiagramsDocument> findGlobalDiagrams(String specialities, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND speciality:  ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String specialities, Date date, Boolean discarded, Pageable pageRequest);

    @Query("doctorId:*?0* AND speciality:  ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String specialities, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality:  ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded,
	    Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality: ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded, Sort sort);

    @Query("doctorId:*?0* AND speciality: ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND (speciality:?4* OR tags:?4*)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String specialities, Date date, Boolean discarded, String searchTerm, Pageable pageRequest);

    @Query("doctorId:*?0* AND speciality: ?1 AND updatedTime: {?2 TO *} AND ( discarded: ?3 OR discarded:false) AND (speciality:?4* OR tags:?4*)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String specialities, Date date, Boolean discarded, String searchTerm, Sort sort);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality: ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false) AND (speciality:?6* OR tags:?6*)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded, String searchTerm,
	    Pageable pageRequest);

    @Query("doctorId:*?0* AND locationId:*?1* AND hospitalId:*?2* AND speciality: ?3 AND updatedTime: {?4 TO *} AND ( discarded: ?5 OR discarded:false) AND (speciality:?6* OR tags:?6*)")
    public List<SolrDiagramsDocument> findCustomDiagrams(String doctorId, String locationId, String hospitalId, String specialities, Date date, Boolean discarded, String searchTerm,
	    Sort sort);
}
