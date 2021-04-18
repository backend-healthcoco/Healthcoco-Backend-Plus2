package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;

public interface AdminServices {
	void importDrug();

	void importCity();

	void importDiagnosticTest();

	void importEducationInstitute();

	void importEducationQualification();

	ContactUs addContactUs(ContactUs request);

	void importProfessionalMembership();

	void importMedicalCouncil();

	Boolean sendLink(SendAppLink request);

	Boolean discardDuplicateClinicalItems(String doctorId);

	Boolean copyClinicalItems(String doctorId, String locationId, List<String> drIds);

	Boolean updateLocationIdInRole();

	Boolean addServices();

	Boolean updateServicesAndSpecialities();

	Boolean addServicesOfSpecialities();

	Boolean addSpecialities();

	Boolean addSymptomsDiseasesCondition();

	Resume addResumes(MultipartFile file, Resume request);
}
