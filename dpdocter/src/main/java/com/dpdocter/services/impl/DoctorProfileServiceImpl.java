package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.AddEditSEORequest;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.Services;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorProfileViewCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.FeedbackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.TreatmentServicesCostCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.CardioPermissionEnum;
import com.dpdocter.enums.DoctorExperienceUnit;
import com.dpdocter.enums.GynacPermissionsEnum;
import com.dpdocter.enums.OpthoPermissionEnums;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorProfileViewRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.ServicesRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserResourceFavouriteRepository;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditFacilityRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorDOBAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
import com.dpdocter.request.DoctorGenderAddEditRequest;
import com.dpdocter.request.DoctorMultipleDataAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorServicesAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.request.RegularCheckUpAddEditRequest;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.DynamicUIService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

	private static Logger logger = Logger.getLogger(DoctorProfileServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private ProfessionalMembershipRepository professionalMembershipRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private SpecialityRepository specialityRepository;
	
	@Autowired
	private ServicesRepository servicesRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private RecommendationsRepository recommendationsRepository;

	@Autowired
	DynamicUIRepository dynamicUIRepository;

	@Autowired
	DoctorProfileViewRepository doctorProfileViewRepository;

	@Autowired
	DynamicUIService dynamicUIService;

	@Autowired
	private UserResourceFavouriteRepository userResourceFavouriteRepository;

	@Override
	@Transactional
	public DoctorNameAddEditRequest addEditName(DoctorNameAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorNameAddEditRequest response = null;
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			BeanUtil.map(request, userCollection);
			userRepository.save(userCollection);
			response = new DoctorNameAddEditRequest();
			BeanUtil.map(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorExperienceAddEditRequest addEditExperience(DoctorExperienceAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorExperienceAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (request.getExperience() > 0) {
				DoctorExperience doctorExperience = new DoctorExperience();
				doctorExperience.setExperience(request.getExperience());
				doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
				doctorCollection.setExperience(doctorExperience);
				response = new DoctorExperienceAddEditRequest();
				BeanUtil.map(doctorExperience, response);
			} else {
				doctorCollection.setExperience(null);
			}
			doctorRepository.save(doctorCollection);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactAddEditRequest addEditContact(DoctorContactAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		DoctorContactAddEditRequest response = null;
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			userCollection.setMobileNumber(request.getMobileNumber());
			doctorCollection.setAdditionalNumbers(request.getAdditionalNumbers());
			doctorCollection.setOtherEmailAddresses(request.getOtherEmailAddresses());
			userRepository.save(userCollection);
			doctorRepository.save(doctorCollection);
			response = new DoctorContactAddEditRequest();
			BeanUtil.map(userCollection, response);
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorEducationAddEditRequest addEditEducation(DoctorEducationAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorEducationAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setEducation(request.getEducation());
			doctorRepository.save(doctorCollection);
			response = new DoctorEducationAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public List<MedicalCouncil> getMedicalCouncils(int page, int size, String updatedTime) {
		List<MedicalCouncil> medicalCouncils = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "medicalCouncil")),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "medicalCouncil")));
			AggregationResults<MedicalCouncil> aggregationResults = mongoTemplate.aggregate(aggregation,
					MedicalCouncilCollection.class, MedicalCouncil.class);
			medicalCouncils = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Medical Councils");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Medical Councils");
		}
		return medicalCouncils;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorSpecialityAddEditRequest addEditSpeciality(DoctorSpecialityAddEditRequest request) {
		DoctorSpecialityAddEditRequest response = null;
		DoctorCollection doctorCollection = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			List<ObjectId> oldSpecialities = doctorCollection.getSpecialities();
			if (doctorCollection != null) {
				response = new DoctorSpecialityAddEditRequest();
				if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {
					List<SpecialityCollection> specialityCollections = specialityRepository
							.findBySuperSpeciality(request.getSpeciality());
					Collection<ObjectId> specialityIds = CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("id"));
					if (specialityIds != null && !specialityIds.isEmpty()) {
						doctorCollection.setSpecialities(new ArrayList<>(specialityIds));
						if (oldSpecialities != null && !oldSpecialities.isEmpty()) {
							removeOldSpecialityPermissions(specialityIds, oldSpecialities, request.getDoctorId());
							List<ServicesCollection> servicesCollections = servicesRepository.findbySpeciality(oldSpecialities);
							List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections, new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> services = new HashSet<>(servicesIds);
							if(doctorCollection.getServices()!= null)doctorCollection.getServices().removeAll(services);
						}
						if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
							List<ServicesCollection> servicesCollections = servicesRepository.findbySpeciality(doctorCollection.getSpecialities());
							List<ObjectId> services = (List<ObjectId>) CollectionUtils.collect(servicesCollections, new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> serviceIds = new HashSet<>(services);
							
							if(doctorCollection.getServices()!= null)doctorCollection.getServices().addAll(services);
							else doctorCollection.setServices(serviceIds);
						}
					} else {
						doctorCollection.setSpecialities(null);
						assignDefaultUIPermissions(request.getDoctorId());
					}
				} else {
					doctorCollection.setSpecialities(null);
					assignDefaultUIPermissions(request.getDoctorId());
				}
				doctorRepository.save(doctorCollection);
				BeanUtil.map(doctorCollection, response);
				response.setDoctorId(doctorCollection.getUserId().toString());

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	private void assignDefaultUIPermissions(String doctorId) {
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(doctorId));
		UIPermissions uiPermissions = dynamicUIService.getDefaultPermissions();
		dynamicUICollection.setUiPermissions(uiPermissions);
		dynamicUIRepository.save(dynamicUICollection);
	}

	private void removeOldSpecialityPermissions(Collection<ObjectId> specialityIds,
			Collection<ObjectId> oldSpecialities, String doctorId) {
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(doctorId));
		if (dynamicUICollection != null) {
			for (ObjectId objectId : specialityIds) {
				if (oldSpecialities.contains(objectId))
					oldSpecialities.remove(objectId);
			}
			if (oldSpecialities != null && !oldSpecialities.isEmpty()) {
				List<SpecialityCollection> oldSpecialityCollections = (List<SpecialityCollection>) specialityRepository
						.findAllById(oldSpecialities);
				@SuppressWarnings("unchecked")
				Collection<String> specialities = CollectionUtils.collect(oldSpecialityCollections,
						new BeanToPropertyValueTransformer("speciality"));
				UIPermissions uiPermissions = dynamicUICollection.getUiPermissions();
				for (String speciality : specialities) {
					if (speciality.equalsIgnoreCase("OPHTHALMOLOGIST")) {
						if (uiPermissions.getClinicalNotesPermissions() != null)
							uiPermissions.getClinicalNotesPermissions()
									.remove(OpthoPermissionEnums.OPTHO_CLINICAL_NOTES.getPermissions());
						if (uiPermissions.getPrescriptionPermissions() != null)
							uiPermissions.getPrescriptionPermissions()
									.remove(OpthoPermissionEnums.OPTHO_RX.getPermissions());
					}
					if (speciality.equalsIgnoreCase("PEDIATRICIAN")) {
						if (uiPermissions.getProfilePermissions() != null)
							uiPermissions.getProfilePermissions()
									.remove(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
					}
					if (speciality.equalsIgnoreCase("GYNECOLOGIST/OBSTETRICIAN")) {
						if (uiPermissions.getClinicalNotesPermissions() != null) {
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.PA.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.PV.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.PS.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.INDICATION_OF_USG.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.EDD.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.LMP.getPermissions());
							uiPermissions.getProfilePermissions()
									.remove(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
						}
					}
					if (speciality.equalsIgnoreCase("CARDIOLOGIST")) {
						uiPermissions.getClinicalNotesPermissions().remove(CardioPermissionEnum.ECG.getPermissions());
						uiPermissions.getClinicalNotesPermissions().remove(CardioPermissionEnum.ECHO.getPermissions());
						uiPermissions.getClinicalNotesPermissions().remove(CardioPermissionEnum.XRAY.getPermissions());
						uiPermissions.getClinicalNotesPermissions()
								.remove(CardioPermissionEnum.HOLTER.getPermissions());
					}
				}
				dynamicUIRepository.save(dynamicUICollection);
			}
		}
	}

	@Override
	@Transactional
	public DoctorAchievementAddEditRequest addEditAchievement(DoctorAchievementAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorAchievementAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setAchievements(request.getAchievements());
			doctorRepository.save(doctorCollection);
			response = new DoctorAchievementAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorProfessionalStatementAddEditRequest addEditProfessionalStatement(
			DoctorProfessionalStatementAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorProfessionalStatementAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setProfessionalStatement(request.getProfessionalStatement());
			doctorRepository.save(doctorCollection);
			response = new DoctorProfessionalStatementAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorRegistrationAddEditRequest addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorRegistrationAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setRegistrationDetails(request.getRegistrationDetails());
			doctorRepository.save(doctorCollection);
			response = new DoctorRegistrationAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorExperienceDetailAddEditRequest addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorExperienceDetailAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setExperienceDetails(request.getExperienceDetails());
			doctorRepository.save(doctorCollection);
			response = new DoctorExperienceDetailAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public String addEditProfilePicture(DoctorProfilePictureAddEditRequest request) {
		UserCollection userCollection = null;
		String response = "";
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (request.getImage() != null) {
				String path = "profile-image";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				userCollection.setImageUrl(imageURLResponse.getImageUrl());
				userCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());

				userCollection = userRepository.save(userCollection);
				response = userCollection.getImageUrl();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public String addEditCoverPicture(DoctorProfilePictureAddEditRequest request) {
		UserCollection userCollection = null;
		String response = "";
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (request.getImage() != null) {
				String path = "cover-image";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				userCollection.setCoverImageUrl(imageURLResponse.getImageUrl());
				userCollection.setCoverThumbnailImageUrl(imageURLResponse.getThumbnailUrl());
				userCollection = userRepository.save(userCollection);
				response = userCollection.getCoverImageUrl();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId, String patientId,
			Boolean isMobileApp, Boolean isSearched) {
		DoctorProfile doctorProfile = null;
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		List<String> specialities = null;
		List<String> services = null;
		List<DoctorRegistrationDetail> registrationDetails = null;
		List<String> professionalMemberships = null;
		List<DoctorClinicProfile> clinicProfile = new ArrayList<DoctorClinicProfile>();
		List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (isSearched == false) {
				criteria.and("isActivate").is(true).and("hasLoginAccess").ne(false);
			}

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("hospital_cl", "location.hospitalId", "_id", "hospital"),
					Aggregation.unwind("hospital"), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
					Aggregation.unwind("doctor"))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			doctorClinicProfileLookupResponses = mongoTemplate.aggregate(aggregation,

					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if (doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					userCollection = doctorClinicProfileLookupResponse.getUser();
					doctorCollection = doctorClinicProfileLookupResponse.getDoctor();
					if (userCollection == null || doctorCollection == null) {
						logger.error("No user found");
						throw new BusinessException(ServiceError.NoRecord, "No user found");
					}
					DoctorClinicProfile doctorClinicProfile = getDoctorClinic(doctorClinicProfileLookupResponse,
							patientId, isMobileApp, doctorClinicProfileLookupResponses.size());
					if (doctorClinicProfile != null)
						clinicProfile.add(doctorClinicProfile);
				}

				doctorProfile = new DoctorProfile();

				BeanUtil.map(userCollection, doctorProfile);

				BeanUtil.map(doctorCollection, doctorProfile);

				doctorProfile.setDoctorId(doctorCollection.getUserId().toString());
				// set specialities using speciality ids
				if (doctorCollection.getSpecialities() != null) {
					List<SpecialityCollection> specialityCollections = (List<SpecialityCollection>) specialityRepository
							.findAllById(doctorCollection.getSpecialities());
					specialities = (List<String>) CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("superSpeciality"));
					if (isMobileApp) {
						List<String> parentSpecialities = (List<String>) CollectionUtils.collect(specialityCollections,
								new BeanToPropertyValueTransformer("speciality"));
						doctorProfile.setParentSpecialities(parentSpecialities);
					}
					doctorProfile.setSpecialities(specialities);
				}

				if (doctorCollection.getServices() != null && !doctorCollection.getServices().isEmpty()) {
					services = (List<String>) CollectionUtils.collect(
							(Collection<?>) servicesRepository.findAllById(doctorCollection.getServices()),
							new BeanToPropertyValueTransformer("service"));
				}
				doctorProfile.setServices(services);

				// set medical councils using medical councils ids
				registrationDetails = new ArrayList<DoctorRegistrationDetail>();
				if (doctorProfile.getRegistrationDetails() != null
						&& !doctorProfile.getRegistrationDetails().isEmpty()) {
					for (DoctorRegistrationDetail registrationDetail : doctorProfile.getRegistrationDetails()) {
						DoctorRegistrationDetail doctorRegistrationDetail = new DoctorRegistrationDetail();
						BeanUtil.map(registrationDetail, doctorRegistrationDetail);
						registrationDetails.add(doctorRegistrationDetail);
					}
				}
				doctorProfile.setRegistrationDetails(registrationDetails);
				// set professional memberships using professional membership
				// ids
				if (doctorCollection.getProfessionalMemberships() != null
						&& !doctorCollection.getProfessionalMemberships().isEmpty()) {
					professionalMemberships = (List<String>) CollectionUtils.collect(
							(Collection<?>) professionalMembershipRepository
									.findAllById(doctorCollection.getProfessionalMemberships()),
							new BeanToPropertyValueTransformer("membership"));
				}
				doctorProfile.setProfessionalMemberships(professionalMemberships);

				// set clinic profile details
				doctorProfile.setClinicProfile(clinicProfile);
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Profile");
		}
		return doctorProfile;
	}

	private DoctorClinicProfile getDoctorClinic(DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse,
			String patientId, Boolean isMobileApp, int locationSize) {
		DoctorClinicProfile doctorClinic = new DoctorClinicProfile();
		try {
			LocationCollection locationCollection = doctorClinicProfileLookupResponse.getLocation();

			if (locationCollection != null) {
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}

				doctorClinic.setClinicAddress(address);
				BeanUtil.map(locationCollection, doctorClinic);
			}
			if (doctorClinicProfileLookupResponse != null)
				BeanUtil.map(doctorClinicProfileLookupResponse, doctorClinic);
			doctorClinic.setLocationId(doctorClinicProfileLookupResponse.getLocationId().toString());
			doctorClinic.setDoctorId(doctorClinicProfileLookupResponse.getDoctorId().toString());
			doctorClinic.setDoctorSlugURL(doctorClinicProfileLookupResponse.getDoctorSlugURL());

			Criteria criteria = new Criteria("doctorId").is(doctorClinicProfileLookupResponse.getDoctorId())
					.and("locationId").is(locationCollection.getId()).and("hospitalId")
					.is(locationCollection.getHospitalId());
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("treatment_services_cl", "treatmentServiceId", "_id", "treatmentServicesList"),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((0) * 5),
					Aggregation.limit(5));

			AggregationResults<TreatmentServiceCost> aggregationResults = mongoTemplate.aggregate(aggregation,
					TreatmentServicesCostCollection.class, TreatmentServiceCost.class);
			List<TreatmentServiceCost> treatmentServicesCosts = aggregationResults.getMappedResults();
			doctorClinic.setTreatmentServiceCosts(treatmentServicesCosts);
			doctorClinic.setNoOfServices(
					(int) mongoTemplate.count(new Query(criteria), TreatmentServicesCostCollection.class));

			List<Feedback> feedbacks = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria.and("isVisible").is(true)),
									Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
									Aggregation.skip((0) * 5), Aggregation.limit(5)),
							FeedbackCollection.class, Feedback.class)
					.getMappedResults();
			doctorClinic.setFeedbacks(feedbacks);
			doctorClinic.setNoOfFeedbacks((int) mongoTemplate.count(new Query(criteria), FeedbackCollection.class));

			List<UserRoleLookupResponse> userRoleLookupResponses = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(
											new Criteria("userId").is(doctorClinicProfileLookupResponse.getDoctorId())
													.and("locationId").is(locationCollection.getId()).and("hospitalId")
													.is(locationCollection.getHospitalId())),
									Aggregation.lookup("role_cl", "roleId", "_id", "roleCollection"),
									Aggregation.unwind("roleCollection")),
							UserRoleCollection.class, UserRoleLookupResponse.class)
					.getMappedResults();
			List<Role> roles = null;

			for (UserRoleLookupResponse roleLookupResponse : userRoleLookupResponses) {
				RoleCollection otherRoleCollection = roleLookupResponse.getRoleCollection();
				if (isMobileApp && locationSize == 1
						&& !(otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.DOCTOR.getRole())
								|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.CONSULTANT_DOCTOR.getRole())
								|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole())
								|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.HOSPITAL_ADMIN.getRole())
								|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole()))) {
					logger.warn("You are staff member so please login from website.");
					throw new BusinessException(ServiceError.NotAuthorized,
							"You are staff member so please login from website.");
				} else if (isMobileApp && !(otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.DOCTOR.getRole())
						|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.CONSULTANT_DOCTOR.getRole())
						|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole())
						|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.HOSPITAL_ADMIN.getRole())
						|| otherRoleCollection.getRole().equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole()))) {
					return null;
				}

				if (otherRoleCollection != null) {
					AccessControl accessControl = accessControlServices.getAccessControls(otherRoleCollection.getId(),
							otherRoleCollection.getLocationId(), otherRoleCollection.getHospitalId());

					Role role = new Role();
					BeanUtil.map(otherRoleCollection, role);
					role.setLocationId(roleLookupResponse.getLocationId());
					role.setHospitalId(roleLookupResponse.getHospitalId());
					role.setAccessModules(accessControl.getAccessModules());

					if (roles == null)
						roles = new ArrayList<Role>();
					roles.add(role);
				}
				doctorClinic.setRoles(roles);

				if (!DPDoctorUtils.anyStringEmpty(patientId)) {
					RecommendationsCollection recommendationsCollection = recommendationsRepository
							.findByDoctorIdLocationIdAndPatientId(doctorClinicProfileLookupResponse.getDoctorId(),
									doctorClinicProfileLookupResponse.getLocationId(), new ObjectId(patientId));
					if (recommendationsCollection != null)
						doctorClinic.setIsDoctorRecommended(!recommendationsCollection.getDiscarded());

					Integer favCount = userResourceFavouriteRepository.findCount(
							doctorClinicProfileLookupResponse.getDoctorId(), Resource.DOCTOR.getType(),
							doctorClinicProfileLookupResponse.getLocationId(), new ObjectId(patientId), false);

					if (favCount != null && favCount > 0)
						doctorClinic.setIsFavourite(true);
				}
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		}
		return doctorClinic;
	}

	@Override
	@Transactional
	public List<ProfessionalMembership> getProfessionalMemberships(int page, int size, String updatedTime) {
		List<ProfessionalMembership> professionalMemberships = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "membership")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "membership")));
			AggregationResults<ProfessionalMembership> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProfessionalMembershipCollection.class, ProfessionalMembership.class);
			professionalMemberships = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Professional Memberships");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Professional Memberships");
		}
		return professionalMemberships;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorProfessionalAddEditRequest addEditProfessionalMembership(DoctorProfessionalAddEditRequest request) {
		List<String> professionalMemberships = null;
		List<ObjectId> professionalMembershipIds = null;
		DoctorProfessionalAddEditRequest response = new DoctorProfessionalAddEditRequest();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (request.getMembership() != null && !request.getMembership().isEmpty()) {
				List<ProfessionalMembershipCollection> professionalMembershipCollections = professionalMembershipRepository
						.find(request.getMembership());
				professionalMembershipIds = (List<ObjectId>) CollectionUtils.collect(professionalMembershipCollections,
						new BeanToPropertyValueTransformer("id"));
				professionalMemberships = (List<String>) CollectionUtils.collect(professionalMembershipCollections,
						new BeanToPropertyValueTransformer("membership"));
			}
			doctorCollection.setProfessionalMemberships(professionalMembershipIds);
			doctorRepository.save(doctorCollection);
			response.setDoctorId(doctorCollection.getUserId().toString());
			response.setMembership(professionalMemberships);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorAppointmentNumbersAddEditRequest addEditAppointmentNumbers(
			DoctorAppointmentNumbersAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorAppointmentNumbersAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setAppointmentBookingNumber(request.getAppointmentBookingNumber());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorAppointmentNumbersAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorVisitingTimeAddEditRequest addEditVisitingTime(DoctorVisitingTimeAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorVisitingTimeAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setWorkingSchedules(request.getWorkingSchedules());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorVisitingTimeAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorConsultationFeeAddEditRequest addEditConsultationFee(DoctorConsultationFeeAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorConsultationFeeAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setConsultationFee(request.getConsultationFee());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorConsultationFeeAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorAppointmentSlotAddEditRequest addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorAppointmentSlotAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setAppointmentSlot(request.getAppointmentSlot());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorAppointmentSlotAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorGeneralInfo addEditGeneralInfo(DoctorGeneralInfo request) {
		DoctorGeneralInfo response = null;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setAppointmentBookingNumber(request.getAppointmentBookingNumber());
			doctorClinicProfileCollection.setConsultationFee(request.getConsultationFee());
			doctorClinicProfileCollection.setAppointmentSlot(request.getAppointmentSlot());
			doctorClinicProfileCollection.setFacility(request.getFacility());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorGeneralInfo();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding or editing general info : " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Speciality> getSpecialities(int page, int size, String updatedTime) {
		List<Speciality> specialities = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "superSpeciality")),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "superSpeciality")));
			AggregationResults<Speciality> aggregationResults = mongoTemplate.aggregate(aggregation,
					SpecialityCollection.class, Speciality.class);
			specialities = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Specialities");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Specialities");
		}
		return specialities;
	}

	@Override
	@Transactional
	public List<EducationInstitute> getEducationInstitutes(int page, int size, String updatedTime) {
		List<EducationInstitute> educationInstitutes = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")));
			AggregationResults<EducationInstitute> aggregationResults = mongoTemplate.aggregate(aggregation,
					EducationInstituteCollection.class, EducationInstitute.class);
			educationInstitutes = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Education Institutes");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Education Institutes");
		}
		return educationInstitutes;
	}

	@Override
	@Transactional
	public List<EducationQualification> getEducationQualifications(int page, int size, String updatedTime) {
		List<EducationQualification> qualifications = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")));
			AggregationResults<EducationQualification> aggregationResults = mongoTemplate.aggregate(aggregation,
					EducationQualificationCollection.class, EducationQualification.class);
			qualifications = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Professional Memberships");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Professional Memberships");
		}
		return qualifications;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorMultipleDataAddEditResponse addEditMultipleData(DoctorMultipleDataAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		List<String> specialitiesresponse = new ArrayList<>();
		List<String> parentSpecialitiesresponse = new ArrayList<>();
		DoctorMultipleDataAddEditResponse response = null;
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (userCollection != null && doctorCollection != null) {

				if (!DPDoctorUtils.anyStringEmpty(request.getTitle())) {
					userCollection.setTitle(request.getTitle());
				}

				if (!DPDoctorUtils.anyStringEmpty(request.getFirstName())) {
					userCollection.setFirstName(request.getFirstName());
				}

				if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
					doctorCollection.setGender(request.getGender());
				}
				
				if (!DPDoctorUtils.anyStringEmpty(request.getFreshchatRestoreId())) {
					doctorCollection.setFreshchatRestoreId(request.getFreshchatRestoreId());
				}
				doctorCollection.setDob(request.getDob());

				response = new DoctorMultipleDataAddEditResponse();
				response.setDoctorId(request.getDoctorId());

				if (request.getExperience() > 0) {
					DoctorExperience doctorExperience = new DoctorExperience();
					doctorExperience.setExperience(request.getExperience());
					doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
					doctorCollection.setExperience(doctorExperience);
				} else {
					doctorCollection.setExperience(null);
				}
				if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {
					List<ObjectId> oldSpecialities = doctorCollection.getSpecialities();
					List<SpecialityCollection> specialityCollections = specialityRepository
							.findBySuperSpeciality(request.getSpeciality());
					if (specialityCollections != null && !specialityCollections.isEmpty()) {
						Collection<ObjectId> specialityIds = CollectionUtils.collect(specialityCollections,
								new BeanToPropertyValueTransformer("id"));
						Collection<String> specialities = CollectionUtils.collect(specialityCollections,
								new BeanToPropertyValueTransformer("superSpeciality"));
						Collection<String> parentSpecialities = CollectionUtils.collect(specialityCollections,
								new BeanToPropertyValueTransformer("speciality"));

						if (specialityIds != null && !specialityIds.isEmpty()) {
							doctorCollection.setSpecialities(new ArrayList<>(specialityIds));
							specialitiesresponse.addAll(specialities);
							parentSpecialitiesresponse.addAll(parentSpecialities);
							if (oldSpecialities != null && !oldSpecialities.isEmpty()) {
								removeOldSpecialityPermissions(specialityIds, oldSpecialities, request.getDoctorId());
								List<ServicesCollection> servicesCollections = servicesRepository.findbySpeciality(oldSpecialities);
								List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections, new BeanToPropertyValueTransformer("id"));
								Set<ObjectId> services = new HashSet<>(servicesIds);
								if(doctorCollection.getServices()!= null)doctorCollection.getServices().removeAll(services);
						}
						if(doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
							List<ServicesCollection> servicesCollections = servicesRepository.findbySpeciality(doctorCollection.getSpecialities());
							List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections, new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> services = new HashSet<>(servicesIds);
							if(doctorCollection.getServices()!= null)doctorCollection.getServices().addAll(services);
							else doctorCollection.setServices(services);
						}
						} else {
							doctorCollection.setSpecialities(null);
							assignDefaultUIPermissions(request.getDoctorId());
							specialitiesresponse = null;
							parentSpecialitiesresponse = null;
						}
					} else {
						doctorCollection.setSpecialities(null);
						assignDefaultUIPermissions(request.getDoctorId());
						specialitiesresponse = null;
						parentSpecialitiesresponse = null;
					}
				} else {
					doctorCollection.setSpecialities(null);

				}

				if (request.getProfileImage() != null) {
					String path = "profile-image";
					// save image
					request.getProfileImage()
							.setFileName(request.getProfileImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager
							.saveImageAndReturnImageUrl(request.getProfileImage(), path, true);
					userCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
				}

				if (request.getCoverImage() != null) {
					String path = "cover-image";
					// save image
					request.getCoverImage().setFileName(request.getCoverImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getCoverImage(),
							path, true);
					userCollection.setCoverImageUrl(imageURLResponse.getImageUrl());
					userCollection.setCoverThumbnailImageUrl(imageURLResponse.getThumbnailUrl());
				}
				userCollection = userRepository.save(userCollection);
				doctorCollection = doctorRepository.save(doctorCollection);

				BeanUtil.map(userCollection, response);
				BeanUtil.map(doctorCollection, response);
				response.setSpecialities(specialitiesresponse);
				response.setParentSpecialities(parentSpecialitiesresponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorAddEditFacilityRequest addEditFacility(DoctorAddEditFacilityRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorAddEditFacilityRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			} else {
				doctorClinicProfileCollection.setUpdatedTime(new Date());
			}
			doctorClinicProfileCollection.setFacility(request.getFacility());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorAddEditFacilityRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;

	}

	@Override
	@Transactional
	public DoctorGenderAddEditRequest addEditGender(DoctorGenderAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorGenderAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setGender(request.getGender());
			doctorRepository.save(doctorCollection);

			response = new DoctorGenderAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Gender");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Gender");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorDOBAddEditRequest addEditDOB(DoctorDOBAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorDOBAddEditRequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setDob(request.getDob());
			doctorRepository.save(doctorCollection);

			response = new DoctorDOBAddEditRequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public DoctorClinicProfile addEditRecommedation(String doctorId, String locationId, String patientId) {
		DoctorClinicProfile response;

		try {

			ObjectId doctorObjectId = new ObjectId(doctorId);
			ObjectId locationObjectId = new ObjectId(locationId);
			ObjectId patientObjectId = new ObjectId(patientId);
			DoctorClinicProfileCollection doctorClinicProfileCollection = null;
			RecommendationsCollection recommendationsCollection = null;

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(doctorObjectId,
					locationObjectId);

			UserCollection userCollection = userRepository.findById(patientObjectId).orElse(null);
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			if (userCollection != null) {
				recommendationsCollection = recommendationsRepository
						.findByDoctorIdLocationIdAndPatientId(doctorObjectId, locationObjectId, patientObjectId);

				if (recommendationsCollection != null) {
					if (!recommendationsCollection.getDiscarded()) {
						doctorClinicProfileCollection
								.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() - 1);
						recommendationsCollection.setDiscarded(true);
					} else {
						doctorClinicProfileCollection
								.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() + 1);
						recommendationsCollection.setDiscarded(false);
					}
				} else {

					recommendationsCollection = new RecommendationsCollection();
					recommendationsCollection.setDoctorId(doctorObjectId);
					recommendationsCollection.setLocationId(locationObjectId);
					recommendationsCollection.setPatientId(patientObjectId);
					doctorClinicProfileCollection
							.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() + 1);

				}
				recommendationsCollection = recommendationsRepository.save(recommendationsCollection);
				doctorClinicProfileCollection = doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				transnationalService.checkDoctor(doctorClinicProfileCollection.getDoctorId(),
						doctorClinicProfileCollection.getLocationId());
				response = new DoctorClinicProfile();
				BeanUtil.map(doctorClinicProfileCollection, response);
				response.setIsDoctorRecommended(!recommendationsCollection.getDiscarded());
			} else {
				throw new BusinessException(ServiceError.Unknown, "Error  DoctorClinicProfile not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}

		return response;
	}

	@Override
	public DoctorContactsResponse getPatient(int page, int size, String doctorId, String locationId, String hospitalId,
			long from, long to) {
		DoctorContactsResponse response = new DoctorContactsResponse();
		try {
			ObjectId doctorObjectId = null;
			ObjectId locationObjectId = null;
			ObjectId hospitalObjectId = null;
			Aggregation aggregation = null;
			Criteria criteria = new Criteria().and("discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			criteria = criteria.and("doctorId").is(doctorObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			criteria = criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			criteria = criteria.and("hospitalId").is(hospitalObjectId);
			if (from > 0)

				criteria = criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(from)));
			if (to > 0)

				criteria = criteria.and("createdTime").lte(DPDoctorUtils.getEndTime(new Date(to)));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			AggregationResults<PatientCard> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientCollection.class, PatientCard.class);
			response.setPatientCards(aggregationResults.getMappedResults());

			response.setTotalSize((int) mongoTemplate.count(new Query(criteria), PatientCollection.class));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting Patient ");
			throw new BusinessException(ServiceError.Unknown, "Error getting Patient");
		}
		return response;
	}

	/*
	 * @Override
	 * 
	 * @Transactional public RegularCheckUpAddEditRequest
	 * addRegularCheckupMonths(RegularCheckUpAddEditRequest request) {
	 * UserCollection userCollection = null; RegularCheckUpAddEditRequest response =
	 * null; try { userCollection = userRepository.findById(new
	 * ObjectId(request.getDoctorId())); BeanUtil.map(request, userCollection);
	 * userRepository.save(userCollection); response = new
	 * RegularCheckUpAddEditRequest(); BeanUtil.map(request, response);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); logger.error(e +
	 * " Error Editing Doctor Profile"); throw new
	 * BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile"); }
	 * return response; }
	 */

	@Override
	@Transactional
	public DoctorClinicProfile addRegularCheckupMonths(RegularCheckUpAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorClinicProfile response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			doctorClinicProfileCollection.setRegularCheckUpMonths(request.getRegularCheckUpMonths());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorClinicProfile();
			BeanUtil.map(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateDoctorProfileViews(String doctorId) {
		Boolean status = false;
		try {
			DoctorProfileViewCollection doctorProfileViewCollection = new DoctorProfileViewCollection();
			doctorProfileViewCollection.setDoctorId(new ObjectId(doctorId));
			doctorProfileViewRepository.save(doctorProfileViewCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return status;

	}

	@Override
	@Transactional
	public Boolean updateEMRSetting(String doctorId, Boolean discarded) {
		Boolean status = false;
		try {
			DoctorCollection doctorCollection = doctorRepository.findById(new ObjectId(doctorId)).orElse(null);
			if (doctorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
			}
			doctorCollection.setIsGetDiscardedEMR(discarded);
			doctorRepository.save(doctorCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return status;

	}

	@Override
	@Transactional
	public AddEditSEORequest addEditSEO(AddEditSEORequest request) {
		DoctorCollection doctorCollection = null;
		AddEditSEORequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setMetaTitle(request.getMetaTitle());
			doctorCollection.setMetaKeyword(request.getMetaKeyword());
			doctorCollection.setMetaDesccription(request.getMetaDesccription());
			doctorCollection.setSlugUrl(request.getSlugUrl());
			doctorRepository.save(doctorCollection);
			response = new AddEditSEORequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor SEO");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor SEO");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorProfile getDoctorProfile(String userUId) {
		DoctorProfile doctorProfile = null;
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		List<String> specialities = null;
		List<String> services = null;
		List<String> parentSpecialities = null;
		List<DoctorRegistrationDetail> registrationDetails = null;
		List<String> professionalMemberships = null;
		List<DoctorClinicProfile> clinicProfile = new ArrayList<DoctorClinicProfile>();
		List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = null;
		try {
			Criteria criteria = new Criteria("user.userUId").is(userUId).and("isDoctorListed").is(true);

			doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"), Aggregation.match(criteria),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if (doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					userCollection = doctorClinicProfileLookupResponse.getUser();
					doctorCollection = doctorClinicProfileLookupResponse.getDoctor();
					if (userCollection == null || doctorCollection == null) {
						logger.error("No user found");
						throw new BusinessException(ServiceError.NoRecord, "No user found");
					}
					DoctorClinicProfile doctorClinicProfile = getDoctorClinic(doctorClinicProfileLookupResponse, null,
							false, doctorClinicProfileLookupResponses.size());
					if (doctorClinicProfile != null)
						clinicProfile.add(doctorClinicProfile);
				}
				doctorProfile = new DoctorProfile();
				BeanUtil.map(userCollection, doctorProfile);
				BeanUtil.map(doctorCollection, doctorProfile);
				doctorProfile.setDoctorId(doctorCollection.getUserId().toString());
				// set specialities using speciality ids
				if (doctorCollection.getSpecialities() != null) {
					List<SpecialityCollection> specialityCollections = (List<SpecialityCollection>) specialityRepository
							.findAllById(doctorCollection.getSpecialities());
					specialities = (List<String>) CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("superSpeciality"));

					parentSpecialities = (List<String>) CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("speciality"));

					doctorProfile.setSpecialities(specialities);
					doctorProfile.setParentSpecialities(parentSpecialities);
				}

				if (doctorCollection.getServices() != null && !doctorCollection.getServices().isEmpty()) {
					services = (List<String>) CollectionUtils.collect(
							(Collection<?>) servicesRepository.findAllById(doctorCollection.getServices()),
							new BeanToPropertyValueTransformer("service"));
				}
				doctorProfile.setServices(services);

				// set medical councils using medical councils ids
				registrationDetails = new ArrayList<DoctorRegistrationDetail>();
				if (doctorProfile.getRegistrationDetails() != null
						&& !doctorProfile.getRegistrationDetails().isEmpty()) {
					for (DoctorRegistrationDetail registrationDetail : doctorProfile.getRegistrationDetails()) {
						DoctorRegistrationDetail doctorRegistrationDetail = new DoctorRegistrationDetail();
						BeanUtil.map(registrationDetail, doctorRegistrationDetail);
						registrationDetails.add(doctorRegistrationDetail);
					}
				}
				doctorProfile.setRegistrationDetails(registrationDetails);
				// set professional memberships using professional membership
				// ids
				if (doctorCollection.getProfessionalMemberships() != null
						&& !doctorCollection.getProfessionalMemberships().isEmpty()) {
					professionalMemberships = (List<String>) CollectionUtils.collect(
							(Collection<?>) professionalMembershipRepository
									.findAllById(doctorCollection.getProfessionalMemberships()),
							new BeanToPropertyValueTransformer("membership"));
				}

				doctorProfile.setProfessionalMemberships(professionalMemberships);

				// set clinic profile details
				doctorProfile.setClinicProfile(clinicProfile);
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Profile by userUId");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Profile by userUId");
		}
		return doctorProfile;
	}

	@Override
	public Boolean updatePrescriptionSMS(String doctorId, Boolean isSendSMS) {
		Boolean response = false;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No doctor found by doctorId");
			}
			doctorCollection.setIsPrescriptionSMS(isSendSMS);
			doctorCollection.setUpdatedTime(new Date());
			doctorRepository.save(doctorCollection);
			response = true;

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Profile by doctorId");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Profile by doctorId");
		}
		return response;
	}

	@Override
	public Boolean updateShowInventory(String doctorId, String locationId, Boolean showInventory) {
		Boolean response = false;
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No doctor clinic profile found");
			}
			doctorClinicProfileCollection.setShowInventory(showInventory);
			doctorClinicProfileCollection.setUpdatedTime(new Date());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = true;

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	public Boolean updateShowInventoryCount(String doctorId, String locationId, Boolean showInventoryCount) {
		Boolean response = false;
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No doctor clinic profile found");
			}
			doctorClinicProfileCollection.setShowInventoryCount(showInventoryCount);
			doctorClinicProfileCollection.setUpdatedTime(new Date());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = true;

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	public Boolean updateSavetoInventory(String doctorId, String locationId, Boolean saveToInventory) {
		Boolean response = false;
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No doctor clinic profile found");
			}
			doctorClinicProfileCollection.setSaveToInventory(saveToInventory);
			doctorClinicProfileCollection.setUpdatedTime(new Date());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = true;

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	public DoctorServicesAddEditRequest addEditServices(DoctorServicesAddEditRequest request) {
		DoctorServicesAddEditRequest response = null;
		DoctorCollection doctorCollection = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (doctorCollection != null) {
				response = new DoctorServicesAddEditRequest();
				if (request.getServices() != null && !request.getServices().isEmpty()) {
					List<ServicesCollection> servicesCollections = servicesRepository
							.findbyService(request.getServices());
					@SuppressWarnings("unchecked")
					List<ObjectId> serviceIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
							new BeanToPropertyValueTransformer("id"));
					
					Set<ObjectId> services = new HashSet<>(serviceIds);
					if (services != null && !services.isEmpty()) {
						doctorCollection.setServices(services);
					} else {
						doctorCollection.setServices(null);
					}
				} else {
					doctorCollection.setServices(null);
				}
				doctorRepository.save(doctorCollection);
				BeanUtil.map(doctorCollection, response);
				response.setDoctorId(doctorCollection.getUserId().toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDrugTypePlacement(String doctorId, String drugTypePlacement) {
		Boolean response = false;
		DoctorCollection doctorCollection = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection != null) {
				doctorCollection.setDrugTypePlacement(drugTypePlacement);
				doctorRepository.save(doctorCollection);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Drug type placement");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Drug type placement");
		}
		return response;
	}

	@Override
	public List<Services> getServices(int page, int size, String updatedTime) {
		List<Services> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Services> aggregationResults = mongoTemplate.aggregate(aggregation,
					ServicesCollection.class, Services.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Services");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Services");
		}
		return response;
	}
}
