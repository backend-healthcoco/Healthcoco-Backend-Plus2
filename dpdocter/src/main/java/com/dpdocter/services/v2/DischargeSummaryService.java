package com.dpdocter.services.v2;

import java.util.List;

import com.dpdocter.response.v2.DischargeSummaryResponse;

public interface DischargeSummaryService {

	List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, String updatedTime, Boolean discarded);

}
