package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.AcadamicProfile;
import com.dpdocter.beans.DentalAssessment;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.ENTAssessment;
import com.dpdocter.beans.EyeAssessment;
import com.dpdocter.beans.GrowthAssessmentAndGeneralBioMetrics;
import com.dpdocter.beans.NutritionAssessment;
import com.dpdocter.beans.PhysicalAssessment;
import com.dpdocter.beans.RegistrationDetails;
import com.dpdocter.response.AcadamicClassResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.NutritionSchoolAssociationResponse;

public interface CampVisitService {

	GrowthAssessmentAndGeneralBioMetrics addEditGrowthAssessmentAndGeneralBioMetrics(
			GrowthAssessmentAndGeneralBioMetrics request);

	GrowthAssessmentAndGeneralBioMetrics getGrowthAssessmentAndGeneralBioMetricsById(String id);

	GrowthAssessmentAndGeneralBioMetrics discardGrowthAssessmentAndGeneralBioMetricsById(String id, Boolean discarded);

	List<GrowthAssessmentAndGeneralBioMetrics> getGrowthAssessmentAndGeneralBioMetricsList(String academicProfileId,
			String schoolId, String branchId, String doctorId, String updatedTime, int page, int size,
			Boolean isDiscarded);

	Integer getGrowthAssessmentAndGeneralBioMetricsListCount(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded);

	PhysicalAssessment addEditPhysicalAssessment(PhysicalAssessment request);

	PhysicalAssessment getPhysicalAssessmentById(String id);

	PhysicalAssessment discardPhysicalAssessment(String id, Boolean discarded);

	List<PhysicalAssessment> getPhysicalAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded);

	Integer getPhysicalAssessmentCount(String academicProfileId, String schoolId, String branchId, String doctorId,
			String updatedTime, int page, int size, Boolean isDiscarded);

	Integer getENTAssessmentListCount(String academicProfileId, String schoolId, String branchId, String doctorId,
			String updatedTime, int page, int size, Boolean isDiscarded);

	List<ENTAssessment> getENTAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded);

	ENTAssessment addEditENTAssessment(ENTAssessment request);

	ENTAssessment getENTAssessmentById(String id);

	ENTAssessment discardENTAssessmentById(String id, Boolean discarded);

	DentalAssessment addEditDentalAssessment(DentalAssessment request);

	DentalAssessment getDentalAssessmentById(String id);

	DentalAssessment discardDentalAssessmentById(String id, Boolean discarded);

	List<DentalAssessment> getDentalAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded);

	Integer getDentalAssessmentListCount(String academicProfileId, String schoolId, String branchId, String doctorId,
			String updatedTime, int page, int size, Boolean isDiscarded);

	EyeAssessment addEditEyeAssessment(EyeAssessment request);

	EyeAssessment getEyeAssessmentById(String id);

	EyeAssessment discardEyeAssessmentById(String id, Boolean discarded);

	List<EyeAssessment> getEyeAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded);

	Integer getEyeAssessmentListCount(String academicProfileId, String schoolId, String branchId, String doctorId,
			String updatedTime, int page, int size, Boolean isDiscarded);

	NutritionAssessment addEditNutritionAssessment(NutritionAssessment request);

	NutritionAssessment getNutritionAssessmentById(String id);

	NutritionAssessment discardNutritionAssessmentById(String id, Boolean discarded);

	List<NutritionAssessment> getNutritionAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded, String recipe);

	Integer getNutritionAssessmentListCount(String academicProfileId, String schoolId, String branchId, String doctorId,
			String updatedTime, int page, int size, Boolean isDiscarded, String recipe);

	ImageURLResponse addCampVisitImage(MultipartFile file);

	List<DrugInfo> getDrugInfo(int page, int size, String updatedTime, String searchTerm, Boolean discarded);

	Integer getDrugInfoCount(String updatedTime, String searchTerm);

	List<AcadamicProfile> getTeacherProfile(int page, int size, String branchId, String schoolId, String searchTerm,
			Boolean discarded, String profileType, String userId, String updatedTime);

	List<AcadamicProfile> getStudentProfile(int page, int size, String branchId, String schoolId, String classId,
			String sectionId, String searchTerm, Boolean discarded, String profileType, String userId,
			String updatedTime);

	Integer countTeacherProfile(String branchId, String schoolId, String searchTerm, Boolean discarded,
			String profileType, String userId, String updatedTime);

	Integer countStudentProfile(String branchId, String schoolId, String classId, String sectionId, String searchTerm,
			Boolean discarded, String profileType, String userId, String updatedTime);

	RegistrationDetails getAcadamicProfile(String profileId);

	List<NutritionSchoolAssociationResponse> getAssociations(int page, int size, String doctorId, String searchTerm,
			String updatedTime);

	List<AcadamicClassResponse> getAcadamicClass(int page, int size, String branchId, String schoolId,
			String searchTerm, Boolean discarded);

	Integer countAcadamicClass(String branchId, String schoolId, String searchTerm, Boolean discarded);

}