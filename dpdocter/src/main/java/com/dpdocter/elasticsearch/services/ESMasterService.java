package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Speciality;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESSymptomDiseaseConditionDocument;
import com.dpdocter.response.DiseaseListResponse;

import common.util.web.Response;

public interface ESMasterService {

    Response<Reference> searchReference(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    List<DiseaseListResponse> searchDisease(String range, long page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, String searchTerm);

    List<BloodGroup> searchBloodGroup();

    List<Profession> searchProfession(String searchTerm, String updatedTime, long page, int size);

    Boolean add();

    List<ProfessionalMembership> searchProfessionalMembership(String searchTerm, String updatedTime, long page, int size);

    List<EducationInstitute> searchEducationInstitute(String searchTerm, String updatedTime, long page, int size);

    List<EducationQualification> searchEducationQualification(String searchTerm, String updatedTime, long page, int size);

    List<MedicalCouncil> searchMedicalCouncil(String searchTerm, String updatedTime, long page, int size);

    List<Speciality> searchSpeciality(String searchTerm, String updatedTime, long page, int size);

    void addEditDisease(ESDiseasesDocument esDiseasesDocument);

	void addEditServices(ESServicesDocument esServicesDocument);

	void addEditSpecialities(ESSpecialityDocument esSpecialityDocument);

	void addEditSymptomDiseaseConditionDocument(ESSymptomDiseaseConditionDocument esSymptomDiseaseConditionDocument);
}
