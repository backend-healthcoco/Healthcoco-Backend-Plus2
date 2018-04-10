package com.dpdocter.services;

import com.dpdocter.request.ExportRequest;

public interface DownloadDateServices {

	Boolean downlaodData(ExportRequest request);

	void sendDataToDoctor();

}
