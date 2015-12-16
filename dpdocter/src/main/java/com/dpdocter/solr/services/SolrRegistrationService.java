package com.dpdocter.solr.services;

import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.response.SolrPatientResponseDetails;

public interface SolrRegistrationService {
    boolean addPatient(SolrPatientDocument request);

    boolean editPatient(SolrPatientDocument request);

    boolean deletePatient(String id);

    SolrPatientResponseDetails searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm, int page, int size);

    SolrPatientResponseDetails searchPatient(AdvancedSearch request);

    boolean addDoctor(SolrDoctorDocument request);
    
    // List<SolrPatientDocument> searchPatientByFirstName(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByMiddleName(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByLastName(String doctorId, String
    // locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByPID(String doctorId, String
    // locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByMobileNumber(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByEmailAddress(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByUserName(String doctorId, String
    // locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByCity(String doctorId, String
    // locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByLocality(String doctorId, String
    // locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByBloodGroup(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByReferredBy(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByProfession(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByPostalCode(String doctorId,
    // String locationId, String hospitalId, String searchValue);
    //
    // List<SolrPatientDocument> searchPatientByGender(String doctorId, String
    // locationId, String hospitalId, String searchValue);

    void patientProfilePicChange(String username, String imageUrl);

	void addEditName(DoctorNameAddEditRequest request);

	void addEditSpeciality(DoctorSpecialityAddEditRequest request);

	void addEditVisitingTime(DoctorVisitingTimeAddEditRequest request);

	void addEditConsultationFee(DoctorConsultationFeeAddEditRequest request);

	void addEditProfilePicture(String doctorId, String addEditProfilePictureResponse);

	void addEditExperience(String doctorId, DoctorExperience experienceResponse);

	void addEditGeneralInfo(DoctorGeneralInfo request);

	void addEditMultipleData(DoctorMultipleDataAddEditResponse addEditNameResponse);

}
