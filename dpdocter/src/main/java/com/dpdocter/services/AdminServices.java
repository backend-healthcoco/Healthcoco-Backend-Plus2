package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.User;
import com.dpdocter.response.DoctorResponse;

public interface AdminServices {

	List<User> getInactiveUsers(int page, int size);

	List<Hospital> getHospitals(int page, int size);

	List<Location> getClinics(int page, int size, String hospitalId, Boolean isClinic, Boolean isLab, String searchTerm);

	Resume addResumes(Resume request);

	List<Resume> getResumes(int page, int size, String type);

	void importDrug();

	void importCity();

	void importDiagnosticTest();

	void importEducationInstitute();

	void importEducationQualification();

	List<DoctorResponse> getDoctors(int page, int size, String locationId, String state, String searchTerm);

	List<Location> getLabs(int page, int size, String hospitalId);

	ContactUs addContactUs(ContactUs request);

	List<ContactUs> getContactUs(int page, int size);

	List<Speciality> getUniqueSpecialities(String searchTerm, String updatedTime, int page, int size);

	void importProfessionalMembership();

	void importMedicalCouncil();
}
