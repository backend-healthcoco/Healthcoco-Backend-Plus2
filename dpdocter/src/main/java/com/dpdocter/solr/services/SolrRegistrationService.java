package com.dpdocter.solr.services;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.response.SolrPatientResponseDetails;

public interface SolrRegistrationService {
    boolean addPatient(SolrPatientDocument request);

    boolean editPatient(SolrPatientDocument request);

    boolean deletePatient(String id);

    SolrPatientResponseDetails searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm, int page, int size, UriInfo uriInfo);

    SolrPatientResponseDetails searchPatient(AdvancedSearch request, UriInfo uriInfo);

    boolean addDoctor(SolrDoctorDocument request);

    void patientProfilePicChange(String username, String imageUrl);

    void addEditName(DoctorNameAddEditRequest request);

    void addEditSpeciality(DoctorSpecialityAddEditRequest request);

    void addEditVisitingTime(DoctorVisitingTimeAddEditRequest request);

    void addEditConsultationFee(DoctorConsultationFeeAddEditRequest request);

    void addEditProfilePicture(String doctorId, String addEditProfilePictureResponse);

    void addEditExperience(String doctorId, DoctorExperience experienceResponse);

    void addEditGeneralInfo(DoctorGeneralInfo request);

    void addEditMultipleData(DoctorMultipleDataAddEditResponse addEditNameResponse);

    void updateClinicProfile(ClinicProfile clinicProfileUpdateResponse);

    void updateClinicAddress(ClinicAddress clinicAddressUpdateResponse);

    void updateClinicSpecialization(ClinicSpecialization clinicSpecializationUpdateResponse);

    void updateLabProperties(ClinicLabProperties clinicLabProperties);

    void editLocation(DoctorLocation doctorLocation);

}
