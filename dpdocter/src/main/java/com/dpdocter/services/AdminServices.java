package com.dpdocter.services;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;

public interface AdminServices {

	Resume addResumes(Resume request);

	void importDrug();

	void importCity();

	void importDiagnosticTest();

	void importEducationInstitute();

	void importEducationQualification();

	ContactUs addContactUs(ContactUs request);

	void importProfessionalMembership();

	void importMedicalCouncil();

	Boolean sendLink(SendAppLink request);
}
