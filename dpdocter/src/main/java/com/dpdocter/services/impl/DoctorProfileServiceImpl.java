package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Speciality;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.DoctorExperienceUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.EducationInstituteRepository;
import com.dpdocter.repository.EducationQualificationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MedicalCouncilRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
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
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.FileManager;

import common.util.web.DPDoctorUtils;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

    private static Logger logger = Logger.getLogger(DoctorProfileServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MedicalCouncilRepository medicalCouncilRepository;

    @Autowired
    private ProfessionalMembershipRepository professionalMembershipRepository;

    @Autowired
    private DoctorClinicProfileRepository doctorClinicProfileRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private EducationQualificationRepository educationQualificationRepository;

    @Autowired
    private EducationInstituteRepository educationInstituteRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Override
    @Transactional
    public Boolean addEditName(DoctorNameAddEditRequest request) {
	UserCollection userCollection = null;
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    userCollection = userRepository.findOne(request.getDoctorId());
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    BeanUtil.map(request, userCollection);
	    BeanUtil.map(request, doctorCollection);
	    userRepository.save(userCollection);
	    doctorRepository.save(doctorCollection);

	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public DoctorExperience addEditExperience(DoctorExperienceAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	DoctorExperience response = new DoctorExperience();
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    DoctorExperience doctorExperience = new DoctorExperience();
	    doctorExperience.setExperience(request.getExperience());
	    doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
	    doctorCollection.setExperience(doctorExperience);
	    doctorRepository.save(doctorCollection);
	    BeanUtil.map(doctorExperience, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditContact(DoctorContactAddEditRequest request) {
	UserCollection userCollection = null;
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    userCollection = userRepository.findOne(request.getDoctorId());
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    userCollection.setMobileNumber(request.getMobileNumber());
	    doctorCollection.setAdditionalNumbers(request.getAdditionalNumbers());
	    doctorCollection.setOtherEmailAddresses(request.getOtherEmailAddresses());
	    userRepository.save(userCollection);
	    doctorRepository.save(doctorCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditEducation(DoctorEducationAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setEducation(request.getEducation());
	    doctorRepository.save(doctorCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditMedicalCouncils(List<MedicalCouncil> medicalCouncils) {
	List<MedicalCouncilCollection> medicalCouncilCollections = null;
	Boolean response = false;
	try {
	    medicalCouncilCollections = new ArrayList<MedicalCouncilCollection>();
	    for (MedicalCouncil medicalCouncil : medicalCouncils) {
		MedicalCouncilCollection medicalCouncilCollection = new MedicalCouncilCollection();
		if (medicalCouncil.getId() == null) {
		    medicalCouncilCollection.setCreatedTime(new Date());
		}
		BeanUtil.map(medicalCouncil, medicalCouncilCollection);
		medicalCouncilCollections.add(medicalCouncilCollection);
	    }
	    medicalCouncilRepository.save(medicalCouncilCollections);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Medical Councils");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Medical Councils");
	}
	return response;
    }

    @Override
    @Transactional
    public List<MedicalCouncil> getMedicalCouncils(int page, int size, String updatedTime) {
	List<MedicalCouncil> medicalCouncils = null;
	List<MedicalCouncilCollection> medicalCouncilCollections = null;
	try {
	    long updatedTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		medicalCouncilCollections = medicalCouncilRepository.find(new Date(updatedTimeStamp),
			new PageRequest(page, size, Sort.Direction.ASC, "medicalCouncil"));
	    else
		medicalCouncilCollections = medicalCouncilRepository.find(new Date(updatedTimeStamp), new Sort(Sort.Direction.ASC, "medicalCouncil"));
	    medicalCouncils = new ArrayList<MedicalCouncil>();
	    BeanUtil.map(medicalCouncilCollections, medicalCouncils);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Getting Medical Councils");
	    throw new BusinessException(ServiceError.Unknown, "Error Getting Medical Councils");
	}
	return medicalCouncils;
    }

    @Override
    @Transactional
    public List<String> addEditSpeciality(DoctorSpecialityAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	List<SpecialityCollection> specialityCollections = null;
	List<String> specialities = null;
	List<String> specialitiesByName = null;
	try {
	    specialityCollections = specialityRepository.findAll();
	    specialities = new ArrayList<String>();
	    specialitiesByName = new ArrayList<String>();

	    if (request.getSpeciality() != null) {
		if (!request.getSpeciality().isEmpty()) {
		    specialityCollections = specialityRepository.findAll();
		    specialities = new ArrayList<String>();
		    for (String speciality : request.getSpeciality()) {
//			Boolean specialityFound = false;
			for (SpecialityCollection specialityCollection : specialityCollections) {
			    if (speciality.trim().equalsIgnoreCase(specialityCollection.getSuperSpeciality())) {
				specialities.add(specialityCollection.getId());
//				specialityFound = true;
				break;
			    }
			}
//			if (!specialityFound) {
//			    SpecialityCollection specialityCollection = new SpecialityCollection();
//			    specialityCollection.setSpeciality(speciality);
//			    specialityCollection.setSuperSpeciality(speciality);
//			    specialityCollection.setCreatedTime(new Date());
//			    specialityCollection = specialityRepository.save(specialityCollection);
//			    specialities.add(specialityCollection.getId());
//			}
		    }
		}
	    }
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setSpecialities(specialities);
	    doctorRepository.save(doctorCollection);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return specialitiesByName;
    }

    @Override
    @Transactional
    public Boolean addEditAchievement(DoctorAchievementAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setAchievements(request.getAchievements());
	    doctorRepository.save(doctorCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setProfessionalStatement(request.getProfessionalStatement());
	    doctorRepository.save(doctorCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setRegistrationDetails(request.getRegistrationDetails());
	    doctorRepository.save(doctorCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setExperienceDetails(request.getExperienceDetails());
	    doctorRepository.save(doctorCollection);
	    response = true;
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
	    userCollection = userRepository.findOne(request.getDoctorId());
	    if (request.getImage() != null) {
		String path = "profile-image";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
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
	    userCollection = userRepository.findOne(request.getDoctorId());
	    if (request.getImage() != null) {
		String path = "cover-image";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
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
    public DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId) {
	DoctorProfile doctorProfile = null;
	UserCollection userCollection = null;
	DoctorCollection doctorCollection = null;
	LocationCollection locationCollection = null;
	List<String> specialities = null;
	List<DoctorRegistrationDetail> registrationDetails = null;
	List<String> professionalMemberships = null;
	List<DoctorClinicProfile> clinicProfile = new ArrayList<DoctorClinicProfile>();
	DoctorClinicProfile doctorClinic = new DoctorClinicProfile();
	try {
	    userCollection = userRepository.findOne(doctorId);
	    doctorCollection = doctorRepository.findByUserId(doctorId);
	    if(userCollection == null || doctorCollection == null){
		    logger.error("No user found");
		    throw new BusinessException(ServiceError.NoRecord, "No user found");	
	    }
	    if (locationId == null) {
		List<UserLocationCollection> userLocationCollections = userLocationRepository.findByUserId(userCollection.getId());
		for (Iterator<UserLocationCollection> iterator = userLocationCollections.iterator(); iterator.hasNext();) {
		    UserLocationCollection userLocationCollection = iterator.next();
		    DoctorClinicProfileCollection doctorClinicCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());

		    locationCollection = locationRepository.findOne(userLocationCollection.getLocationId());
		    if (locationCollection != null) {
			String address = locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress()
				: "" + locationCollection.getCity() != null ? ", "+locationCollection.getCity()
					: "" + locationCollection.getPostalCode() != null ? ", "+locationCollection.getPostalCode() 
								: "" + locationCollection.getState() != null ? ", "+locationCollection.getState() 
									: "" + locationCollection.getCountry() != null ? ", "+locationCollection.getCountry() : "";

			doctorClinic.setClinicAddress(address);
			BeanUtil.map(locationCollection, doctorClinic);
		    }
		    if (doctorClinicCollection != null)
			BeanUtil.map(doctorClinicCollection, doctorClinic);
		    doctorClinic.setLocationId(userLocationCollection.getLocationId());
		    doctorClinic.setDoctorId(doctorId);
		    clinicProfile.add(doctorClinic);
		}
	    } else {
		UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(userCollection.getId(), locationId);
		if (userLocationCollection != null) {
		    DoctorClinicProfileCollection doctorClinicCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());

		    locationCollection = locationRepository.findOne(locationId);
		    if (locationCollection != null) {
		    	String address = locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress()
						: "" + locationCollection.getCity() != null ? ", "+locationCollection.getCity()
							: "" + locationCollection.getPostalCode() != null ? ", "+locationCollection.getPostalCode() 
										: "" + locationCollection.getState() != null ? ", "+locationCollection.getState() 
											: "" + locationCollection.getCountry() != null ? ", "+locationCollection.getCountry() : "";

			BeanUtil.map(locationCollection, doctorClinic);
			doctorClinic.setClinicAddress(address);
		    }
		    doctorClinic.setLocationId(userLocationCollection.getLocationId());
		    doctorClinic.setDoctorId(userLocationCollection.getUserId());
		    clinicProfile.add(doctorClinic);
		    if (doctorClinicCollection != null)
			BeanUtil.map(doctorClinicCollection, doctorClinic);
		}
	    }
	    doctorProfile = new DoctorProfile();
	    BeanUtil.map(userCollection, doctorProfile);
	    BeanUtil.map(doctorCollection, doctorProfile);

	    doctorProfile.setClinicProfile(clinicProfile);
	    // set specialities using speciality ids
	    if (doctorProfile.getSpecialities() != null) {
		specialities = (List<String>) CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctorProfile.getSpecialities()),
			new BeanToPropertyValueTransformer("superSpeciality"));
	    }
	    doctorProfile.setSpecialities(specialities);

	    // set medical councils using medical councils ids
	    registrationDetails = new ArrayList<DoctorRegistrationDetail>();
	    if (doctorProfile.getRegistrationDetails() != null) {
		for (DoctorRegistrationDetail registrationDetail : doctorProfile.getRegistrationDetails()) {
		    DoctorRegistrationDetail doctorRegistrationDetail = new DoctorRegistrationDetail();
		    BeanUtil.map(registrationDetail, doctorRegistrationDetail);
		    MedicalCouncilCollection medicalCouncilCollection = medicalCouncilRepository.findOne(registrationDetail.getMedicalCouncil());
		    if (medicalCouncilCollection != null)
			doctorRegistrationDetail.setMedicalCouncil(medicalCouncilCollection.getMedicalCouncil());
		    registrationDetails.add(doctorRegistrationDetail);
		}
	    }
	    doctorProfile.setRegistrationDetails(registrationDetails);
	    // set professional memberships using professional membership ids
	    if (doctorProfile.getProfessionalMemberships() != null) {
		professionalMemberships = (List<String>) CollectionUtils.collect(
			(Collection<?>) professionalMembershipRepository.findAll(doctorProfile.getProfessionalMemberships()),
			new BeanToPropertyValueTransformer("membership"));
	    }
	    doctorProfile.setProfessionalMemberships(professionalMemberships);

	    // set clinic profile details
	    doctorProfile.setClinicProfile(clinicProfile);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Getting Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Profile");
	}
	return doctorProfile;
    }

    @Override
    @Transactional
    public Boolean insertProfessionalMemberships(List<ProfessionalMembership> professionalMemberships) {
	List<ProfessionalMembershipCollection> professionalMembershipCollections = null;
	Boolean response = false;
	try {
	    professionalMembershipCollections = new ArrayList<ProfessionalMembershipCollection>();
	    BeanUtil.map(professionalMemberships, professionalMembershipCollections);
	    for (ProfessionalMembershipCollection professionalMembership : professionalMembershipCollections) {
		if (professionalMembership.getId() == null)
		    professionalMembership.setCreatedTime(new Date());
	    }
	    professionalMembershipRepository.save(professionalMembershipCollections);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Inserting Professional Memberships");
	    throw new BusinessException(ServiceError.Unknown, "Error Inserting Professional Memberships");
	}
	return response;
    }

    @Override
    @Transactional
    public List<ProfessionalMembership> getProfessionalMemberships(int page, int size, String updatedTime) {
	List<ProfessionalMembership> professionalMemberships = null;
	List<ProfessionalMembershipCollection> professionalMembershipCollections = null;
	try {
	    long updatedTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		professionalMembershipCollections = professionalMembershipRepository.find(new Date(updatedTimeStamp),
			new PageRequest(page, size, Sort.Direction.ASC, "membership"));
	    else
		professionalMembershipCollections = professionalMembershipRepository.find(new Date(updatedTimeStamp),
			new Sort(Sort.Direction.ASC, "membership"));
	    professionalMemberships = new ArrayList<ProfessionalMembership>();
	    BeanUtil.map(professionalMembershipCollections, professionalMemberships);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Getting Professional Memberships");
	    throw new BusinessException(ServiceError.Unknown, "Error Getting Professional Memberships");
	}
	return professionalMemberships;
    }

    @Override
    @Transactional
    public Boolean addEditProfessionalMembership(DoctorProfessionalAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	List<ProfessionalMembershipCollection> professionalMembershipCollections = null;
	List<String> professionalMemberships = null;
	Boolean response = false;
	try {
	    professionalMembershipCollections = professionalMembershipRepository.findAll();
	    professionalMemberships = new ArrayList<String>();
	    if (request.getMembership() != null)
		for (String professionalMembership : request.getMembership()) {
		    Boolean professionalMembershipFound = false;
		    for (ProfessionalMembershipCollection professionalMembershipCollection : professionalMembershipCollections) {
			if (professionalMembership.trim().equalsIgnoreCase(professionalMembershipCollection.getMembership())) {
			    professionalMemberships.add(professionalMembershipCollection.getId());
			    professionalMembershipFound = true;
			    break;
			}
		    }
		    if (!professionalMembershipFound) {
			ProfessionalMembershipCollection professionalMembershipCollection = new ProfessionalMembershipCollection();
			professionalMembershipCollection.setMembership(professionalMembership);
			professionalMembershipCollection.setCreatedTime(new Date());
			professionalMembershipCollection = professionalMembershipRepository.save(professionalMembershipCollection);
			professionalMemberships.add(professionalMembershipCollection.getId());
		    }
		}
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setProfessionalMemberships(professionalMemberships);
	    doctorRepository.save(doctorCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	Boolean response = false;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		if (doctorClinicProfileCollection == null) {
		    doctorClinicProfileCollection = new DoctorClinicProfileCollection();
		    doctorClinicProfileCollection.setUserLocationId(userLocationCollection.getId());
		    doctorClinicProfileCollection.setCreatedTime(new Date());
		}
		doctorClinicProfileCollection.setAppointmentBookingNumber(request.getAppointmentBookingNumber());
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		response = true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Clinic Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditVisitingTime(DoctorVisitingTimeAddEditRequest request) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	Boolean response = false;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		if (doctorClinicProfileCollection == null) {
		    doctorClinicProfileCollection = new DoctorClinicProfileCollection();
		    doctorClinicProfileCollection.setUserLocationId(userLocationCollection.getId());
		    doctorClinicProfileCollection.setCreatedTime(new Date());
		}
		doctorClinicProfileCollection.setWorkingSchedules(request.getWorkingSchedules());
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		response = true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Clinic Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditConsultationFee(DoctorConsultationFeeAddEditRequest request) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	Boolean response = false;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		if (doctorClinicProfileCollection == null) {
		    doctorClinicProfileCollection = new DoctorClinicProfileCollection();
		    doctorClinicProfileCollection.setUserLocationId(userLocationCollection.getId());
		    doctorClinicProfileCollection.setCreatedTime(new Date());
		}
		doctorClinicProfileCollection.setConsultationFee(request.getConsultationFee());
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		response = true;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Clinic Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	Boolean response = false;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		if (doctorClinicProfileCollection == null) {
		    doctorClinicProfileCollection = new DoctorClinicProfileCollection();
		    doctorClinicProfileCollection.setUserLocationId(userLocationCollection.getId());
		    doctorClinicProfileCollection.setCreatedTime(new Date());
		}
		doctorClinicProfileCollection.setAppointmentSlot(request.getAppointmentSlot());
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		response = true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Clinic Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditGeneralInfo(DoctorGeneralInfo request) {
	boolean response = false;
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		if (doctorClinicProfileCollection == null) {
		    doctorClinicProfileCollection = new DoctorClinicProfileCollection();
		    doctorClinicProfileCollection.setUserLocationId(userLocationCollection.getId());
		    doctorClinicProfileCollection.setCreatedTime(new Date());
		}
		doctorClinicProfileCollection.setAppointmentBookingNumber(request.getAppointmentBookingNumber());
		doctorClinicProfileCollection.setConsultationFee(request.getConsultationFee());
		doctorClinicProfileCollection.setAppointmentSlot(request.getAppointmentSlot());
		doctorClinicProfileCollection.setFacility(request.getFacility());
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		response = true;
	    }
	} catch (Exception e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error while adding or editing general info : " + e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public List<Speciality> getSpecialities(int page, int size, String updatedTime) {
	List<Speciality> specialities = null;
	List<SpecialityCollection> specialitiesCollections = null;
	try {

	    long updatedTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		specialitiesCollections = specialityRepository.find(new Date(updatedTimeStamp),
			new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
	    else
		specialitiesCollections = specialityRepository.find(new Date(updatedTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    specialities = new ArrayList<Speciality>();
	    BeanUtil.map(specialitiesCollections, specialities);
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
	List<EducationInstituteCollection> educationInstituteCollections = null;
	try {
	    long updatedTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		educationInstituteCollections = educationInstituteRepository.find(new Date(updatedTimeStamp),
			new PageRequest(page, size, Sort.Direction.ASC, "name"));
	    else
		educationInstituteCollections = educationInstituteRepository.find(new Date(updatedTimeStamp), new Sort(Sort.Direction.ASC, "name"));

	    educationInstitutes = new ArrayList<EducationInstitute>();
	    BeanUtil.map(educationInstituteCollections, educationInstitutes);
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
	List<EducationQualificationCollection> qualificationCollections = null;
	try {
	    long updatedTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		qualificationCollections = educationQualificationRepository.find(new Date(updatedTimeStamp),
			new PageRequest(page, size, Sort.Direction.ASC, "name"));
	    else
		qualificationCollections = educationQualificationRepository.find(new Date(updatedTimeStamp), new Sort(Sort.Direction.ASC, "name"));
	    qualifications = new ArrayList<EducationQualification>();
	    BeanUtil.map(qualificationCollections, qualifications);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Getting Professional Memberships");
	    throw new BusinessException(ServiceError.Unknown, "Error Getting Professional Memberships");
	}
	return qualifications;
    }

    @Override
    @Transactional
    public DoctorMultipleDataAddEditResponse addEditMultipleData(DoctorMultipleDataAddEditRequest request) {
	UserCollection userCollection = null;
	DoctorCollection doctorCollection = null;
	List<SpecialityCollection> specialityCollections = null;
	List<String> specialities = null;
	List<String> specialitiesresponse = null;
	DoctorMultipleDataAddEditResponse response = null;
	try {
	    userCollection = userRepository.findOne(request.getDoctorId());
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
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
		doctorCollection.setDob(request.getDob());
		
		response = new DoctorMultipleDataAddEditResponse();
		response.setDoctorId(request.getDoctorId());

		if (request.getExperience() != null) {
		    DoctorExperience doctorExperience = new DoctorExperience();
		    doctorExperience.setExperience(request.getExperience());
		    doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
		    doctorCollection.setExperience(doctorExperience);
		}

		if (request.getSpeciality() != null) {
		    if (!request.getSpeciality().isEmpty()) {
			specialityCollections = specialityRepository.findAll();
			specialities = new ArrayList<String>();
			specialitiesresponse = new ArrayList<String>();
			for (String speciality : request.getSpeciality()) {
//			    Boolean specialityFound = false;
			    for (SpecialityCollection specialityCollection : specialityCollections) {
				if (speciality.trim().equalsIgnoreCase(specialityCollection.getSuperSpeciality())) {
				    specialities.add(specialityCollection.getId());
				    specialitiesresponse.add(specialityCollection.getSuperSpeciality());
//				    specialityFound = true;
				    break;
				}
			    }
//			    if (!specialityFound) {
//				SpecialityCollection specialityCollection = new SpecialityCollection();
//				specialityCollection.setSpeciality(speciality);
//				specialityCollection.setCreatedTime(new Date());
//				specialityCollection = specialityRepository.save(specialityCollection);
//				specialities.add(specialityCollection.getId());
//				specialitiesresponse.add(specialityCollection.getSpeciality());
//			    }
			}
			doctorCollection.setSpecialities(specialities);
		    } else {
			doctorCollection.setSpecialities(new ArrayList<String>());
		    }
		}

		if (request.getProfileImage() != null) {
		    String path = "profile-image";
		    // save image
		    request.getProfileImage().setFileName(request.getProfileImage().getFileName() + new Date().getTime());
		    ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getProfileImage(), path, true);
		    userCollection.setImageUrl(imageURLResponse.getImageUrl());
		    userCollection.setThumbnailUrl(imageURLResponse.getImageUrl());
		    response.setProfileImageUrl(imageURLResponse.getImageUrl());
		    response.setThumbnailProfileImageUrl(imageURLResponse.getImageUrl());
		}

		if (request.getCoverImage() != null) {
		    String path = "cover-image";
		    // save image
		    request.getCoverImage().setFileName(request.getCoverImage().getFileName() + new Date().getTime());
		    ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getCoverImage(), path, true);
		    userCollection.setCoverImageUrl(imageURLResponse.getImageUrl());
		    userCollection.setCoverThumbnailImageUrl(imageURLResponse.getThumbnailUrl());
		    response.setCoverImageUrl(imageURLResponse.getImageUrl());
		    response.setThumbnailCoverImageUrl(imageURLResponse.getThumbnailUrl());
		}
		userCollection = userRepository.save(userCollection);
		doctorCollection = doctorRepository.save(doctorCollection);

		BeanUtil.map(userCollection, response);
		BeanUtil.map(doctorCollection, response);
		response.setSpecialities(specialitiesresponse);
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
    public Boolean addEditFacility(DoctorAddEditFacilityRequest request) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	Boolean response = false;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		if (doctorClinicProfileCollection == null) {
		    doctorClinicProfileCollection = new DoctorClinicProfileCollection();
		    doctorClinicProfileCollection.setUserLocationId(userLocationCollection.getId());
		    doctorClinicProfileCollection.setCreatedTime(new Date());
		} else {
		    doctorClinicProfileCollection.setUpdatedTime(new Date());
		}
		doctorClinicProfileCollection.setFacility(request.getFacility());
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		response = true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Clinic Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
	}
	return response;

    }

    @Override
    @Transactional
    public Boolean addEditGender(DoctorGenderAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setGender(request.getGender());
	    doctorRepository.save(doctorCollection);

	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Gender");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Gender");
	}
	return response;
    }

    @Override
    @Transactional
    public Boolean addEditDOB(DoctorDOBAddEditRequest request) {
	DoctorCollection doctorCollection = null;
	Boolean response = false;
	try {
	    doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    doctorCollection.setDob(request.getDob());
	    doctorRepository.save(doctorCollection);

	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Editing Doctor Profile");
	    throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
	}
	return response;
    }
}
