package com.dpdocter.services;

import common.util.web.Response;

public interface VisitFieldWiseService {

	Response<Object> getComplaintData(String doctorId, String locationId, String hospitalId, String patientId);

}
