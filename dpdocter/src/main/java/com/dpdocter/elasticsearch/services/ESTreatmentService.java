package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;

public interface ESTreatmentService {

	void addEditService(ESTreatmentServiceDocument esTreatmentServiceDocument);

	void addEditServiceCost(ESTreatmentServiceCostDocument esTreatmentServiceDocument);

	List<?> search(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
