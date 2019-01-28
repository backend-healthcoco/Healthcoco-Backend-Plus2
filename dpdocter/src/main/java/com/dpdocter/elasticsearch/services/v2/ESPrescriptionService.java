package com.dpdocter.elasticsearch.services.v2;

import common.util.web.Response;

public interface ESPrescriptionService {

	Response<Object> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm, String category, Boolean searchByGenericName);

	Long drugCount(String range, String doctorId, String locationId, String hospitalId, String updatedTime,
			Boolean discarded, String searchTerm, String category, Boolean searchByGenericName);
}
