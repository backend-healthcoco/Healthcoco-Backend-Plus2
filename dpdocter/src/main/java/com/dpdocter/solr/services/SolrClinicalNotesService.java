package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;

public interface SolrClinicalNotesService {

    boolean addComplaints(SolrComplaintsDocument request);

    boolean editComplaints(SolrComplaintsDocument request);

    boolean deleteComplaints(String id);

    List<SolrComplaintsDocument> searchComplaints(String searchTerm);

    boolean addDiagnoses(SolrDiagnosesDocument request);

    boolean editDiagnoses(SolrDiagnosesDocument request);

    boolean deleteDiagnoses(String id);

    List<SolrDiagnosesDocument> searchDiagnoses(String searchTerm);

    boolean addNotes(SolrNotesDocument request);

    boolean editNotes(SolrNotesDocument request);

    boolean deleteNotes(String id);

    List<SolrNotesDocument> searchNotes(String searchTerm);

    boolean addDiagrams(SolrDiagramsDocument request);

    boolean editDiagrams(SolrDiagramsDocument request);

    boolean deleteDiagrams(String id);

    List<SolrDiagramsDocument> searchDiagrams(String searchTerm);

    boolean addInvestigations(SolrInvestigationsDocument request);

    boolean editInvestigations(SolrInvestigationsDocument request);

    boolean deleteInvestigations(String id);

    List<SolrInvestigationsDocument> searchInvestigations(String searchTerm);

    boolean addObservations(SolrObservationsDocument request);

    boolean editObservations(SolrObservationsDocument request);

    boolean deleteObservations(String id);

    List<SolrObservationsDocument> searchObservations(String searchTerm);

}
