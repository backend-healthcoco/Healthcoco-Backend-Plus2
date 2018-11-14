package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;

public interface ESExpenseTypeService {
	void addEditExpenseType(ESExpenseTypeDocument esDocument);

	List<?> search( String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

}
