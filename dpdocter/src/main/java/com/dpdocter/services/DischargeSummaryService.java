package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DischargeSummary;

public interface DischargeSummaryService {

	DischargeSummary addEditDischargeSummary(DischargeSummary dischargeSummary);

	// List<DischargeSummary> getAllDischargeSummary();

	List<DischargeSummary> getDischargeSummary(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, String updatedTime);

	Integer getDischargeSummaryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	DischargeSummary viewDischargeSummary(String summaryId);

}
