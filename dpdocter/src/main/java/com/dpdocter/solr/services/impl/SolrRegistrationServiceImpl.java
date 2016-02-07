package com.dpdocter.solr.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.AdvancedSearch;
import com.dpdocter.solr.beans.AdvancedSearchParameter;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.beans.SolrWorkingSchedule;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.enums.AdvancedSearchType;
import com.dpdocter.solr.repository.SolrDoctorRepository;
import com.dpdocter.solr.repository.SolrPatientRepository;
import com.dpdocter.solr.response.SolrPatientResponse;
import com.dpdocter.solr.response.SolrPatientResponseDetails;
import com.dpdocter.solr.services.SolrRegistrationService;
import common.util.web.DPDoctorUtils;

@Service
public class SolrRegistrationServiceImpl implements SolrRegistrationService {

    private static Logger logger = Logger.getLogger(SolrRegistrationServiceImpl.class.getName());

    @Autowired
    private SolrDoctorRepository solrDoctorRepository;

    @Autowired
    private SolrPatientRepository solrPatientRepository;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Override
    public boolean addPatient(SolrPatientDocument request) {
	boolean response = false;
	try {
	    solrPatientRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getUserId(), Resource.PATIENT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Saving Patient");
	    // throw new BusinessException(ServiceError.Forbidden,
	    // "Error Occurred While Saving Patient");
	}
	return response;
    }

    @Override
    public boolean editPatient(SolrPatientDocument request) {
	boolean response = false;
	try {
	    solrPatientRepository.save(request);
	    response = true;
	    transnationalService.addResource(request.getUserId(), Resource.PATIENT, true);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Editing Patient");
	    // throw new BusinessException(ServiceError.Forbidden,
	    // "Error Occurred While Editing Patient");
	}
	return response;
    }

    @Override
    public boolean deletePatient(String id) {
	boolean response = false;
	try {
	    solrPatientRepository.delete(id);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Deleting Patient");
	    throw new BusinessException(ServiceError.Forbidden, "Error Occurred While Deleting Patient");
	}
	return response;
    }

