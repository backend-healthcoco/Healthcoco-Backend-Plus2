package com.dpdocter.elasticsearch.services;
import java.util.List;

import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;
import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;
import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;

public interface ESDischargeSummaryService {
	
	boolean addBabyNote(ESBabyNoteDocument request);

	boolean addOperationNote( ESOperationNoteDocument request);

	boolean addLabourNotes(EsLabourNoteDocument request);
	
	List<ESBabyNoteDocument> searchBabyNotes(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESOperationNoteDocument> searchOperationNotes(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<EsLabourNoteDocument> searchLabourNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
