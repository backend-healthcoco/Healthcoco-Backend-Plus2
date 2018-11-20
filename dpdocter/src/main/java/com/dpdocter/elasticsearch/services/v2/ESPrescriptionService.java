package com.dpdocter.elasticsearch.services.v2;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.document.ESAdvicesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;

public interface ESPrescriptionService {

	List<?> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm, String category, Boolean searchByGenericName);
}