    @Override
    public SolrPatientResponseDetails searchPatient(String doctorId, String locationId, String hospitalId, String searchTerm, int page, int size,
	    UriInfo uriInfo) {

	List<SolrPatientDocument> patients = new ArrayList<SolrPatientDocument>();
	List<SolrPatientResponse> patientsResponse = null;
	SolrPatientResponseDetails patientResponseDetails = null;
	try {

	    Criteria advancedCriteria = Criteria.where("doctorId").is(doctorId).and("locationId").is(locationId).and("hospitalId").is(hospitalId);

	    if (searchTerm != null && !searchTerm.isEmpty()) {
		searchTerm = searchTerm.replaceAll("\\s+", "");

		Criteria criteria = Criteria.where(AdvancedSearchType.FIRST_NAME.getSearchType()).contains(searchTerm)
			.or(AdvancedSearchType.EMAIL_ADDRESS.getSearchType()).contains(Arrays.asList(searchTerm))
			.or(AdvancedSearchType.MOBILE_NUMBER.getSearchType()).contains(Arrays.asList(searchTerm)).or(AdvancedSearchType.PID.getSearchType())
			.contains(Arrays.asList(searchTerm));
		advancedCriteria.and(criteria);
	    }

	    SimpleQuery query = new SimpleQuery(advancedCriteria);

	    solrTemplate.setSolrCore("patients");
	    if (size > 0)
		patients = solrTemplate.queryForPage(query.setPageRequest(new PageRequest(page, size)), SolrPatientDocument.class).getContent();
	    else
		patients = solrTemplate.queryForPage(query, SolrPatientDocument.class).getContent();

	    if (patients != null && !patients.isEmpty()) {
		patientsResponse = new ArrayList<SolrPatientResponse>();
		for (SolrPatientDocument patient : patients) {
		    SolrPatientResponse patientResponse = new SolrPatientResponse();

		    patient.setImageUrl(getFinalImageURL(patient.getImageUrl(), uriInfo));
		    patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl(), uriInfo));

		    BeanUtil.map(patient, patientResponse);
		    // Reference reference = new Reference();
		    // reference.setReference(patient.getReferredBy());
		    // patientResponse.setReferredBy(reference);
		    patientsResponse.add(patientResponse);
		}
		patientResponseDetails = new SolrPatientResponseDetails();
		patientResponseDetails.setPatients(patientsResponse);
		patientResponseDetails.setTotalSize(solrTemplate.count(new SimpleQuery(advancedCriteria)));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patient");
	    throw new BusinessException(ServiceError.Forbidden, "Error Occurred While Searching Patients");
	}
	return patientResponseDetails;
    }

    @Override
    public SolrPatientResponseDetails searchPatient(AdvancedSearch request, UriInfo uriInfo) {
	List<SolrPatientDocument> patients = null;
	List<SolrPatientResponse> response = new ArrayList<SolrPatientResponse>();
	SolrPatientResponseDetails responseDetails = null;
	try {
	    Criteria advancedCriteria = createAdvancedSearchCriteria(request);

	    SimpleQuery query = new SimpleQuery(advancedCriteria);

	    solrTemplate.setSolrCore("patients");
	    if (request.getSize() > 0)
		patients = solrTemplate.queryForPage(
			query.setPageRequest(new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime")), SolrPatientDocument.class)
			.getContent();
	    else
		patients = solrTemplate.queryForPage(query.addSort(new Sort(Sort.Direction.DESC, "createdTime")), SolrPatientDocument.class).getContent();

	    if (patients != null && !patients.isEmpty()) {
		response = new ArrayList<SolrPatientResponse>();
		for (SolrPatientDocument patient : patients) {
		    SolrPatientResponse patientResponse = new SolrPatientResponse();

		    patient.setImageUrl(getFinalImageURL(patient.getImageUrl(), uriInfo));
		    patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl(), uriInfo));

		    BeanUtil.map(patient, patientResponse);
		    response.add(patientResponse);
		}
		responseDetails = new SolrPatientResponseDetails();
		responseDetails.setPatients(response);
		responseDetails.setTotalSize(solrTemplate.count(new SimpleQuery(advancedCriteria)));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patients");
	    throw new BusinessException(ServiceError.Forbidden, "Error Occurred While Searching Patients");
	}
	return responseDetails;
    }

    private Criteria createAdvancedSearchCriteria(AdvancedSearch request) throws ParseException {
	String doctorId = request.getDoctorId();
	String locationId = request.getLocationId();
	String hospitalId = request.getHospitalId();

	Criteria advancedCriteria = Criteria.where("doctorId").is(doctorId).and("locationId").is(locationId).and("hospitalId").is(hospitalId);

	if (request.getSearchParameters() != null && !request.getSearchParameters().isEmpty()) {
	    for (AdvancedSearchParameter searchParameter : request.getSearchParameters()) {
		String searchValue = searchParameter.getSearchValue();
		String searchType = searchParameter.getSearchType().getSearchType();
		if (!DPDoctorUtils.anyStringEmpty(searchValue, searchType)) {
		    if (searchType.equalsIgnoreCase(AdvancedSearchType.DOB.getSearchType())) {
			String[] dob = searchValue.split("/");
			if (advancedCriteria == null) {
			    advancedCriteria = new Criteria("days").is(Integer.parseInt(dob[1])).and("months").is(Integer.parseInt(dob[0])).and("years")
				    .is(Integer.parseInt(dob[2]));
			} else {
			    advancedCriteria = advancedCriteria.and("days").is(Integer.parseInt(dob[1])).and("months").is(Integer.parseInt(dob[0]))
				    .and("years").is(Integer.parseInt(dob[2]));
			}
		    } else if (searchType.equalsIgnoreCase(AdvancedSearchType.REGISTRATION_DATE.getSearchType())) {
			String[] dob = searchValue.split("/");
			DateTime start = new DateTime(Integer.parseInt(dob[2]), Integer.parseInt(dob[0]), Integer.parseInt(dob[1]), 0, 0, 0);

			DateTime end = new DateTime(Integer.parseInt(dob[2]), Integer.parseInt(dob[0]), Integer.parseInt(dob[1]), 23, 59, 59);
			if (advancedCriteria == null) {
			    advancedCriteria = new Criteria("createdTime").between(start, end);
			} else {
			    advancedCriteria = advancedCriteria.and("createdTime").between(start, end);
			}
		    } else if (searchType.equalsIgnoreCase(AdvancedSearchType.REFERRED_BY.getSearchType())
			    || searchType.equalsIgnoreCase(AdvancedSearchType.PROFESSION.getSearchType())) {
			if (advancedCriteria == null) {
			    advancedCriteria = new Criteria(searchType).is(searchValue);
			} else {
			    advancedCriteria = advancedCriteria.and(searchType).is(searchValue);
			}
		    } else {
			searchValue = searchValue.replaceAll("\\s+", "");
			if (advancedCriteria == null) {
			    advancedCriteria = new Criteria(searchType).contains(searchValue);
			} else {
			    advancedCriteria = advancedCriteria.and(searchType).contains(searchValue);
			}
		    }

		}
	    }
	}
	return advancedCriteria;
    }

    // @Override
    // public List<SolrPatientDocument> searchPatientByFirstName(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByFirstName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByMiddleName(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByMiddleName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByLastName(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByLastName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByPID(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByPID(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByMobileNumber(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByMobileNumber(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByEmailAddress(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByEmailAddress(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByUserName(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByUserName(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByCity(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByCity(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByLocality(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByLocality(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByBloodGroup(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByBloodGroup(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByReferredBy(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByReferredBy(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByProfession(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByProfession(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByPostalCode(String
    // doctorId, String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByPostalCode(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }
    //
    // @Override
    // public List<SolrPatientDocument> searchPatientByGender(String doctorId,
    // String locationId, String hospitalId, String searchValue) {
    // List<SolrPatientDocument> response = null;
    // try {
    // response = solrPatientRepository.findByGender(doctorId, locationId,
    // hospitalId, searchValue);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.error(e + " Error Occurred While Searching Patients");
    // throw new BusinessException(ServiceError.Forbidden,
    // "Error Occurred While Searching Patients");
    // }
    // return response;
    //
    // }

    @Override
    public void patientProfilePicChange(String username, String imageUrl) {
	SolrPatientDocument document = null;
	try {
	    document = solrPatientRepository.findByUserName(username);
	    if (document != null) {
		document.setImageUrl(imageUrl);
		solrPatientRepository.save(document);
		transnationalService.addResource(document.getId(), Resource.PATIENT, true);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Searching Patients");
	}

    }

    @Override
    public boolean addDoctor(SolrDoctorDocument request) {
	boolean response = false;
	try {
	    SolrDoctorDocument doctorDocument = solrDoctorRepository.findByUserIdAndLocationId(request.getUserId(), request.getLocationId());
	    if (doctorDocument != null)
		request.setId(doctorDocument.getId());
	    else
		request.setId(request.getUserId() + request.getLocationId());
	    solrDoctorRepository.save(request);
	    transnationalService.addResource(request.getUserId(), Resource.DOCTOR, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    // throw new BusinessException(ServiceError.Forbidden,
	    // "Error While Saving Doctor Details to Solr : " + e.getMessage());
	}
	return response;
    }

    @Override
    public void addEditName(DoctorNameAddEditRequest request) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByUserId(request.getDoctorId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		doctorDocument.setFirstName(request.getFirstName());
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditSpeciality(DoctorSpecialityAddEditRequest request) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByUserId(request.getDoctorId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		doctorDocument.setSpecialities(request.getSpeciality());
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditProfilePicture(String doctorId, String addEditProfilePictureResponse) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByUserId(doctorId);
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		doctorDocument.setImageUrl(addEditProfilePictureResponse);
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(doctorId, Resource.DOCTOR, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditVisitingTime(DoctorVisitingTimeAddEditRequest request) {
	try {
	    SolrDoctorDocument doctorDocuments = solrDoctorRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    List<SolrWorkingSchedule> solrWorkingSchedules = new ArrayList<SolrWorkingSchedule>();
	    if (request.getWorkingSchedules() != null) {
		for (WorkingSchedule workingSchedule : request.getWorkingSchedules()) {
		    SolrWorkingSchedule solrWorkingSchedule = new SolrWorkingSchedule();
		    solrWorkingSchedule.setWorkingDay(workingSchedule.getWorkingDay());
		    List<WorkingHours> hours = workingSchedule.getWorkingHours();
		    solrWorkingSchedule.setWorkingHours(hours);
		    solrWorkingSchedules.add(solrWorkingSchedule);
		}
	    }
	    doctorDocuments.setWorkingSchedules(solrWorkingSchedules);
	    doctorDocuments = solrDoctorRepository.save(doctorDocuments);
	    BeanUtil.map(doctorDocuments, request);
	    transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditConsultationFee(DoctorConsultationFeeAddEditRequest request) {
	try {
	    SolrDoctorDocument doctorDocuments = solrDoctorRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    doctorDocuments.setConsultationFee(request.getConsultationFee());
	    doctorDocuments = solrDoctorRepository.save(doctorDocuments);
	    BeanUtil.map(doctorDocuments, request);
	    transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditExperience(String doctorId, DoctorExperience experienceResponse) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByUserId(doctorId);
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		doctorDocument.setExperience(experienceResponse);
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(doctorId, Resource.DOCTOR, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditGeneralInfo(DoctorGeneralInfo request) {
	try {
	    SolrDoctorDocument doctorDocuments = solrDoctorRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (doctorDocuments != null) {
		doctorDocuments.setConsultationFee(request.getConsultationFee());
		solrDoctorRepository.save(doctorDocuments);
		transnationalService.addResource(request.getDoctorId(), Resource.DOCTOR, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void addEditMultipleData(DoctorMultipleDataAddEditResponse addEditNameResponse) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByUserId(addEditNameResponse.getDoctorId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		doctorDocument.setFirstName(addEditNameResponse.getFirstName());
		doctorDocument.setImageUrl(addEditNameResponse.getProfileImageUrl());
		doctorDocument.setExperience(addEditNameResponse.getExperience());
		doctorDocument.setSpecialities(addEditNameResponse.getSpecialities());
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(addEditNameResponse.getDoctorId(), Resource.DOCTOR, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private String getFinalImageURL(String imageURL, UriInfo uriInfo) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;
    }

    @Override
    public void updateClinicProfile(ClinicProfile clinicProfileUpdateResponse) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByLocationId(clinicProfileUpdateResponse.getId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		String mobileNumber = doctorDocument.getMobileNumber();
		BeanUtil.map(clinicProfileUpdateResponse, doctorDocument);
		doctorDocument.setMobileNumber(mobileNumber);
		doctorDocument.setLocationMobileNumber(clinicProfileUpdateResponse.getMobileNumber());
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(clinicProfileUpdateResponse.getId(), Resource.LOCATION, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void updateClinicAddress(ClinicAddress clinicAddressUpdateResponse) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByLocationId(clinicAddressUpdateResponse.getId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		BeanUtil.map(clinicAddressUpdateResponse, doctorDocument);
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(clinicAddressUpdateResponse.getId(), Resource.LOCATION, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void updateClinicSpecialization(ClinicSpecialization clinicSpecializationUpdateResponse) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByLocationId(clinicSpecializationUpdateResponse.getId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		BeanUtil.map(clinicSpecializationUpdateResponse, doctorDocument);
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(clinicSpecializationUpdateResponse.getId(), Resource.LOCATION, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void updateLabProperties(ClinicLabProperties clinicLabProperties) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByLocationId(clinicLabProperties.getId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		doctorDocument.setIsLab(clinicLabProperties.getIsLab());
		doctorDocument.setIsHomeServiceAvailable(clinicLabProperties.getIsHomeServiceAvailable());
		doctorDocument.setIsNABLAccredited(clinicLabProperties.getIsNABLAccredited());
		doctorDocument.setIsNABLAccredited(clinicLabProperties.getIsNABLAccredited());

		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(clinicLabProperties.getId(), Resource.LOCATION, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void editLocation(DoctorLocation doctorLocation) {
	try {
	    List<SolrDoctorDocument> doctorDocuments = solrDoctorRepository.findByLocationId(doctorLocation.getLocationId());
	    for (SolrDoctorDocument doctorDocument : doctorDocuments) {
		BeanUtil.map(doctorLocation, doctorDocument);
		solrDoctorRepository.save(doctorDocument);
		transnationalService.addResource(doctorLocation.getLocationId(), Resource.LOCATION, true);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
