package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;

public interface ESDentalLabService {

	boolean addDentalWorks(ESDentalWorksDocument request);

	List<ESDentalWorksDocument> searchDentalworks(String range, long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
