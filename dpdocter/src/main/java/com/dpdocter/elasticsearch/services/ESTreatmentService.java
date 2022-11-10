package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;

import common.util.web.Response;

public interface ESTreatmentService {

	void addEditService(ESTreatmentServiceDocument esTreatmentServiceDocument);

	void addEditServiceCost(ESTreatmentServiceCostDocument esTreatmentServiceDocument);

	Response<Object> search(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String ratelistId);

}
