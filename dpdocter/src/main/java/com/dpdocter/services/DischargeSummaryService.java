package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.response.DischargeSummaryResponse;

public interface DischargeSummaryService {

	DischargeSummaryResponse addEditDischargeSummary(DischargeSummaryRequest dischargeSummary);

	// List<DischargeSummary> getAllDischargeSummary();

	List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, String updatedTime);

	int getDischargeSummaryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	DischargeSummaryResponse viewDischargeSummary(String summaryId);

}
