package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;

public interface ESDiagnosticTestRepository extends ElasticsearchRepository<ESDiagnosticTestDocument, String> {

    @Query("{\"bool\": {\"must\": [{\"match_phrase_prefix\": {\"testName\": \"?0*\"}}]}}")
    List<ESDiagnosticTestDocument> findByTestName(String searchTerm);

}
