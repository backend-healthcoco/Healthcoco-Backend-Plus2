package com.dpdocter.services;

import com.dpdocter.request.ExportRequest;

public interface DownloadDataService {

	Boolean downlaodData(ExportRequest request);

	void sendDataToDoctor();

	Boolean downloadClinicalItems(String doctorId, String locationId, String hospitalId);

}
