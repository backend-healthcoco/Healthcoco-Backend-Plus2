package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.CalenderJasperBean;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.CustomAppointment;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.Event;
import com.dpdocter.beans.FieldsCollection;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.NutritionAppointment;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Slot;
import com.dpdocter.beans.User;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.AppointmentWorkFlowCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.CustomAppointmentCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionAppointmentCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientQueueCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.QueueStatus;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSContent;
import com.dpdocter.enums.SMSFormatType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.AppointmentWorkFlowRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.CustomAppointmentRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionAppointmentRepository;
import com.dpdocter.repository.PatientQueueRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserResourceFavouriteRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.request.PrintPatientCardRequest;
import com.dpdocter.response.AVGTimeDetail;
import com.dpdocter.response.AppointmentLookupResponse;
import com.dpdocter.response.CalenderJasperBeanList;
import com.dpdocter.response.CalenderResponse;
import com.dpdocter.response.CalenderResponseForJasper;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.DoctorWithAppointmentCount;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.LocationWithAppointmentCount;
import com.dpdocter.response.LocationWithPatientQueueDetails;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.UserLocationWithDoctorClinicProfile;
import com.dpdocter.response.UserRoleResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.UserFavouriteService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;
import common.util.web.Response;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	private static Logger logger = Logger.getLogger(AppointmentServiceImpl.class.getName());

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LandmarkLocalityRepository landmarkLocalityRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private NutritionAppointmentRepository nutritionAppointmentRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private AppointmentBookedSlotRepository appointmentBookedSlotRepository;

	@Autowired
	private AppointmentWorkFlowRepository appointmentWorkFlowRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private PatientQueueRepository patientQueueRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private CustomAppointmentRepository customAppointmentRepository;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Value(value = "${Appointment.timeSlotIsBooked}")
	private String timeSlotIsBooked;

	@Value(value = "${Appointment.incorrectAppointmentId}")
	private String incorrectAppointmentId;

	@Value(value = "${Appoinment.appointmentDoesNotExist}")
	private String appointmentDoesNotExist;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Value(value = "${mail.appointment.cancel.subject}")
	private String appointmentCancelMailSubject;

	@Value(value = "${mail.appointment.confirm.to.doctor.subject}")
	private String appointmentConfirmToDoctorMailSubject;

	@Value(value = "${mail.appointment.request.to.doctor.subject}")
	private String appointmentRequestToDoctorMailSubject;

	@Value(value = "${mail.appointment.confirm.to.patient.subject}")
	private String appointmentConfirmToPatientMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.doctor.subject}")
	private String appointmentRescheduleToDoctorMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.patient.subject}")
	private String appointmentRescheduleToPatientMailSubject;

	@Autowired
	UserRoleRepository UserRoleRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Value(value = "${jasper.print.dental.works.reports.fileName}")
	private String dentalWorksFormA4FileName;

	@Value(value = "${jasper.print.calender.a4.fileName}")
	private String calenderA4FileName;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private RecommendationsRepository recommendationsRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private UserResourceFavouriteRepository userResourceFavouriteRepository;

	@Autowired
	private UserFavouriteService userFavouriteService;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	@Transactional
	public City addCity(City city) {
		try {
			CityCollection cityCollection = new CityCollection();
			BeanUtil.map(city, cityCollection);
			List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
					city.getCity() + " " + ((cityCollection.getState() != null ? cityCollection.getState() : "") + " ")
							+ (cityCollection.getCountry() != null ? cityCollection.getCountry() : ""));

			if (geocodedLocations != null && !geocodedLocations.isEmpty())
				BeanUtil.map(geocodedLocations.get(0), cityCollection);

			cityCollection = cityRepository.save(cityCollection);
			BeanUtil.map(cityCollection, city);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding city", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return city;
	}

	@Override
	@Transactional
	public Boolean activateDeactivateCity(String cityId, boolean activate) {
		try {
			CityCollection cityCollection = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (cityCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid city Id");
			}
			cityCollection.setIsActivated(activate);
			cityRepository.save(cityCollection);
		} catch (BusinessException be) {
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While activating/deactivating city",
						be.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw be;
		} catch (Exception e) {

			try {
				mailService.sendExceptionMail("Backend Business Exception :: While activating/deactivating city",
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return true;
	}

	@Override
	@Transactional
	public List<City> getCities(String state) {
		List<City> response = new ArrayList<City>();
		try {
			if (DPDoctorUtils.allStringsEmpty(state))
				response = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))),
								CityCollection.class, City.class)
						.getMappedResults();
			else
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(new Criteria("state").is(state)),
								Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))),
						CityCollection.class, City.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting cities", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public City getCity(String cityId) {
		City response = new City();
		try {
			CityCollection city = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (city != null) {
				BeanUtil.map(city, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting city", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public LandmarkLocality addLandmaklLocality(LandmarkLocality landmarkLocality) {
		CityCollection cityCollection = null;
		try {
			LandmarkLocalityCollection landmarkLocalityCollection = new LandmarkLocalityCollection();
			BeanUtil.map(landmarkLocality, landmarkLocalityCollection);
			if (landmarkLocality.getCityId() != null) {
				cityCollection = cityRepository.findById(new ObjectId(landmarkLocality.getCityId())).orElse(null);
			}

			List<GeocodedLocation> geocodedLocations = locationServices
					.geocodeLocation((!DPDoctorUtils.anyStringEmpty(landmarkLocalityCollection.getLandmark())
							? landmarkLocalityCollection.getLandmark() + ", "
							: "")
							+ (!DPDoctorUtils.anyStringEmpty(landmarkLocalityCollection.getLocality())
									? landmarkLocalityCollection.getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getCity())
									? cityCollection.getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getState())
									? cityCollection.getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getCountry())
									? cityCollection.getCountry() + ", "
									: ""));

			if (geocodedLocations != null && !geocodedLocations.isEmpty())
				BeanUtil.map(geocodedLocations.get(0), landmarkLocalityCollection);

			landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
			BeanUtil.map(landmarkLocalityCollection, landmarkLocality);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				mailService.sendExceptionMail(
						"Backend Business Exception :: While activating/deactivating landmark locality",
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return landmarkLocality;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Clinic getClinic(String locationId, String role, Boolean active) {
		Clinic response = new Clinic();
		Location location = null;
		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			Criteria criteria = new Criteria().andOperator(new Criteria("id").is(new ObjectId(locationId)),
					new Criteria("isClinic").is(true));
			location = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital")), LocationCollection.class, Location.class)
					.getUniqueMappedResult();
			if (location == null) {
				return null;
			} else {
				if (!DPDoctorUtils.anyStringEmpty(location.getLogoUrl()))
					location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				if (!DPDoctorUtils.anyStringEmpty(location.getLogoThumbnailUrl()))
					location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
				if (location.getImages() != null && !location.getImages().isEmpty()) {
					for (ClinicImage clinicImage : location.getImages()) {
						if (!DPDoctorUtils.anyStringEmpty(clinicImage.getImageUrl()))
							clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
						if (!DPDoctorUtils.anyStringEmpty(clinicImage.getThumbnailUrl()))
							clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
					}
				}
				String address = (!DPDoctorUtils.anyStringEmpty(location.getStreetAddress())
						? location.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getLandmarkDetails())
								? location.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getLocality()) ? location.getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getCity()) ? location.getCity() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getState()) ? location.getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getCountry()) ? location.getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getPostalCode()) ? location.getPostalCode() : "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				location.setClinicAddress(address);
				response.setLocation(location);
				if (location.getHospital() != null) {
					response.setHospital(location.getHospital());
				}

				Collection<ObjectId> userIds = null;
				if (!DPDoctorUtils.anyStringEmpty(role)) {
					List<UserRoleResponse> userRoleResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("role").is(role.toUpperCase()).and("locationId")
									.is(new ObjectId(location.getId())).and("hospitalId").is(location.getHospitalId())),
							Aggregation.lookup("user_role_cl", "_id", "roleId", "userRoleCollections")),
							RoleCollection.class, UserRoleResponse.class).getMappedResults();
					if (userRoleResponse != null && !userRoleResponse.isEmpty()) {
						List<UserRoleCollection> userRoleCollections = userRoleResponse.get(0).getUserRoleCollections();
						userIds = CollectionUtils.collect(userRoleCollections,
								new BeanToPropertyValueTransformer("userId"));
						if (userIds == null || userIds.isEmpty()) {
							return response;
						}
					}
				}

				Criteria criteria2 = new Criteria("locationId").is(new ObjectId(location.getId()));

				Criteria criteriaForActive = new Criteria();
				if (active)
					criteriaForActive.and("user.isActive").is(true);
				Aggregation aggregation = null;
				if (userIds != null && !userIds.isEmpty()) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria2.and("doctorId").in(userIds)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.match(criteriaForActive),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria2),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.match(criteriaForActive),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"));
				}

				List<UserLocationWithDoctorClinicProfile> userWithDoctorProfile = mongoTemplate.aggregate(aggregation,
						DoctorClinicProfileCollection.class, UserLocationWithDoctorClinicProfile.class)
						.getMappedResults();

				for (Iterator<UserLocationWithDoctorClinicProfile> iterator = userWithDoctorProfile.iterator(); iterator
						.hasNext();) {
					UserLocationWithDoctorClinicProfile doctorClinicProfileCollection = iterator.next();

					DoctorCollection doctorCollection = doctorClinicProfileCollection.getDoctor();
					UserCollection userCollection = doctorClinicProfileCollection.getUser();
					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {

							BeanUtil.map(userCollection, doctor);
						}
						DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
						BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
						doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId());
						doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId());
						doctorClinicProfile.setIsPidHasDate(location.getIsPidHasDate());
						doctorClinicProfile.setPatientInitial(location.getPatientInitial());
						doctorClinicProfile.setPatientCounter(location.getPatientCounter());
						
						ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
								Fields.field("role", "$role.role"), Fields.field("locationId", "$locationId"),
								Fields.field("hospitalId", "$hospitalId")));

						List<Role> roles = mongoTemplate
								.aggregate(
										Aggregation
												.newAggregation(
														Aggregation.match(new Criteria("locationId")
																.is(new ObjectId(
																		doctorClinicProfileCollection.getLocationId()))
																.and("userId")
																.is(new ObjectId(
																		doctorClinicProfileCollection.getDoctorId()))),
														Aggregation.lookup("role_cl", "roleId", "_id", "role"),
														Aggregation.unwind("role"), projectList),
										UserRoleCollection.class, Role.class)
								.getMappedResults();
						if (!roles.isEmpty() && roles != null) {
							doctorClinicProfile.setRoles(roles);
						}
						doctor.setDoctorClinicProfile(doctorClinicProfile);

						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("superSpeciality"));
							doctor.setSpecialities(specialities);
						}
						if (!DPDoctorUtils.anyStringEmpty(doctor.getImageUrl()))
							doctor.setImageUrl(getFinalImageURL(doctor.getImageUrl()));
						if (!DPDoctorUtils.anyStringEmpty(doctor.getThumbnailUrl()))
							doctor.setThumbnailUrl(getFinalImageURL(doctor.getThumbnailUrl()));
						if (!DPDoctorUtils.anyStringEmpty(doctor.getCoverImageUrl()))
							doctor.setCoverImageUrl(getFinalImageURL(doctor.getCoverImageUrl()));
						if (!DPDoctorUtils.anyStringEmpty(doctor.getCoverThumbnailImageUrl()))
							doctor.setCoverThumbnailImageUrl(getFinalImageURL(doctor.getCoverThumbnailImageUrl()));
						doctors.add(doctor);
					}
				}
			}
			response.setDoctors(doctors);
			response.setId(locationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment updateAppointment(final AppointmentRequest request, Boolean updateVisit,
			Boolean isStatusChange) {
		Appointment response = null;
		try {
			AppointmentLookupResponse appointmentLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("appointmentId").is(request.getAppointmentId())),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location")),
					AppointmentCollection.class, AppointmentLookupResponse.class).getUniqueMappedResult();
			if (appointmentLookupResponse != null) {
				AppointmentCollection appointmentCollection = new AppointmentCollection();
				BeanUtil.map(appointmentLookupResponse, appointmentCollection);
				PatientCard patientCard = null;
				List<PatientCard> patientCards = null;
				if (!DPDoctorUtils.allStringsEmpty(request.getPatientId())) {
					patientCards = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
											.and("locationId").is(new ObjectId(request.getLocationId()))
											.and("hospitalId").is(new ObjectId(request.getHospitalId()))),
									Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
							PatientCollection.class, PatientCard.class).getMappedResults();
					if (patientCards != null && !patientCards.isEmpty())
						patientCard = patientCards.get(0);
					appointmentCollection.setLocalPatientName(patientCard.getLocalPatientName());
				} else {
					appointmentCollection.setLocalPatientName(request.getLocalPatientName());
				}

				final String doctorName = appointmentLookupResponse.getDoctor().getTitle() + " "
						+ appointmentLookupResponse.getDoctor().getFirstName();

				if (!isStatusChange) {
					AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
					BeanUtil.map(appointmentLookupResponse, appointmentWorkFlowCollection);
					appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

					appointmentCollection.setState(request.getState());

					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						if (request.getCancelledBy() != null) {
							if (request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType())) {
								appointmentCollection.setCancelledBy(appointmentLookupResponse.getDoctor().getTitle()
										+ " " + appointmentLookupResponse.getDoctor().getFirstName());
								appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.DOCTOR.getType());
							} else {
								appointmentCollection.setCancelledBy(patientCard.getLocalPatientName());
								appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.PATIENT.getType());
							}
						}
						AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
								.findByAppointmentId(request.getAppointmentId());
						if (bookedSlotCollection != null)
							appointmentBookedSlotRepository.delete(bookedSlotCollection);
					} else {
						if (request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())) {
							appointmentCollection.setFromDate(request.getFromDate());
							appointmentCollection.setToDate(request.getToDate());
							appointmentCollection.setTime(request.getTime());
							appointmentCollection.setIsRescheduled(true);
							appointmentCollection.setState(AppointmentState.CONFIRM);
							AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
									.findByAppointmentId(request.getAppointmentId());
							if (bookedSlotCollection != null) {
								bookedSlotCollection.setDoctorId(new ObjectId(request.getDoctorId()));
								bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
								bookedSlotCollection.setToDate(appointmentCollection.getToDate());
								bookedSlotCollection.setTime(request.getTime());
								bookedSlotCollection.setUpdatedTime(new Date());
								appointmentBookedSlotRepository.save(bookedSlotCollection);
							}
						}

						if (!request.getDoctorId().equalsIgnoreCase(appointmentLookupResponse.getDoctorId())) {
							appointmentCollection.setDoctorId(new ObjectId(request.getDoctorId()));
							User drCollection = mongoTemplate.aggregate(
									Aggregation.newAggregation(Aggregation
											.match(new Criteria("id").is(appointmentCollection.getDoctorId()))),
									UserCollection.class, User.class).getUniqueMappedResult();
							appointmentLookupResponse.setDoctor(drCollection);
						}
					}

					DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(appointmentCollection.getDoctorId(),
									appointmentCollection.getLocationId());

					appointmentCollection.setCategory(request.getCategory());
					appointmentCollection.setBranch(request.getBranch());
					appointmentCollection.setExplanation(request.getExplanation());
					appointmentCollection.setNotifyDoctorByEmail(request.getNotifyDoctorByEmail());
					appointmentCollection.setNotifyDoctorBySms(request.getNotifyDoctorBySms());
					appointmentCollection.setNotifyPatientByEmail(request.getNotifyPatientByEmail());
					appointmentCollection.setNotifyPatientBySms(request.getNotifyPatientByEmail());
					appointmentCollection.setUpdatedTime(new Date());
					
					if(request.getTreatmentFields() != null && !request.getTreatmentFields().isEmpty()) {
						List<FieldsCollection> fieldsCollections = new ArrayList<FieldsCollection>();
						for(com.dpdocter.beans.Fields fields : request.getTreatmentFields()) {
							FieldsCollection fieldsCollection = new FieldsCollection();
							BeanUtil.map(fields, fieldsCollection);
							fieldsCollections.add(fieldsCollection);
						}
						appointmentCollection.setTreatmentFields(fieldsCollections);
					}
					appointmentCollection = appointmentRepository.save(appointmentCollection);

					if (updateVisit && !DPDoctorUtils.anyStringEmpty(appointmentCollection.getVisitId())) {
						if (appointmentCollection.getState().getState().equals("CANCEL"))
							patientVisitService.updateAppointmentTime(appointmentCollection.getVisitId(), null, null,
									null);
						else
							patientVisitService.updateAppointmentTime(appointmentCollection.getVisitId(),
									appointmentCollection.getAppointmentId(), appointmentCollection.getTime(),
									appointmentCollection.getFromDate());
					}
					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
					String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
							appointmentCollection.getTime().getFromTime() % 60);
					SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
					SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
					if (clinicProfileCollection != null) {
						sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					} else {
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					}

					Date _24HourDt = _24HourSDF.parse(_24HourTime);

					final String patientName = (patientCard != null && patientCard.getLocalPatientName() != null)
							? patientCard.getLocalPatientName().split(" ")[0]
							: (request.getLocalPatientName() != null ? request.getLocalPatientName().split(" ")[0]
									: "");
					final String appointmentId = appointmentCollection.getAppointmentId();
					final String dateTime = _12HourSDF.format(_24HourDt) + ", "
							+ sdf.format(appointmentCollection.getFromDate());
					final String branch = (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getBranch())) ? appointmentLookupResponse.getBranch() : "";
					final String clinicName = appointmentLookupResponse.getLocation().getLocationName();
					final String clinicContactNum = appointmentLookupResponse.getLocation().getClinicNumber() != null
							? appointmentLookupResponse.getLocation().getClinicNumber()
							: "";

					// sendSMS after appointment is saved
					final String id = appointmentCollection.getId().toString(),
							patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
							patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
							doctorEmailAddress = appointmentLookupResponse.getDoctor().getEmailAddress(),
							doctorMobileNumber = appointmentLookupResponse.getDoctor().getMobileNumber();
					final DoctorFacility facility = (clinicProfileCollection != null)
							? clinicProfileCollection.getFacility()
							: null;

					Executors.newSingleThreadExecutor().execute(new Runnable() {
						@Override
						public void run() {
							try {
								sendAppointmentEmailSmsNotification(false, request, id, appointmentId, doctorName,
										patientName, dateTime, clinicName, clinicContactNum, patientEmailAddress,
										patientMobileNumber, doctorEmailAddress, doctorMobileNumber, facility, branch);
							} catch (MessagingException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (request.getStatus() != null) {

					appointmentCollection.setCheckedInAt(request.getCheckedInAt());
					appointmentCollection.setEngagedAt(request.getEngagedAt());
					appointmentCollection.setCheckedOutAt(request.getCheckedOutAt());

					if (request.getStatus().name().equalsIgnoreCase(QueueStatus.SCHEDULED.name())) {
						appointmentCollection.setCheckedInAt(0);
						appointmentCollection.setEngagedAt(0);
						appointmentCollection.setWaitedFor(0);
						appointmentCollection.setCheckedOutAt(0);
						appointmentCollection.setEngagedFor(0);
					} else if (request.getStatus().name().equalsIgnoreCase(QueueStatus.ENGAGED.name())) {
						appointmentCollection.setWaitedFor(
								appointmentCollection.getEngagedAt() - appointmentCollection.getCheckedInAt());
					} else if (request.getStatus().name().equalsIgnoreCase(QueueStatus.CHECKED_OUT.name())) {
						appointmentCollection.setEngagedFor(
								appointmentCollection.getCheckedOutAt() - appointmentCollection.getEngagedAt());
					}
					appointmentCollection.setStatus(request.getStatus());
					appointmentCollection.setUpdatedTime(new Date());
					appointmentRepository.save(appointmentCollection);
				}

				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
				if (patientCard != null) {
					patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
					patientCard.getUser().setLocationId(patientCard.getLocationId());
					patientCard.getUser().setHospitalId(patientCard.getHospitalId());
					BeanUtil.map(patientCard.getUser(), patientCard);
					patientCard.setUserId(patientCard.getUserId());
					patientCard.setId(patientCard.getUserId());
					patientCard.setColorCode(patientCard.getUser().getColorCode());
					patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
					patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
				} else {
					patientCard = new PatientCard();
					patientCard.setLocalPatientName(request.getLocalPatientName());
				}
				response.setPatient(patientCard);

				response.setDoctorName(doctorName);
				if (appointmentLookupResponse.getLocation() != null) {
					response.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
					response.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

					String address = (!DPDoctorUtils
							.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
									? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
									: "")
							+ (!DPDoctorUtils
									.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
											? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
											: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
									? appointmentLookupResponse.getLocation().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
									? appointmentLookupResponse.getLocation().getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
									? appointmentLookupResponse.getLocation().getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
									? appointmentLookupResponse.getLocation().getCountry() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
									? appointmentLookupResponse.getLocation().getPostalCode()
									: "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					response.setClinicAddress(address);
					response.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
					response.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
				}
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationId(new ObjectId(request.getLocationId()));
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
							"Appointment updated.", ComponentType.APPOINTMENT_REFRESH.getType(), null, null);
				}
			} else {
				logger.error(incorrectAppointmentId);
				throw new BusinessException(ServiceError.InvalidInput, incorrectAppointmentId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment addAppointment(final AppointmentRequest request, Boolean isFormattedResponseRequired) {
		Appointment response = null;
		DoctorClinicProfileCollection clinicProfileCollection = null;
		try {

			ObjectId doctorId = new ObjectId(request.getDoctorId()), locationId = new ObjectId(request.getLocationId()),
					hospitalId = new ObjectId(request.getHospitalId()), patientId = null;

			patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);

			UserCollection userCollection = userRepository.findById(doctorId).orElse(null);
			LocationCollection locationCollection = locationRepository.findById(locationId).orElse(null);
			PatientCard patientCard = null;
			List<PatientCard> patientCards = null;

			if (patientId != null) {
				patientCards = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("userId").is(patientId).and("locationId").is(locationId)
										.and("hospitalId").is(hospitalId)),
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
						PatientCollection.class, PatientCard.class).getMappedResults();
				if (patientCards != null && !patientCards.isEmpty())
					patientCard = patientCards.get(0);
				request.setLocalPatientName(patientCard.getLocalPatientName());
			}
			AppointmentCollection appointmentCollection = null;

			if (request.getCreatedBy().equals(AppointmentCreatedBy.PATIENT)) {
				List<AppointmentCollection> appointmentCollections = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(
												Aggregation
														.match(new Criteria("locationId")
																.is(new ObjectId(request.getLocationId()))
																.andOperator(
																		new Criteria().orOperator(
																				new Criteria("doctorId")
																						.is(new ObjectId(
																								request.getDoctorId())),
																				new Criteria("doctorIds")
																						.is(new ObjectId(
																								request.getDoctorId()))
																						.and("isCalenderBlocked")
																						.is(true)),
																		new Criteria().orOperator(
																				new Criteria("time.fromTime")
																						.lte(request.getTime()
																								.getFromTime())
																						.and("time.toTime")
																						.gt(request.getTime()
																								.getToTime()),
																				new Criteria("time.fromTime")
																						.lt(request.getTime()
																								.getFromTime())
																						.and("time.toTime")
																						.gte(request.getTime()
																								.getToTime())))
																.and("fromDate").is(request.getFromDate()).and("toDate")
																.is(request.getToDate()).and("state")
																.ne(AppointmentState.CANCEL.getState()))),

								AppointmentCollection.class, AppointmentCollection.class)
						.getMappedResults();
				if (appointmentCollections != null && !appointmentCollections.isEmpty()) {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			}

			if (userCollection != null && locationCollection != null) {

				clinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(doctorId, locationId);

				appointmentCollection = new AppointmentCollection();
				BeanUtil.map(request, appointmentCollection);
				appointmentCollection.setCreatedTime(new Date());
				appointmentCollection
						.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());

				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

				String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
						appointmentCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				if (clinicProfileCollection != null) {
					sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				} else {
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				}

				Date _24HourDt = _24HourSDF.parse(_24HourTime);

				final String patientName = (patientCard != null && patientCard.getLocalPatientName() != null)
						? patientCard.getLocalPatientName().split(" ")[0]
						: (request.getLocalPatientName() != null ? request.getLocalPatientName().split(" ")[0] : "");
				final String appointmentId = appointmentCollection.getAppointmentId();
				final String dateTime = _12HourSDF.format(_24HourDt) + ", "
						+ sdf.format(appointmentCollection.getFromDate());
				final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();
				final String clinicName = locationCollection.getLocationName(),
						clinicContactNum = locationCollection.getClinicNumber() != null
								? locationCollection.getClinicNumber()
								: "";
				final String branch = (!DPDoctorUtils.anyStringEmpty(request.getBranch())) ? request.getBranch() : "";

				if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
					appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				} else {
					appointmentCollection.setIsCreatedByPatient(true);
					if (patientCard != null)
						appointmentCollection.setCreatedBy(patientCard.getLocalPatientName());
					else
						appointmentCollection.setCreatedBy(request.getLocalPatientName());

					if (clinicProfileCollection != null && clinicProfileCollection.getFacility() != null
							&& (clinicProfileCollection.getFacility().getType()
									.equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
						appointmentCollection.setState(AppointmentState.CONFIRM);
					} else {
						appointmentCollection.setState(AppointmentState.NEW);
					}

					if (patientId != null)
						userFavouriteService.addRemoveFavourites(request.getPatientId(), request.getDoctorId(),
								Resource.DOCTOR.getType(), request.getLocationId(), false);
				}
				appointmentCollection = appointmentRepository.save(appointmentCollection);

				AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
				BeanUtil.map(appointmentCollection, bookedSlotCollection);
				bookedSlotCollection.setDoctorId(appointmentCollection.getDoctorId());
				bookedSlotCollection.setLocationId(appointmentCollection.getLocationId());
				bookedSlotCollection.setHospitalId(appointmentCollection.getHospitalId());
				bookedSlotCollection.setId(null);
				appointmentBookedSlotRepository.save(bookedSlotCollection);

				// sendSMS after appointment is saved

				final String id = appointmentCollection.getId().toString(),
						patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
						patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
						doctorEmailAddress = userCollection.getEmailAddress(),
						doctorMobileNumber = userCollection.getMobileNumber();
				final DoctorFacility facility = (clinicProfileCollection != null)
						? clinicProfileCollection.getFacility()
						: null;

				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							sendAppointmentEmailSmsNotification(true, request, id, appointmentId, doctorName,
									patientName, dateTime, clinicName, clinicContactNum, patientEmailAddress,
									patientMobileNumber, doctorEmailAddress, doctorMobileNumber, facility, branch);
						} catch (MessagingException e) {
							e.printStackTrace();
						}

					}
				});

				if (appointmentCollection != null) {
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);

					if (isFormattedResponseRequired) {
						if (patientCard != null) {
							patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
							patientCard.getUser().setLocationId(patientCard.getLocationId());
							patientCard.getUser().setHospitalId(patientCard.getHospitalId());
							BeanUtil.map(patientCard.getUser(), patientCard);
							patientCard.setUserId(patientCard.getUserId());
							patientCard.setId(patientCard.getUserId());
							patientCard.setColorCode(patientCard.getUser().getColorCode());
							patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
							patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
						} else {
							patientCard = new PatientCard();
							patientCard.setLocalPatientName(request.getLocalPatientName());
						}
						response.setPatient(patientCard);
						if (userCollection != null)
							response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
						if (locationCollection != null) {
							response.setLocationName(locationCollection.getLocationName());
							response.setClinicNumber(locationCollection.getClinicNumber());

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

							response.setClinicAddress(address);
							response.setLatitude(locationCollection.getLatitude());
							response.setLongitude(locationCollection.getLongitude());
						}
					}
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByLocationId(locationId);
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
								"New appointment created.", ComponentType.APPOINTMENT_REFRESH.getType(), null, null);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private ObjectId registerPatientIfNotRegistered(AppointmentRequest request, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId) {
		ObjectId patientId = null;
		if (request.getPatientId() == null || request.getPatientId().isEmpty()) {

			if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput, "Patient not selected");
			}
			PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
			patientRegistrationRequest.setFirstName(request.getLocalPatientName());
			patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
			patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
			patientRegistrationRequest.setDoctorId(request.getDoctorId());
			patientRegistrationRequest.setLocationId(request.getLocationId());
			patientRegistrationRequest.setHospitalId(request.getHospitalId());
			patientRegistrationRequest.setGender(request.getGender());
			patientRegistrationRequest.setDob(request.getDob());
			patientRegistrationRequest.setAge(request.getAge());
			patientRegistrationRequest.setPNUM(request.getPNUM());
			RegisteredPatientDetails patientDetails = null;
			patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
			if (patientDetails != null) {
				request.setPatientId(patientDetails.getUserId());
			}
			transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			patientId = new ObjectId(request.getPatientId());
		} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

			patientId = new ObjectId(request.getPatientId());
			PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(patientId, locationId,
					hospitalId);
			if (patient == null) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
				patientRegistrationRequest.setFirstName(request.getLocalPatientName());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				patientRegistrationRequest.setGender(request.getGender());
				patientRegistrationRequest.setDob(request.getDob());
				patientRegistrationRequest.setAge(request.getAge());
				patientRegistrationRequest.setPNUM(request.getPNUM());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest, null);
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else {
				List<ObjectId> consultantDoctorIds = patient.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(doctorId))
					consultantDoctorIds.add(doctorId);
				patient.setConsultantDoctorIds(consultantDoctorIds);
				patient.setUpdatedTime(new Date());
				patientRepository.save(patient);
			}
		}

		return patientId;
	}

	private void sendAppointmentEmailSmsNotification(Boolean isAddAppointment, AppointmentRequest request,
			String appointmentCollectionId, String appointmentId, String doctorName, String patientName,
			String dateTime, String clinicName, String clinicContactNum, String patientEmailAddress,
			String patientMobileNumber, String doctorEmailAddress, String doctorMobileNumber,
			DoctorFacility doctorFacility, String branch) throws MessagingException {

		/*
		 * sendAppointmentEmailSmsNotification(true, request,
		 * appointmentCollection.getId().toString(), appointmentId, doctorName,
		 * patientName, dateTime, clinicName, clinicContactNum,
		 * patientCard.getEmailAddress(), patientCard.getUser().getMobileNumber(),
		 * userCollection.getEmailAddress(), userCollection.getMobileNumber(),
		 * (clinicProfileCollection != null) ? clinicProfileCollection.getFacility() :
		 * null);
		 */

		if (isAddAppointment) {
			if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, dateTime, clinicName,
							"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch);
				if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
					sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName,
							appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch);
				}
				sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), doctorMobileNumber,
						patientName, appointmentCollectionId, appointmentId, dateTime, doctorName, clinicName,
						clinicContactNum, branch);

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& patientEmailAddress != null)
					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
							patientEmailAddress, branch);
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
						&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
					sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch);
				}
				if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch);
			} else {
				if (doctorFacility != null
						&& (doctorFacility.getType().equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
					sendEmail(doctorName, patientName, dateTime, clinicName,
							"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch);
					sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName,
							appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentCollectionId, appointmentId, dateTime, doctorName, clinicName,
							clinicContactNum, branch);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
				} else {
					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR",
							doctorEmailAddress, branch);
					sendMsg(null, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum, branch);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "TENTATIVE_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
					sendPushNotification("CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("TENTATIVE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
				}
			}
		} else {
			if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
				if (request.getCancelledBy().equals(AppointmentCreatedBy.DOCTOR.getType())) {
					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", doctorEmailAddress, branch);

					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
								request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
								clinicContactNum, branch);
					}

					sendPushNotification("CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch);

					if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
							&& patientEmailAddress != null)
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", patientEmailAddress, branch);

					if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
							&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
						sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
					}

					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
				} else {
					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
								request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
								clinicContactNum, branch);
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(),
									"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getPatientId(),
									patientMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum, branch);

						sendPushNotification("CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendPushNotification("CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getPatientId(),
									patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum, branch);
						if (!DPDoctorUtils.anyStringEmpty(patientEmailAddress))
							sendEmail(doctorName, patientName, dateTime, clinicName,
									"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", patientEmailAddress, branch);
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch);
					}
				}
			} else {
				if (request.getCreatedBy().getType().equals(AppointmentCreatedBy.DOCTOR.getType())) {
					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, branch);

					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
							sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum, branch);
						else
							sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum, branch);
					}

					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
					else
						sendPushNotification("RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum, branch);
				}
				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& !DPDoctorUtils.allStringsEmpty(patientEmailAddress)) {

					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
							patientEmailAddress, branch);
				}
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum, branch);
						else if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum, branch);
				}

				if (request.getState().getState().equals(AppointmentState.CONFIRM.getState())
						&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch);
				else if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendPushNotification("RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum, branch);
			}
		}
	}

	private void sendPushNotification(String type, String userId, String mobileNumber, String patientName,
			String appointmentCollectionId, String appointmentId, String dateTime, String doctorName, String clinicName,
			String clinicContactNum, String branch) {

		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		if (DPDoctorUtils.anyStringEmpty(dateTime))
			dateTime = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";

		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + (branch != "" ? ", " + branch : "")+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + (branch != "" ? ", " + branch : "")+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been sent for confirmation.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName+ (branch != "" ? ", " + branch : "")
					+ " has been cancelled as per your request.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName+ (branch != "" ? ", " + branch : "")
					+ ", has been cancelled by patient.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment for " + dateTime + " with " + doctorName
					+ " has been cancelled as per your request.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been rescheduled @ " + dateTime
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName+ (branch != "" ? ", " + branch : "")
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		default:
			break;
		}
	}

	private void sendEmail(String doctorName, String patientName, String dateTime, String clinicName, String type,
			String emailAddress, String branch) throws MessagingException {
		
		if(!DPDoctorUtils.anyStringEmpty(branch))branch = " "+branch+" ";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentConfirmToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToDoctorByPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentConfirmToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRequestToDoctorByPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentRequestToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByDoctorToDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByPatientToDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByPatient.vm", branch);
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentRescheduleToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRescheduleByDoctorToDoctor.vm", branch);
			mailService.sendEmail(emailAddress, appointmentRescheduleToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		default:
			break;
		}

	}

	private void sendMsg(String formatType, String type, String doctorId, String locationId, String hospitalId,
			String userId, String mobileNumber, String patientName, String appointmentId, String dateTime,
			String doctorName, String clinicName, String clinicContactNum, String branch) {
		SMSFormatCollection smsFormatCollection = null;
		if (formatType != null) {
			smsFormatCollection = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalIdAndType(new ObjectId(doctorId), new ObjectId(locationId),
					new ObjectId(hospitalId), formatType);
		}

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(new ObjectId(doctorId));
		smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
		smsTrackDetail.setLocationId(new ObjectId(locationId));
		smsTrackDetail.setType("APPOINTMENT");
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(new ObjectId(userId));
		SMS sms = new SMS();

		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		if (DPDoctorUtils.anyStringEmpty(dateTime))
			dateTime = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";
		if (smsFormatCollection != null) {
			if (type.equalsIgnoreCase("CONFIRMED_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("TENTATIVE_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR")
					|| type.equalsIgnoreCase("APPOINTMENT_REMINDER_TO_PATIENT")
					|| type.equalsIgnoreCase("RESCHEDULE_APPOINTMENT_TO_PATIENT")) {
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_NAME.getContent())
						|| clinicName == null)
					clinicName = "";
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_CONTACT_NUMBER.getContent())
						|| clinicContactNum == null)
					clinicContactNum = "";
				if (!smsFormatCollection.getContent().contains(SMSContent.BRANCH.getContent())
						|| branch == null)
					branch = "";
			}
		}
		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "") + (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + (branch != "" ? ", " + branch : "") + ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + (branch != "" ? ", " + branch : "")+ ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointmen @ " + dateTime + " with " + doctorName + (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been sent for confirmation. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName+ (branch != "" ? ", " + branch : "")
					+ " has been cancelled as per your request.";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ ". Request you to book again. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName+ (branch != "" ? ", " + branch : "")
					+ ", has been cancelled by patient.";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment for " + dateTime + " with " + doctorName
					+ " has been cancelled as per your request. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ". Download Healthcoco App- "
					+ patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")+ (branch != "" ? ", " + branch : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been rescheduled @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName+ (branch != "" ? ", " + branch : "")
					+ ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		default:
			break;
		}

		sms.setSmsText(text);

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(mobileNumber);
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		sMSServices.sendSMS(smsTrackDetail, true);
	}

	@Override
	@Transactional
	public Response<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb) {
		Response<Appointment> response = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("updatedTime")
					.gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId)
					doctorObjectIds.add(new ObjectId(id));
				criteria.and("doctorId").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status.toUpperCase()).and("state").ne(AppointmentState.CANCEL.getState());

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));
			List<AppointmentLookupResponse> appointmentLookupResponses = null;

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				if (status.equalsIgnoreCase(QueueStatus.WAITING.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedInAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.ENGAGED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "engagedAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.CHECKED_OUT.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedOutAt"));
				}
			} else if(!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("startTime")){
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "time.fromTime"));
			}
			else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}
			

			if (isWeb)
				response = getAppointmentsForWeb(criteria, sortOperation, page, size, appointmentLookupResponses);
			else {
				Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
				if(count > 0) {
					response = new Response<Appointment>();
					if (size > 0) {
						appointmentLookupResponses = mongoTemplate.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"),
												Aggregation.lookup("location_cl", "locationId", "_id", "location"),
												Aggregation.unwind("location"),

												Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
												new CustomAggregationOperation(new Document(
														"$unwind",
														new BasicDBObject(
																"path", "$patientCard").append("preserveNullAndEmptyArrays",
																		true))),
												new CustomAggregationOperation(
														new Document("$redact",
																new BasicDBObject("$cond",
																		new BasicDBObject("if", new BasicDBObject("$eq",
																				Arrays.asList("$patientCard.locationId",
																						"$locationId")))
																								.append("then", "$$KEEP")
																								.append("else",
																										"$$PRUNE")))),

												Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
												Aggregation.unwind("patientCard.user"), sortOperation,
												Aggregation.skip((long)(page) * size), Aggregation.limit(size))
										.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
								AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
					} else {
						appointmentLookupResponses = mongoTemplate.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"),
												Aggregation.lookup("location_cl", "locationId", "_id", "location"),
												Aggregation.unwind("location"),
												Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
												new CustomAggregationOperation(new Document(
														"$unwind",
														new BasicDBObject(
																"path", "$patientCard").append("preserveNullAndEmptyArrays",
																		true))),
												new CustomAggregationOperation(
														new Document("$redact",
																new BasicDBObject("$cond",
																		new BasicDBObject("if", new BasicDBObject("$eq",
																				Arrays.asList("$patientCard.locationId",
																						"$locationId")))
																								.append("then", "$$KEEP")
																								.append("else",
																										"$$PRUNE")))),

												Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
												Aggregation.unwind("patientCard.user"), sortOperation)
										.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
								AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
					}

					if (appointmentLookupResponses != null && !appointmentLookupResponses.isEmpty()) {
						List<Appointment> appointments = new ArrayList<Appointment>();

						for (AppointmentLookupResponse collection : appointmentLookupResponses) {
							Appointment appointment = new Appointment();
							PatientCard patientCard = null;
							if (collection.getType().getType().equals(AppointmentType.APPOINTMENT.getType())) {
								patientCard = collection.getPatientCard();
								if (patientCard != null) {
									patientCard.setBackendPatientId(patientCard.getId());
									patientCard.setId(patientCard.getUserId());

									if (patientCard.getUser() != null) {
										patientCard.setColorCode(patientCard.getUser().getColorCode());
										patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
									}
									patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
									patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));

								}
							}
							BeanUtil.map(collection, appointment);
							appointment.setPatient(patientCard);

							// -----------------------------------------

							if (isRegisteredPatientRequired == true && patientCard != null) {
								RegisteredPatientDetails registeredPatientDetail = new RegisteredPatientDetails();
								if (patientCard.getUser() != null) {
									BeanUtil.map(patientCard.getUser(), registeredPatientDetail);
									if (patientCard.getUser().getId() != null) {
										registeredPatientDetail.setUserId(patientCard.getUser().getId().toString());
									}
								}

								Patient patient = new Patient();
								BeanUtil.map(patientCard, patient);
								patient.setPatientId(patientCard.getUser().getId().toString());
								ObjectId referredBy = null;
								if (patientCard.getReferredBy() != null) {
									referredBy = new ObjectId(patientCard.getReferredBy());
								}

								patientCard.setReferredBy(null);
								BeanUtil.map(patientCard, registeredPatientDetail);

								registeredPatientDetail.setPatient(patient);
								registeredPatientDetail.setAddress(patientCard.getAddress());

								registeredPatientDetail.setDoctorId(patientCard.getDoctorId().toString());
								registeredPatientDetail.setLocationId(patientCard.getLocationId().toString());
								registeredPatientDetail.setHospitalId(patientCard.getHospitalId().toString());
								registeredPatientDetail.setCreatedTime(patientCard.getCreatedTime());
								registeredPatientDetail.setPID(patientCard.getPID());
								registeredPatientDetail.setMobileNumber(patientCard.getUser().getMobileNumber());

								if (patientCard.getDob() != null) {
									registeredPatientDetail.setDob(patientCard.getDob());
								}

								Reference reference = new Reference();
								if (referredBy != null) {
									ReferencesCollection referencesCollection = referenceRepository.findById(referredBy).orElse(null);
									if (referencesCollection != null)
										BeanUtil.map(referencesCollection, reference);
								}
								registeredPatientDetail.setReferredBy(reference);
								registeredPatientDetail.setColorCode(patientCard.getUser().getColorCode());
								appointment.setRegisteredPatientDetails(registeredPatientDetail);
								appointment.setPatient(null);
							}

							// -----------------------------------------

							if (collection.getDoctor() != null) {
								appointment.setDoctorName(
										collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
							}
							if (collection.getLocation() != null) {
								appointment.setLocationName(collection.getLocation().getLocationName());
								appointment.setClinicNumber(collection.getLocation().getClinicNumber());

								String address = (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getStreetAddress())
										? collection.getLocation().getStreetAddress() + ", "
										: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
												? collection.getLocation().getLandmarkDetails() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
												? collection.getLocation().getLocality() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
												? collection.getLocation().getCity() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
												? collection.getLocation().getState() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
												? collection.getLocation().getCountry() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
												? collection.getLocation().getPostalCode()
												: "");

								if (address.charAt(address.length() - 2) == ',') {
									address = address.substring(0, address.length() - 2);
								}

								appointment.setClinicAddress(address);
								appointment.setLatitude(collection.getLocation().getLatitude());
								appointment.setLongitude(collection.getLocation().getLongitude());
							}
							appointments.add(appointment);
						}
						response.setCount(count);
						response.setDataList(appointments);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private Response<Appointment> getAppointmentsForWeb(Criteria criteria, SortOperation sortOperation, int page, int size,
			List<AppointmentLookupResponse> appointmentLookupResponses) {

		Response<Appointment> response = null;
		Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
		if(count > 0) {
			response = new Response<Appointment>();
			CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("doctorId", "$doctorId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("patientId", "$patientId").append("time", "$time")
							.append("state", "$state").append("isRescheduled", "$isRescheduled")
							.append("fromDate", "$fromDate").append("toDate", "$toDate")
							.append("appointmentId", "$appointmentId").append("subject", "$subject")
							.append("explanation", "$explanation").append("type", "$type")
							.append("isCalenderBlocked", "$isCalenderBlocked").append("treatmentFields", "$treatmentFields")
							.append("isFeedbackAvailable", "$isFeedbackAvailable").append("isAllDayEvent", "$isAllDayEvent")
							.append("doctorName",
									new BasicDBObject("$concat", Arrays.asList("$doctor.title", " ", "$doctor.firstName")))
							.append("cancelledBy", "$cancelledBy").append("notifyPatientBySms", "$notifyPatientBySms")
							.append("notifyPatientByEmail", "$notifyPatientByEmail")
							.append("notifyDoctorBySms", "$notifyDoctorBySms")
							.append("notifyDoctorByEmail", "$notifyDoctorByEmail").append("visitId", "$visitId")
							.append("status", "$status").append("waitedFor", "$waitedFor")
							.append("engagedFor", "$engagedFor").append("engagedAt", "$engagedAt")
							.append("checkedInAt", "$checkedInAt").append("checkedOutAt", "$checkedOutAt")
							.append("count", "$count").append("category", "$category")
							.append("branch", "$branch")
							.append("cancelledByProfile", "$cancelledByProfile")
							.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
							.append("isCreatedByPatient", "$isCreatedByPatient")
							.append("patient._id", "$patientCard.userId").append("patient.userId", "$patientCard.userId")
							.append("patient.localPatientName", "$patientCard.localPatientName")
							.append("patient.PID", "$patientCard.PID").append("patient.PNUM", "$patientCard.PNUM")
							.append("patient.imageUrl", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("eq", Arrays.asList("$patientCard.imageUrl", null)))
													.append("then",
															new BasicDBObject("$concat",
																	Arrays.asList(imagePath, "$patientCard.imageUrl")))
													.append("else", null)))
							.append("patient.thumbnailUrl", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("eq", Arrays.asList("$patientCard.thumbnailUrl", null)))
													.append("then",
															new BasicDBObject("$concat",
																	Arrays.asList(imagePath, "$patientCard.thumbnailUrl")))
													.append("else", null)))
							.append("patient.mobileNumber", "$patientUser.mobileNumber")
							.append("patient.colorCode", "$patientUser.colorCode")));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("time", new BasicDBObject("$first", "$time"))
							.append("state", new BasicDBObject("$first", "$state"))
							.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
							.append("subject", new BasicDBObject("$first", "$subject"))
							.append("explanation", new BasicDBObject("$first", "$explanation"))
							.append("type", new BasicDBObject("$first", "$type"))
							.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
							.append("treatmentFields", new BasicDBObject("$first", "$treatmentFields"))
							.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
							.append("isAllDayEvent", new BasicDBObject("$first", "$isAllDayEvent"))
							.append("doctorName", new BasicDBObject("$first", "$doctorName"))
							.append("cancelledBy", new BasicDBObject("$first", "$cancelledBy"))
							.append("notifyPatientBySms", new BasicDBObject("$first", "$notifyPatientBySms"))
							.append("notifyPatientByEmail", new BasicDBObject("$first", "$notifyPatientByEmail"))
							.append("notifyDoctorBySms", new BasicDBObject("$first", "$notifyDoctorBySms"))
							.append("notifyDoctorByEmail", new BasicDBObject("$first", "$notifyDoctorByEmail"))
							.append("visitId", new BasicDBObject("$first", "$visitId"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("waitedFor", new BasicDBObject("$first", "$waitedFor"))
							.append("engagedFor", new BasicDBObject("$first", "$engagedFor"))
							.append("engagedAt", new BasicDBObject("$first", "$engagedAt"))
							.append("checkedInAt", new BasicDBObject("$first", "$checkedInAt"))
							.append("checkedOutAt", new BasicDBObject("$first", "$checkedOutAt"))
							.append("count", new BasicDBObject("$first", "$count"))
							.append("category", new BasicDBObject("$first", "$category"))
							.append("branch", new BasicDBObject("$first", "$branch"))
							.append("cancelledByProfile", new BasicDBObject("$first", "$cancelledByProfile"))
							.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("isCreatedByPatient", new BasicDBObject("$first", "$isCreatedByPatient"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("patient", new BasicDBObject("$first", "$patient"))));

			if (size > 0) {
				response.setDataList(mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"),
												Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
												new CustomAggregationOperation(new Document(
														"$unwind",
														new BasicDBObject(
																"path", "$patientCard").append("preserveNullAndEmptyArrays",
																		true))),
												new CustomAggregationOperation(
														new Document("$redact",
																new BasicDBObject("$cond",
																		new BasicDBObject("if", new BasicDBObject("$eq",
																				Arrays.asList("$patientCard.locationId",
																						"$locationId")))
																								.append("then", "$$KEEP")
																								.append("else",
																										"$$PRUNE")))),

												Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
												Aggregation.unwind("patientUser"), projectOperation, groupOperation,
												sortOperation, Aggregation.skip((long)(page) * size), Aggregation.limit(size))
										.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
								AppointmentCollection.class, Appointment.class)
						.getMappedResults());
			} else {
				response.setDataList(mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"),
												Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
												new CustomAggregationOperation(new Document(
														"$unwind",
														new BasicDBObject(
																"path", "$patientCard").append("preserveNullAndEmptyArrays",
																		true))),
												new CustomAggregationOperation(
														new Document("$redact",
																new BasicDBObject("$cond",
																		new BasicDBObject("if", new BasicDBObject("$eq",
																				Arrays.asList("$patientCard.locationId",
																						"$locationId")))
																								.append("then", "$$KEEP")
																								.append("else",
																										"$$PRUNE")))),

												Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
												Aggregation.unwind("patientUser"), projectOperation, groupOperation,
												sortOperation)
										.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
								AppointmentCollection.class, Appointment.class)
						.getMappedResults());
			}
		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getPatientAppointments(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		Response<Object> response = new Response<Object>();
		List<Appointment> appointments = null;
		List<AppointmentLookupResponse> appointmentLookupResponses = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("updatedTime")
					.gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null)
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}

			long count = mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if (count > 0) {
				response.setData(count);
				if (size > 0) {
					appointmentLookupResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")),
									Aggregation.skip((long)(page) * size), Aggregation.limit(size)),
							AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
				} else {
					appointmentLookupResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime"))),
							AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
				}

				if (appointmentLookupResponses != null) {
					appointments = new ArrayList<Appointment>();
					for (AppointmentLookupResponse collection : appointmentLookupResponses) {
						Appointment appointment = new Appointment();
						BeanUtil.map(collection, appointment);
						if (collection.getDoctor() != null) {
							appointment.setDoctorName(
									collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
						}
						if (collection.getLocation() != null) {
							appointment.setLocationName(collection.getLocation().getLocationName());
							appointment.setClinicNumber(collection.getLocation().getClinicNumber());
							String address = (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getStreetAddress())
									? collection.getLocation().getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
											? collection.getLocation().getLandmarkDetails() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
											? collection.getLocation().getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
											? collection.getLocation().getCity() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
											? collection.getLocation().getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
											? collection.getLocation().getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
											? collection.getLocation().getPostalCode()
											: "");

							if (address.charAt(address.length() - 2) == ',') {
								address = address.substring(0, address.length() - 2);
							}
							appointment.setClinicAddress(address);
							appointment.setLatitude(collection.getLocation().getLatitude());
							appointment.setLongitude(collection.getLocation().getLongitude());
						}
						appointments.add(appointment);
					}
				}
				response.setDataList(appointments);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Lab getLab(String locationId, String patientId, Boolean active) {
		Lab response = new Lab();
		Location location = new Location();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			ObjectId locationObjectId = new ObjectId(locationId);

			Criteria criteria = new Criteria().andOperator(new Criteria("id").is(locationObjectId),
					new Criteria("isLab").is(true));
			location = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital")), LocationCollection.class, Location.class)
					.getUniqueMappedResult();
			if (location == null) {
				return null;
			} else {

				RecommendationsCollection recommendationsCollection = new RecommendationsCollection();
				if (!DPDoctorUtils.anyStringEmpty(patientId)) {
					ObjectId patientObjectId = new ObjectId(patientId);
					recommendationsCollection = recommendationsRepository.findByDoctorIdAndLocationIdAndPatientId(null,
							locationObjectId, patientObjectId);
					if (recommendationsCollection != null)
						location.setIsClinicRecommended(!recommendationsCollection.getDiscarded());

					if (location.getIsLab()) {
						Integer favCount = userResourceFavouriteRepository.findCount(locationObjectId,
								Resource.LAB.getType(), null, patientObjectId, false);
						if (favCount != null && favCount > 0)
							location.setIsFavourite(true);
					}
				}

				location.setClinicAddress(
						(!DPDoctorUtils.anyStringEmpty(location.getStreetAddress()) ? location.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getLandmarkDetails())
										? location.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getLocality())
										? location.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getCity()) ? location.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getState()) ? location.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getCountry())
										? location.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getPostalCode()) ? location.getPostalCode()
										: ""));

				location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
				location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				if (location.getImages() != null && !location.getImages().isEmpty()) {
					for (ClinicImage image : location.getImages()) {
						image.setImageUrl(getFinalImageURL(image.getImageUrl()));
						image.setThumbnailUrl(getFinalImageURL(image.getThumbnailUrl()));

					}
				}
				response.setLocation(location);
				response.setHospital(location.getHospital());
				response.setId(locationId);
				Criteria criteria2 = new Criteria("locationId").is(new ObjectId(location.getId()));

				Criteria criteriaForActive = new Criteria();
				if (active)
					criteriaForActive.and("user.isActive").is(true);

				List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria2),
										Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
										Aggregation.unwind("user"), Aggregation.match(criteriaForActive),
										Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
										Aggregation.unwind("doctor")),

								DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
						.getMappedResults();

				for (Iterator<DoctorClinicProfileLookupResponse> iterator = doctorClinicProfileLookupResponses
						.iterator(); iterator.hasNext();) {
					DoctorClinicProfileLookupResponse doctorClinicProfileCollection = iterator.next();
					DoctorCollection doctorCollection = doctorClinicProfileCollection.getDoctor();
					UserCollection userCollection = doctorClinicProfileCollection.getUser();

					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						if (doctorClinicProfileCollection != null) {
							DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
							BeanUtil.map(location, doctorClinicProfile);
							BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
							doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId().toString());
							doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
							doctor.setDoctorClinicProfile(doctorClinicProfile);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("speciality"));
							doctor.setSpecialities(specialities);
						}
						doctor.setCoverImageUrl(getFinalImageURL(doctor.getCoverImageUrl()));
						doctor.setCoverThumbnailImageUrl(getFinalImageURL(doctor.getCoverImageUrl()));
						doctor.setThumbnailUrl(getFinalImageURL(doctor.getThumbnailUrl()));
						doctor.setImageUrl(getFinalImageURL(doctor.getImageUrl()));

						doctors.add(doctor);
					}
				}
				response.setDoctors(doctors);
				response.setId(locationId);
				response.setNoOfLabTest(
						(int) mongoTemplate.count(
								new Query(new Criteria("locationId").is(new ObjectId(location.getId()))
										.and("hospitalId").is(new ObjectId(location.getHospitalId()))),
								LabTestCollection.class));
				if (response.getNoOfLabTest() != null && response.getNoOfLabTest() > 0) {
					List<LabTest> labTests = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("locationId").is(new ObjectId(location.getId()))
											.and("hospitalId").is(new ObjectId(location.getHospitalId()))),
									Aggregation.lookup("diagnostic_test_cl", "testId", "_id", "test"),
									Aggregation.unwind("test"), Aggregation.limit(5)),
							LabTestCollection.class, LabTest.class).getMappedResults();
					response.setLabTests(labTests);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getCountries() {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("country").first("country").as("country"),
					Aggregation.project("country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "country"));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class,
					City.class);
			response = groupResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("static-access")
	@Override
	@Transactional
	public List<City> getStates(String country) {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("state").first("state").as("state").first("country").as("country"),
					Aggregation.project("state", "country").andExclude("_id"),
					Aggregation.sort(Sort.Direction.ASC, "state"));
			if (!DPDoctorUtils.anyStringEmpty(country))
				aggregation.match(Criteria.where("country").is(country));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class,
					City.class);
			response = groupResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	/*
	 * slots are divided in such a way that we slice available time from 'fromTime
	 * of working hours' to 'start time of booked slots' & not available 'start Time
	 * of booked slotss' to 'end time of booked slots' & assign startTime = end time
	 * of booked slots. After booked slots completed again slicing available time
	 * from 'endTime of last booked slots i.e. now start time' to 'to Time of
	 * working hours
	 */
	@Override
	@Transactional
	public SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date, Boolean isPatient) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		SlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(doctorObjectId,
					locationObjectId);
			if (doctorClinicProfileCollection != null) {

				if (!isPatient) {
					UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdAndLocationId(doctorObjectId,
							locationObjectId);
					if (userRoleCollection != null) {
						RoleCollection roleCollection = roleRepository.findById(userRoleCollection.getId()).orElse(null);
						if (roleCollection != null)
							if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.RECEPTIONIST_NURSE.getRole())
									|| roleCollection.getRole().equalsIgnoreCase("RECEPTIONIST")) {
								throw new BusinessException(ServiceError.NotAuthorized,
										"You are not authorized to have slots.");
							}
					}
				}
				Integer startTime = 0, endTime = 0;
				float slotTime = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				String day = sdf.format(date);
				if (doctorClinicProfileCollection.getWorkingSchedules() != null
						&& doctorClinicProfileCollection.getAppointmentSlot() != null) {
					slotTime = doctorClinicProfileCollection.getAppointmentSlot().getTime();
					response = new SlotDataResponse();
					response.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
					slotResponse = new ArrayList<Slot>();
					List<WorkingHours> workingHours = null;
					for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
						if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
							workingHours = workingSchedule.getWorkingHours();
						}
					}
					if (workingHours != null && !workingHours.isEmpty()) {

						Calendar localCalendar = Calendar
								.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
						localCalendar.setTime(date);
						int dayOfDate = localCalendar.get(Calendar.DATE);
						int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
						int yearOfDate = localCalendar.get(Calendar.YEAR);

						DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
						DateTime end = new DateTime(yearOfDate, monthOfDate, dayOfDate, 23, 59, 59, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
						
						List<AppointmentBookedSlotCollection> bookedSlots = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(new Criteria("locationId").is(locationObjectId)
										.andOperator(
												new Criteria().orOperator(new Criteria("doctorId").is(doctorObjectId).and("type").is(AppointmentType.APPOINTMENT.name()), 
														new Criteria("doctorIds").in(Arrays.asList(doctorObjectId)).and("type").is(AppointmentType.EVENT.name())),										
										new Criteria().orOperator(new Criteria("fromDate").gte(new Date(start.getMillis())).and("toDate").lte(new Date(end.getMillis())),
												new Criteria("fromDate").lte(new Date(start.getMillis())).and("toDate").gte(new Date(start.getMillis())),
																	new Criteria("fromDate").lte(new Date(end.getMillis())).and("toDate").gte(new Date(end.getMillis()))))
										.and("isPatientDiscarded").ne(true)),
										Aggregation.sort(new Sort(Direction.ASC, "time.fromTime"))), 
								AppointmentBookedSlotCollection.class, AppointmentBookedSlotCollection.class).getMappedResults();
								
//								appointmentBookedSlotRepository
//								.findByDoctorLocationId(doctorObjectId, locationObjectId, start, end,
//										new Sort(Direction.ASC, "time.fromTime"));
						int i = 0;
						for (WorkingHours hours : workingHours) {
							startTime = hours.getFromTime();
							endTime = hours.getToTime();

							if (bookedSlots != null && !bookedSlots.isEmpty()) {
								while (i < bookedSlots.size()) {
									AppointmentBookedSlotCollection bookedSlot = bookedSlots.get(i);
									if (endTime > startTime) {
										if (bookedSlot.getTime().getFromTime() >= startTime
												|| bookedSlot.getTime().getToTime() >= endTime) {
											if (!bookedSlot.getFromDate().equals(bookedSlot.getToDate())) {
												if (bookedSlot.getIsAllDayEvent()) {
													if (bookedSlot.getFromDate().equals(date))
														bookedSlot.getTime().setToTime(719);
													if (bookedSlot.getToDate().equals(date))
														bookedSlot.getTime().setFromTime(0);
												}
											}
											List<Slot> slots = DateAndTimeUtility.sliceTime(startTime,
													bookedSlot.getTime().getFromTime(), Math.round(slotTime), true);
											if (slots != null)
												slotResponse.addAll(slots);

											slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFromTime(),
													bookedSlot.getTime().getToTime(), Math.round(slotTime), false);
											if (slots != null)
												slotResponse.addAll(slots);
											startTime = bookedSlot.getTime().getToTime();
											i++;
										} else {
											i++;
											break;
										}
									} else {
										i++;
										break;
									}
								}
							}

							if (endTime > startTime) {
								List<Slot> slots = DateAndTimeUtility.sliceTime(startTime, endTime,
										Math.round(slotTime), true);
								if (slots != null)
									slotResponse.addAll(slots);
							}
						}

						if (checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
								doctorClinicProfileCollection.getTimeZone()))
							for (Slot slot : slotResponse) {
								if (slot.getMinutesOfDay() < getMinutesOfDay(date)) {
									slot.setIsAvailable(false);
									slotResponse.set(slotResponse.indexOf(slot), slot);
								}
							}
					}
					response.setSlots(slotResponse);
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Event addEvent(final EventRequest request, Boolean forAllDoctors) {
		Event response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);

			AppointmentCollection appointmentCollection = null;

			List<ObjectId> doctorIds = new ArrayList<ObjectId>();
			if(forAllDoctors) {
				List<DoctorClinicProfileCollection> doctors = doctorClinicProfileRepository.findByLocationIdAndIsActivate(locationObjectId, true);
				doctorIds = (List<ObjectId>) CollectionUtils.collect(doctors, new BeanToPropertyValueTransformer("doctorId"));
			}
			else if (request.getDoctorIds() != null && !request.getDoctorIds().isEmpty()) {
				for (String doctorId : request.getDoctorIds())
					doctorIds.add(new ObjectId(doctorId));
			}
			
			Aggregation aggregation = Aggregation
					.newAggregation(
							Aggregation
									.match(new Criteria("locationId")
											.is(new ObjectId(request.getLocationId()))
											.andOperator(
													new Criteria().orOperator(
															new Criteria("doctorId")
																	.is(new ObjectId(
																			request.getDoctorId())),
															new Criteria("doctorIds")
																	.in(doctorIds)
																	.and("isCalenderBlocked")
																	.is(true)),
													new Criteria().orOperator(
															new Criteria("time.fromTime")															
																	.lte(request.getTime()
																			.getFromTime())
																	.and("time.toTime")
																	.gte(request.getTime()
																			.getToTime()),
															new Criteria("time.fromTime")
																	.lte(request.getTime()
																			.getFromTime())
																	.and("time.toTime")
																	.gte(request.getTime()
																			.getFromTime()),
																	new Criteria("time.fromTime")
																	.lte(request.getTime()
																			.getToTime())
																	.and("time.toTime")
																	.gte(request.getTime()
																			.getToTime())		),
//													new Criteria().orOperator(
//															new Criteria("time.fromTime")
//																	.lte(request.getTime()
//																			.getFromTime())
//																	.and("time.toTime")
//																	.gte(request.getTime()
//																			.getToTime()),
//															new Criteria("time.fromTime")
//																	.lte(request.getTime()
//																			.getFromTime())
//																	.and("time.toTime")
//																	.gte(request.getTime()
//																			.getToTime())),
													new Criteria().orOperator(new Criteria("fromDate").gte(request.getFromDate()).and("toDate").lte(request.getToDate()),
															new Criteria("fromDate").lte(request.getFromDate()).and("toDate").gte(request.getFromDate()),
																				new Criteria("fromDate").lte(request.getToDate()).and("toDate").gte(request.getToDate()))
													)
//											.and("fromDate").gte(request.getFromDate())
//											.and("toDate").lte(request.getToDate())
											.and("state").ne(AppointmentState.CANCEL.getState())));
			
			List<AppointmentCollection> appointmentCollections = mongoTemplate
					.aggregate(aggregation,
//							Aggregation
//									.newAggregation(
//											Aggregation
//													.match(new Criteria(
//															"locationId")
//																	.is(new ObjectId(request.getLocationId()))
//																	.orOperator(
//																			new Criteria("doctorId").in(doctorIds)
//																					.and("time.fromTime")
//																					.lte(request.getTime()
//																							.getFromTime())
//																					.and("time.toTime")
//																					.gt(request.getTime().getToTime()),
//																			new Criteria("doctorIds").is(doctorIds)
//																					.and("isCalenderBlocked").is(true)
//																					.and("time.fromTime")
//																					.lte(request.getTime()
//																							.getFromTime())
//																					.and("time.toTime")
//																					.gt(request.getTime().getToTime()),
//																			new Criteria("doctorId")
//																					.in(doctorIds).and("time.fromTime")
//																					.lt(request.getTime().getFromTime())
//																					.and("time.toTime")
//																					.gte(request.getTime().getToTime()),
//																			new Criteria("doctorIds").is(doctorIds)
//																					.and("isCalenderBlocked").is(true)
//																					.and("time.fromTime")
//																					.lt(request.getTime().getFromTime())
//																					.and("time.toTime")
//																					.gte(request.getTime().getToTime()))
//
//																	.and("fromDate").is(request.getFromDate())
//																	.and("toDate").is(request.getToDate()).and("state")
//																	.ne(AppointmentState.CANCEL.getState()))),
							AppointmentCollection.class, AppointmentCollection.class)
					.getMappedResults();
			if (userCollection != null) {
				if (appointmentCollections == null || appointmentCollections.isEmpty()
						|| !request.getIsCalenderBlocked()) {
					ObjectId patientId = null;
					if (request.getIsPatientRequired() != null && request.getIsPatientRequired()) {
						AppointmentRequest appointmentRequest = new AppointmentRequest();
						BeanUtil.map(request, appointmentRequest);
						patientId = registerPatientIfNotRegistered(appointmentRequest, doctorObjectId, locationObjectId,
								hospitalObjectId);
					}

					appointmentCollection = new AppointmentCollection();
					BeanUtil.map(request, appointmentCollection);
					appointmentCollection
							.setAppointmentId(UniqueIdInitial.EVENT.getInitial() + DPDoctorUtils.generateRandomId());
					appointmentCollection.setDoctorId(doctorObjectId);
					appointmentCollection.setLocationId(locationObjectId);
					appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setType(AppointmentType.EVENT);
					appointmentCollection.setCreatedTime(new Date());
					appointmentCollection.setAdminCreatedTime(new Date());
					appointmentCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
					appointmentCollection.setId(null);
					appointmentCollection.setDoctorIds(doctorIds);
					appointmentCollection.setPatientId(patientId);
					appointmentCollection.setHospitalId(hospitalObjectId);
					appointmentCollection = appointmentRepository.save(appointmentCollection);

					if (request.getIsCalenderBlocked()) {

						AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
						BeanUtil.map(appointmentCollection, bookedSlotCollection);
						bookedSlotCollection.setId(null);
						bookedSlotCollection.setAppointmentId(appointmentCollection.getAppointmentId());
						bookedSlotCollection.setDoctorId(doctorObjectId);
						bookedSlotCollection.setLocationId(locationObjectId);
						bookedSlotCollection.setHospitalId(hospitalObjectId);
						bookedSlotCollection.setDoctorIds(appointmentCollection.getDoctorIds());
						bookedSlotCollection = appointmentBookedSlotRepository.save(bookedSlotCollection);
					}

					final String createdBy = appointmentCollection.getCreatedBy(),
							id = appointmentCollection.getId().toString();
					if (appointmentCollection != null) {
						Executors.newSingleThreadExecutor().execute(new Runnable() {
							@Override
							public void run() {

								if (request.getDoctorIds() != null && !request.getDoctorIds().isEmpty()) {
									for (String doctorId : request.getDoctorIds()) {
										pushNotificationServices.notifyUser(doctorId,
												"Event created by " + createdBy + " is here - Tap to view it!",
												ComponentType.EVENT.getType(), id, null);
									}

								}
							}
						});
						response = new Event();
						BeanUtil.map(appointmentCollection, response);
					}
				} else {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Event updateEvent(EventRequest request, Boolean forAllDoctors) {
		Event response = null;
		try {			
			AppointmentCollection appointmentCollection = appointmentRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (appointmentCollection != null) {
				
				List<ObjectId> doctorIds = new ArrayList<ObjectId>();
				if (request.getDoctorIds() != null && !request.getDoctorIds().isEmpty()) {
					for (String doctorId : request.getDoctorIds())
						doctorIds.add(new ObjectId(doctorId));
				}
				
				if (request.getState().equals(AppointmentState.RESCHEDULE) && request.getIsCalenderBlocked()) {
					
					Aggregation aggregation = Aggregation
							.newAggregation(
									Aggregation
											.match(new Criteria("locationId")
													.is(new ObjectId(request.getLocationId()))
													.and("id").ne(new ObjectId(request.getId()))
													.andOperator(
															new Criteria().orOperator(
																	new Criteria("doctorId")
																			.is(new ObjectId(
																					request.getDoctorId())),
																	new Criteria("doctorIds")
																			.in(doctorIds)
																			.and("isCalenderBlocked")
																			.is(true)),
															new Criteria().orOperator(
																	new Criteria("time.fromTime")															
																			.lte(request.getTime()
																					.getFromTime())
																			.and("time.toTime")
																			.gte(request.getTime()
																					.getToTime()),
																	new Criteria("time.fromTime")
																			.lte(request.getTime()
																					.getFromTime())
																			.and("time.toTime")
																			.gte(request.getTime()
																					.getFromTime()),
																			new Criteria("time.fromTime")
																			.lte(request.getTime()
																					.getToTime())
																			.and("time.toTime")
																			.gte(request.getTime()
																					.getToTime())		),
															new Criteria().orOperator(new Criteria("fromDate").gte(request.getFromDate()).and("toDate").lte(request.getToDate()),
																	new Criteria("fromDate").lte(request.getFromDate()).and("toDate").gte(request.getFromDate()),
																						new Criteria("fromDate").lte(request.getToDate()).and("toDate").gte(request.getToDate()))
															)
//													.and("fromDate").gte(request.getFromDate())
//													.and("toDate").lte(request.getToDate())
													.and("state").ne(AppointmentState.CANCEL.getState())));
					List<AppointmentCollection> appointmentCollections = mongoTemplate
					.aggregate(aggregation, AppointmentCollection.class, AppointmentCollection.class).getMappedResults();
					if (appointmentCollections != null && !appointmentCollections.isEmpty()) {
						logger.error(timeSlotIsBooked);
						throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
					}
			}
					AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
					BeanUtil.map(appointmentCollection, appointmentWorkFlowCollection);
					appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

					appointmentCollection.setState(request.getState());

					if (request.getState().equals(AppointmentState.CANCEL)) {
						AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
								.findByAppointmentId(appointmentCollection.getAppointmentId());
						if (bookedSlotCollection != null)
							appointmentBookedSlotRepository.delete(bookedSlotCollection);
					} else {
						appointmentCollection.setFromDate(request.getFromDate());
						appointmentCollection.setToDate(request.getToDate());
						appointmentCollection.setTime(request.getTime());
						appointmentCollection.setIsCalenderBlocked(request.getIsCalenderBlocked());
						appointmentCollection.setExplanation(request.getExplanation());
						appointmentCollection.setDoctorIds(doctorIds);
						appointmentCollection.setSubject(request.getSubject());
						appointmentCollection.setIsAllDayEvent(request.getIsAllDayEvent());
						
						if (request.getState().equals(AppointmentState.RESCHEDULE)) {
							appointmentCollection.setIsRescheduled(true);
							appointmentCollection.setState(AppointmentState.CONFIRM);
						}
						
						AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
								.findByAppointmentId(appointmentCollection.getAppointmentId());

						if (request.getIsCalenderBlocked()) {
							if (bookedSlotCollection == null) {
								bookedSlotCollection = new AppointmentBookedSlotCollection();
								BeanUtil.map(appointmentCollection, bookedSlotCollection);
								bookedSlotCollection.setId(null);
							}
							bookedSlotCollection.setIsAllDayEvent(request.getIsAllDayEvent());
							bookedSlotCollection.setDoctorIds(doctorIds);
							bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
							bookedSlotCollection.setToDate(appointmentCollection.getToDate());
							bookedSlotCollection.setTime(appointmentCollection.getTime());
							bookedSlotCollection.setUpdatedTime(new Date());
							bookedSlotCollection = appointmentBookedSlotRepository.save(bookedSlotCollection);							
						} else {
							if (bookedSlotCollection != null)
								appointmentBookedSlotRepository.delete(bookedSlotCollection);
						}
					}
					appointmentCollection.setUpdatedTime(new Date());
					appointmentCollection = appointmentRepository.save(appointmentCollection);
					response = new Event();
					BeanUtil.map(appointmentCollection, response);
				
			} else {
				logger.error("Incorrect Id");
				throw new BusinessException(ServiceError.InvalidInput, "Incorrect Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean sendReminderToPatient(String appointmentId) {
		Boolean response = false;
		try {
			AppointmentLookupResponse appointmentLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("appointmentId").is(appointmentId)),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "patientId", "_id", "patient"), Aggregation.unwind("patient")),
					AppointmentCollection.class, AppointmentLookupResponse.class).getUniqueMappedResult();

			if (appointmentLookupResponse != null) {
				if (appointmentLookupResponse.getPatientId() != null) {
					User doctor = appointmentLookupResponse.getDoctor();
					User patient = appointmentLookupResponse.getPatient();
					PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
							new ObjectId(appointmentLookupResponse.getPatientId()),
							new ObjectId(appointmentLookupResponse.getLocationId()),
							new ObjectId(appointmentLookupResponse.getHospitalId()));

					Location locationCollection = appointmentLookupResponse.getLocation();
					if (doctor != null && locationCollection != null && patient != null) {
						DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
								.findByDoctorIdAndLocationId(new ObjectId(appointmentLookupResponse.getDoctorId()),
										new ObjectId(appointmentLookupResponse.getLocationId()));

						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

						String _24HourTime = String.format("%02d:%02d",
								appointmentLookupResponse.getTime().getFromTime() / 60,
								appointmentLookupResponse.getTime().getFromTime() % 60);
						SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
						SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
						if (clinicProfileCollection != null) {
							sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
							_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
							_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						} else {
							sdf.setTimeZone(TimeZone.getTimeZone("IST"));
							_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
							_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
						}

						Date _24HourDt = _24HourSDF.parse(_24HourTime);
						String patientName = patientCollection.getLocalPatientName(),
								dateTime = _12HourSDF.format(_24HourDt) + ", "
										+ sdf.format(appointmentLookupResponse.getFromDate()),
								doctorName = doctor.getTitle() + " " + doctor.getFirstName(),
								clinicName = locationCollection.getLocationName(),
								clinicContactNum = locationCollection.getClinicNumber() != null
										? locationCollection.getClinicNumber()
										: "";
										
						sendMsg(SMSFormatType.APPOINTMENT_REMINDER.getType(), "APPOINTMENT_REMINDER_TO_PATIENT",
								appointmentLookupResponse.getDoctorId().toString(),
								appointmentLookupResponse.getLocationId().toString(),
								appointmentLookupResponse.getHospitalId().toString(),
								appointmentLookupResponse.getPatientId().toString(), patient.getMobileNumber(),
								patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum, appointmentLookupResponse.getBranch());
						response = true;
					}
				}
			} else {
				logger.error(appointmentDoesNotExist);
				throw new BusinessException(ServiceError.InvalidInput, appointmentDoesNotExist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request) {
		List<PatientQueue> response = null;
		try {
			response = updateQueue(null, request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
					request.getPatientId(), new Date(), null, null, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding patient In Queue");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientQueue> rearrangePatientInQueue(String doctorId, String locationId, String hospitalId,
			String patientId, String appointmentId, int sequenceNo) {
		List<PatientQueue> response = null;
		try {
			response = updateQueue(appointmentId, doctorId, locationId, hospitalId, patientId, new Date(), null,
					sequenceNo, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while rearranging patient In Queue");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientQueue> getPatientQueue(String doctorId, String locationId, String hospitalId, String status) {
		List<PatientQueue> response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

			Criteria criteria = new Criteria("doctorId").is(doctorObjectId).and("locationId").is(locationObjectId)
					.and("hospitalId").is(hospitalObjectId).and("date").gt(start).lte(end).and("discarded").is(false)
					.and("isPatientDiscarded").ne(true);

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status.toUpperCase());

			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"),
							Aggregation.lookup("user_cl", "patientId", "_id", "patient.user"),
							Aggregation.unwind("patient.user"),
							Aggregation.match(new Criteria()
									.orOperator(new Criteria("patient.locationId").is(locationObjectId).and(
											"patient.hospitalId").is(hospitalObjectId), new Criteria("patient.doctorId")
													.is(doctorObjectId))),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$_id")
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("locationId", new BasicDBObject("$first", "$locationId"))
											.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
											.append("patient", new BasicDBObject("$first", "$patient"))
											.append("sequenceNo", new BasicDBObject("$first", "$sequenceNo"))
											.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
											.append("date", new BasicDBObject("$first", "$date")))),
							Aggregation.sort(new Sort(Direction.DESC, "sequenceNo"))),
					PatientQueueCollection.class, PatientQueue.class).getMappedResults();

			for (PatientQueue collection : response) {
				if (collection.getPatient().getUser() != null) {
					collection.getPatient().setColorCode(collection.getPatient().getUser().getColorCode());
					collection.getPatient().setMobileNumber(collection.getPatient().getUser().getMobileNumber());
				}

				collection.getPatient().setId(collection.getPatient().getUserId());
				collection.getPatient().setImageUrl(getFinalImageURL(collection.getPatient().getImageUrl()));
				collection.getPatient().setThumbnailUrl(getFinalImageURL(collection.getPatient().getThumbnailUrl()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while rearranging patient In Queue");
		}
		return response;
	}

	private List<PatientQueue> updateQueue(String appointmentId, String doctorId, String locationId, String hospitalId,
			String patientId, Date date, Integer startTime, Integer sequenceNo, Boolean isPatientDetailRequire) {
		List<PatientQueue> response = null;
		List<PatientQueueCollection> patientQueueCollections = null;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			DateTime start = DPDoctorUtils.getStartTime(date);
			DateTime end = DPDoctorUtils.getEndTime(date);
			PatientQueueCollection patientQueueCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(appointmentId))
				patientQueueCollection = patientQueueRepository.findByAppointmentId(appointmentId);
			else
				patientQueueCollection = patientQueueRepository.findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndDateBetween(doctorObjectId, locationObjectId, hospitalObjectId,
						patientObjectId, start, end);

			if (patientQueueCollection == null)
				patientQueueCollection = new PatientQueueCollection();

			if (!DPDoctorUtils.anyStringEmpty(appointmentId))
				patientQueueCollection.setAppointmentId(appointmentId);
			patientQueueCollection.setDoctorId(doctorObjectId);
			patientQueueCollection.setLocationId(locationObjectId);
			patientQueueCollection.setHospitalId(hospitalObjectId);
			patientQueueCollection.setPatientId(patientObjectId);
			patientQueueCollection.setDate(date);
			patientQueueCollection.setStartTime(startTime);
			patientQueueCollection.setDiscarded(false);

			patientQueueCollections = patientQueueRepository.findByDoctorIdAndLocationIdAndHospitalIdAndDateBetweenAndDiscarded(doctorObjectId, locationObjectId, hospitalObjectId,
					start, end, false, new Sort(Direction.DESC, "sequenceNo"));
			if (startTime != null) {
				if (patientQueueCollections == null || patientQueueCollections.isEmpty()) {
					patientQueueCollection.setSequenceNo(1);
					patientQueueRepository.save(patientQueueCollection);
				} else {
					for (PatientQueueCollection queueCollection : patientQueueCollections) {
						int seq = queueCollection.getSequenceNo();
						if (queueCollection.getStartTime() > startTime) {
							queueCollection.setSequenceNo(seq + 1);
							patientQueueRepository.save(queueCollection);
						} else {
							patientQueueCollection.setSequenceNo(seq + 1);
							patientQueueRepository.save(patientQueueCollection);
							break;
						}
					}
				}
			} else if (sequenceNo != null) {
				if (sequenceNo == 0) {
					for (PatientQueueCollection queueCollection : patientQueueCollections) {
						int seq = queueCollection.getSequenceNo();
						if (appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())) {
							// queueCollection.setDiscarded(true);
							patientQueueRepository.delete(queueCollection);
							break;
						} else {
							queueCollection.setSequenceNo(seq - 1);
							patientQueueRepository.save(queueCollection);
						}

					}
				} else {
					Integer toCheck = patientQueueRepository.find(appointmentId, doctorObjectId, locationObjectId,
							hospitalObjectId, patientObjectId, start, end, sequenceNo, false);
					if (toCheck == null || toCheck == 0) {
						PatientQueueCollection temp = null;
						int oldSeqNum = 0;
						int newStartTime = 0;
						for (PatientQueueCollection queueCollection : patientQueueCollections) {
							if (appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())) {
								oldSeqNum = queueCollection.getSequenceNo();
							}
							if (oldSeqNum > 0)
								break;
						}
						for (PatientQueueCollection queueCollection : patientQueueCollections) {
							if (oldSeqNum < sequenceNo) {
								if (queueCollection.getSequenceNo() >= oldSeqNum
										&& queueCollection.getSequenceNo() <= sequenceNo) {
									if (oldSeqNum == queueCollection.getSequenceNo()) {
										queueCollection.setStartTime(newStartTime + 1);
										queueCollection.setSequenceNo(sequenceNo);
										patientQueueRepository.save(queueCollection);

									} else {
										queueCollection.setSequenceNo(queueCollection.getSequenceNo() - 1);
										patientQueueRepository.save(queueCollection);
									}
								}
								newStartTime = queueCollection.getStartTime();
							} else if (oldSeqNum > sequenceNo) {
								if (queueCollection.getSequenceNo() <= oldSeqNum
										&& queueCollection.getSequenceNo() >= sequenceNo) {
									if (oldSeqNum == queueCollection.getSequenceNo()) {
										queueCollection.setSequenceNo(sequenceNo);
										temp = new PatientQueueCollection();
										BeanUtil.map(queueCollection, temp);
									} else {
										queueCollection.setSequenceNo(queueCollection.getSequenceNo() + 1);
										patientQueueRepository.save(queueCollection);
									}
								}
								newStartTime = queueCollection.getStartTime();
							}
						}
						if (temp != null) {
							temp.setStartTime(newStartTime + 1);
							patientQueueRepository.save(temp);
						}
					}

				}
			} else {
				patientQueueCollection
						.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());
				if (patientQueueCollections == null || patientQueueCollections.isEmpty()) {
					patientQueueCollection.setSequenceNo(1);
					patientQueueCollection.setStartTime(0);
				} else {
					for (PatientQueueCollection queueCollection : patientQueueCollections) {
						int seq = queueCollection.getSequenceNo();
						patientQueueCollection.setSequenceNo(seq + 1);
						patientQueueCollection.setStartTime(queueCollection.getStartTime() + 1);
						break;
					}
				}
				patientQueueRepository.save(patientQueueCollection);
			}

			if (isPatientDetailRequire) {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("doctorId").is(doctorObjectId).and("locationId")
										.is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("date")
										.gt(start).lte(end).and("discarded").is(false)),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
								Aggregation.unwind("patient"),
								Aggregation.lookup("user_cl", "patientId", "_id", "patient.user"),
								Aggregation.unwind("patient.user"),
								// Aggregation.match(new
								// Criteria("patient.locationId").is(locationObjectId).and("patient.hospitalId").is(hospitalObjectId)),
								Aggregation.sort(new Sort(Direction.ASC, "sequenceNo"))),
						PatientQueueCollection.class, PatientQueue.class).getMappedResults();
				if (response != null && !response.isEmpty())
					for (PatientQueue collection : response) {
						if (collection.getPatient().getUser() != null) {
							collection.getPatient()
									.setMobileNumber(collection.getPatient().getUser().getMobileNumber());
							collection.getPatient().setColorCode(collection.getPatient().getUser().getColorCode());
						}
						collection.getPatient().setId(collection.getPatient().getUserId());
						collection.getPatient().setImageUrl(getFinalImageURL(collection.getPatient().getImageUrl()));
						collection.getPatient()
								.setThumbnailUrl(getFinalImageURL(collection.getPatient().getThumbnailUrl()));
					}
			} else {
				patientQueueCollections = patientQueueRepository.findByDoctorIdAndLocationIdAndHospitalIdAndDateBetweenAndDiscarded(doctorObjectId, locationObjectId,
						hospitalObjectId, start, end, false, new Sort(Direction.ASC, "sequenceNo"));
				if (patientQueueCollections != null && !patientQueueCollections.isEmpty()) {
					response = new ArrayList<PatientQueue>();
					BeanUtil.map(patientQueueCollections, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	@Transactional
	public  Integer getMinutesOfDay(Date date) {
		DateTime dateTime = new DateTime(new Date(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

		Integer currentMinute = dateTime.getMinuteOfDay();
		return currentMinute;
	}

	// private Boolean checkToday(Date date) {
	// Boolean status = false;
	// DateTime inputDate = new DateTime(date,
	// DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
	// DateTime today = new
	// DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
	// if (inputDate.getYear() == today.getYear() && today.getDayOfYear() ==
	// inputDate.getDayOfYear()) {
	// status = true;
	// }
	//
	// return status;
	// }

	@Override
	@Transactional
	public  Boolean checkToday(int dayOfDate, int yearOfDate, String timeZone) {
		Boolean status = false;
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		if (yearOfDate == localCalendar.get(Calendar.YEAR) && dayOfDate == localCalendar.get(Calendar.DAY_OF_YEAR)) {
			status = true;
		}
		return status;
	}

	@Override
	@Transactional
	public Appointment getAppointmentById(ObjectId appointmentId) {
		Appointment appointment = null;
try {
		
		AppointmentLookupResponse appointmentLookupResponse = mongoTemplate
				.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(appointmentId)),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
						Aggregation.unwind("patient")), AppointmentCollection.class, AppointmentLookupResponse.class)
				.getUniqueMappedResult();

		if (appointmentLookupResponse != null) {
			appointment = new Appointment();
			BeanUtil.map(appointmentLookupResponse, appointment);
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
					new ObjectId(appointmentLookupResponse.getPatientId()),
					new ObjectId(appointmentLookupResponse.getLocationId()),
					new ObjectId(appointmentLookupResponse.getHospitalId()));

			PatientCard patient = new PatientCard();
			BeanUtil.map(patientCollection, patient);
			patient.setUserId(patient.getUserId());
			patient.setId(patient.getUserId());
			if (patient.getUser() != null)
				patient.setColorCode(patient.getUser().getColorCode());
			patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
			patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));
			appointment.setPatient(patient);
			if (appointmentLookupResponse.getDoctor() != null) {
				appointment.setDoctorName(appointmentLookupResponse.getDoctor().getTitle() + " "
						+ appointmentLookupResponse.getDoctor().getFirstName());
			}
			if (appointmentLookupResponse.getLocation() != null) {
				appointment.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
				appointment.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

				String address = (!DPDoctorUtils
						.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
								? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
								? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
								? appointmentLookupResponse.getLocation().getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
								? appointmentLookupResponse.getLocation().getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
								? appointmentLookupResponse.getLocation().getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
								? appointmentLookupResponse.getLocation().getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
								? appointmentLookupResponse.getLocation().getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}

				appointment.setClinicAddress(address);
				appointment.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
				appointment.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
			}
		}
} catch (Exception e) {
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while getting appointment");
}
		return appointment;
	}

	// @Scheduled(cron = "0 13 12 * * ?", zone = "IST")
	// @Transactional
	// public void updateQueue() {
	//
	// List<AppointmentCollection> appointmentList =
	// appointmentRepository.findByAppointment(
	// DPDoctorUtils.getFormTime(new Date()), DPDoctorUtils.getToTime(new
	// Date()),
	// new Sort(Sort.Direction.ASC, "time.fromTime"));
	// if (appointmentList != null) {
	//
	// while (!appointmentList.isEmpty()) {
	//
	// List<PatientQueueCollection> sortedList = new
	// ArrayList<PatientQueueCollection>();
	// PatientQueueCollection patientQueueCollectionPrev = null;
	// PatientQueueCollection patientQueueCollection = null;
	// AppointmentCollection appointmentCollection = null;
	// int indexi = 0;
	// while (indexi < appointmentList.size()) {
	// appointmentCollection = appointmentList.get(indexi);
	// if (sortedList.isEmpty()) {
	//
	// patientQueueCollectionPrev = new PatientQueueCollection();
	// patientQueueCollectionPrev.setAppointmentId(appointmentCollection.getAppointmentId());
	// patientQueueCollectionPrev.setDoctorId(appointmentCollection.getDoctorId());
	// patientQueueCollectionPrev.setLocationId(appointmentCollection.getLocationId());
	// patientQueueCollectionPrev.setHospitalId(appointmentCollection.getHospitalId());
	// patientQueueCollectionPrev.setPatientId(appointmentCollection.getPatientId());
	// patientQueueCollectionPrev.setDate(new Date());
	// patientQueueCollectionPrev.setStartTime(appointmentCollection.getTime().getFromTime());
	// patientQueueCollectionPrev.setDiscarded(false);
	// sortedList.add(patientQueueCollectionPrev);
	// appointmentList.remove(appointmentCollection);
	//
	// } else if
	// (appointmentCollection.getDoctorId().equals(patientQueueCollectionPrev.getDoctorId())
	// && appointmentCollection.getLocationId()
	// .equals(patientQueueCollectionPrev.getLocationId())) {
	// patientQueueCollection = new PatientQueueCollection();
	// patientQueueCollection.setAppointmentId(appointmentCollection.getAppointmentId());
	// patientQueueCollection.setDoctorId(appointmentCollection.getDoctorId());
	// patientQueueCollection.setLocationId(appointmentCollection.getLocationId());
	// patientQueueCollection.setHospitalId(appointmentCollection.getHospitalId());
	// patientQueueCollection.setPatientId(appointmentCollection.getPatientId());
	// patientQueueCollection.setDate(new Date());
	// patientQueueCollection.setStartTime(appointmentCollection.getTime().getFromTime());
	// patientQueueCollection.setDiscarded(false);
	// sortedList.add(patientQueueCollection);
	// appointmentList.remove(appointmentCollection);
	//
	// } else
	// indexi++;
	// }
	//
	// for (indexi = 0; indexi < sortedList.size(); indexi++) {
	// sortedList.get(indexi).setSequenceNo(indexi + 1);
	//
	// }
	//
	// patientQueueRepository.save(sortedList);
	// }
	// }
	// }

	@Scheduled(cron = "0 30 0 * * ?", zone = "IST")
	@Transactional
	public void updateQueue() {

		List<AppointmentCollection> appointmentList = appointmentRepository.findConfirmAppointments(
				DPDoctorUtils.getStartTime(new Date()), DPDoctorUtils.getEndTime(new Date()),
				new Sort(Sort.Direction.ASC, "time.fromTime"));
		Map<String, List<PatientQueueCollection>> doctorsPatientQueue = new HashMap<String, List<PatientQueueCollection>>();

		for (AppointmentCollection appointmentCollection : appointmentList) {
			PatientQueueCollection patientQueueCollection = new PatientQueueCollection();
			patientQueueCollection.setAppointmentId(appointmentCollection.getAppointmentId());
			patientQueueCollection.setDoctorId(appointmentCollection.getDoctorId());
			patientQueueCollection.setLocationId(appointmentCollection.getLocationId());
			patientQueueCollection.setHospitalId(appointmentCollection.getHospitalId());
			patientQueueCollection.setPatientId(appointmentCollection.getPatientId());
			patientQueueCollection.setDate(appointmentCollection.getFromDate());
			patientQueueCollection.setStartTime(appointmentCollection.getTime().getFromTime());
			patientQueueCollection.setCreatedTime(appointmentCollection.getCreatedTime());
			patientQueueCollection.setUpdatedTime(appointmentCollection.getUpdatedTime());
			patientQueueCollection.setDiscarded(false);

			List<PatientQueueCollection> patientQueueCollections = doctorsPatientQueue
					.get(appointmentCollection.getDoctorId().toString() + ""
							+ appointmentCollection.getLocationId().toString());
			if (patientQueueCollections == null) {
				patientQueueCollection.setSequenceNo(1);
				patientQueueCollections = new ArrayList<PatientQueueCollection>();
			} else
				patientQueueCollection.setSequenceNo(patientQueueCollections.size() + 1);

			patientQueueCollections.add(patientQueueCollection);
			doctorsPatientQueue.put(appointmentCollection.getDoctorId().toString() + ""
					+ appointmentCollection.getLocationId().toString(), patientQueueCollections);
		}

		for (Entry<String, List<PatientQueueCollection>> entry : doctorsPatientQueue.entrySet()) {
			patientQueueRepository.saveAll(entry.getValue());
		}

	}

	@Override
	public LocationWithPatientQueueDetails getNoOfPatientInQueue(String locationId, List<String> doctorId, String from,
			String to) {
		LocationWithPatientQueueDetails response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId))
					.and("type").is(AppointmentType.APPOINTMENT.getType())
					.and("state").ne(AppointmentState.CANCEL.getState()).and("isPatientDiscarded").ne(true);
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}
			if (DPDoctorUtils.allStringsEmpty(from, to)) {
				localCalendar.setTime(new Date());
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(start).and("toDate").lte(end);
			}

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId)
					doctorObjectIds.add(new ObjectId(id));
				criteria.and("doctorId").in(doctorObjectIds);
			}
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.group("$status").count().as("count"));

			List<Appointment> appointments = mongoTemplate
					.aggregate(aggregation, AppointmentCollection.class, Appointment.class).getMappedResults();
			if (appointments != null && !appointments.isEmpty()) {
				response = new LocationWithPatientQueueDetails();
				response.setLocationId(locationId);
				for (Appointment appointment : appointments) {
					if (!DPDoctorUtils.anyStringEmpty(appointment.getId()))
						switch (QueueStatus.valueOf(appointment.getId().toUpperCase())) {
						case SCHEDULED:
							response.setScheduledPatientNum(appointment.getCount());
							break;
						case WAITING:
							response.setWaitingPatientNum(appointment.getCount());
							break;
						case ENGAGED:
							response.setEngagedPatientNum(appointment.getCount());
							break;
						case CHECKED_OUT:
							response.setCheckedOutPatientNum(appointment.getCount());
							break;
						default:
							break;
						}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting No Of Patient In Queue");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LocationWithAppointmentCount getDoctorsWithAppointmentCount(String locationId, String role, Boolean active,
			String from, String to) {
		LocationWithAppointmentCount response = new LocationWithAppointmentCount();
		List<DoctorWithAppointmentCount> doctors = new ArrayList<DoctorWithAppointmentCount>();
		long totalCount = 0;
		try {
			Collection<ObjectId> userIds = null;
			if (!DPDoctorUtils.anyStringEmpty(role)) {
				List<UserRoleResponse> userRoleResponse = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("role").is(role.toUpperCase()).and("locationId")
										.is(new ObjectId(locationId))),
								Aggregation.lookup("user_role_cl", "_id", "roleId", "userRoleCollections")),
						RoleCollection.class, UserRoleResponse.class).getMappedResults();
				if (userRoleResponse != null && !userRoleResponse.isEmpty()) {
					List<UserRoleCollection> userRoleCollections = userRoleResponse.get(0).getUserRoleCollections();
					userIds = CollectionUtils.collect(userRoleCollections,
							new BeanToPropertyValueTransformer("userId"));
					if (userIds == null || userIds.isEmpty()) {
						return response;
					}
				}
			}

			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId));

			Criteria criteriaForActive = new Criteria();
			if (active)
				criteriaForActive.and("user.isActive").is(true);

			Aggregation aggregation = null;
			if (userIds != null && !userIds.isEmpty()) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria.and("doctorId").in(userIds)),
						Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.match(criteriaForActive));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.match(criteriaForActive));
			}

			List<UserLocationWithDoctorClinicProfile> userWithDoctorProfile = mongoTemplate.aggregate(aggregation,
					DoctorClinicProfileCollection.class, UserLocationWithDoctorClinicProfile.class).getMappedResults();

			for (Iterator<UserLocationWithDoctorClinicProfile> iterator = userWithDoctorProfile.iterator(); iterator
					.hasNext();) {
				UserLocationWithDoctorClinicProfile doctorClinicProfileCollection = iterator.next();

				UserCollection userCollection = doctorClinicProfileCollection.getUser();
				if (userCollection != null) {
					DoctorWithAppointmentCount doctor = new DoctorWithAppointmentCount();
					BeanUtil.map(userCollection, doctor);
					doctor.setDoctorId(userCollection.getId().toString());
					Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

					Criteria criteria2 = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("doctorId")
							.is(userCollection.getId()).and("locationId").is(new ObjectId(locationId))
							.and("isPatientDiscarded").is(false);
					if (!DPDoctorUtils.anyStringEmpty(from)) {
						localCalendar.setTime(new Date(Long.parseLong(from)));
						int currentDay = localCalendar.get(Calendar.DATE);
						int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
						int currentYear = localCalendar.get(Calendar.YEAR);

						DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
								DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

						criteria2.and("fromDate").gte(fromTime);
					}
					if (!DPDoctorUtils.anyStringEmpty(to)) {
						localCalendar.setTime(new Date(Long.parseLong(to)));
						int currentDay = localCalendar.get(Calendar.DATE);
						int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
						int currentYear = localCalendar.get(Calendar.YEAR);

						DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
								DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

						criteria2.and("toDate").lte(toTime);
					}
					long count = mongoTemplate.count(new Query(criteria2), AppointmentCollection.class);
					totalCount = totalCount + count;
					doctor.setNoOfAppointments(count);
					doctors.add(doctor);
				}
			}
			response.setDoctors(doctors);
			response.setNoOfAppointments(totalCount);
			response.setLocationId(locationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Object changeStatusInAppointment(String doctorId, String locationId, String hospitalId, String patientId,
			String appointmentId, String status, Boolean isObjectRequired) {
		Object response = null;
		if (isObjectRequired == false) {
			response = false;
		}
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null, patientObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);

			AppointmentCollection appointmentCollection = appointmentRepository.findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndAppointmentId(doctorObjectId, locationObjectId,
					hospitalObjectId, patientObjectId, appointmentId);
			if (appointmentCollection == null)
				throw new BusinessException(ServiceError.InvalidInput, "Appointment Not Found");

			if (status.equalsIgnoreCase(QueueStatus.SCHEDULED.name())) {
				appointmentCollection.setCheckedInAt(0);
				appointmentCollection.setEngagedAt(0);
				appointmentCollection.setWaitedFor(0);
				appointmentCollection.setCheckedOutAt(0);
				appointmentCollection.setEngagedFor(0);
			} else if (status.equalsIgnoreCase(QueueStatus.WAITING.name())) {
				appointmentCollection.setCheckedInAt(new Date(System.currentTimeMillis()).getTime());
			} else if (status.equalsIgnoreCase(QueueStatus.ENGAGED.name())) {
				appointmentCollection.setEngagedAt(new Date(System.currentTimeMillis()).getTime());
				appointmentCollection
						.setWaitedFor(appointmentCollection.getEngagedAt() - appointmentCollection.getCheckedInAt());
			} else if (status.equalsIgnoreCase(QueueStatus.CHECKED_OUT.name())) {
				appointmentCollection.setCheckedOutAt(new Date(System.currentTimeMillis()).getTime());
				appointmentCollection
						.setEngagedFor(appointmentCollection.getCheckedOutAt() - appointmentCollection.getEngagedAt());
			}

			appointmentCollection.setStatus(QueueStatus.valueOf(status));
			appointmentCollection.setUpdatedTime(new Date());
			appointmentCollection = appointmentRepository.save(appointmentCollection);

			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByLocationId(locationObjectId);
			for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
				pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
						"Appointment status changed.", ComponentType.APPOINTMENT_STATUS_CHANGE.getType(),
						appointmentCollection.getId().toString(), null);
			}

			/*
			 * pushNotificationServices.notifyUser(doctorId, "Appointment status changed.",
			 * ComponentType.APPOINTMENT_STATUS_CHANGE.getType(), null, null);
			 */
			if (isObjectRequired == true) {
				if (appointmentCollection != null) {
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);
				}
			} else {
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public CustomAppointment addCustomAppointment(CustomAppointment request) {
		CustomAppointment response = null;
		try {

			CustomAppointmentCollection appointmentCollection = null;
			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (DPDoctorUtils.anyStringEmpty(request.getPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput, "Patient Name should not Empty ");
			}
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid doctor Id");
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				appointmentCollection = customAppointmentRepository.findById(new ObjectId(request.getId())).orElse(null);
				request.setUpdatedTime(new Date());
				request.setCreatedBy(appointmentCollection.getCreatedBy());
				request.setCreatedTime(appointmentCollection.getCreatedTime());
			} else {
				appointmentCollection = new CustomAppointmentCollection();
				request.setCreatedBy(doctor.getTitle() + " " + doctor.getFirstName());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, appointmentCollection);
			customAppointmentRepository.save(appointmentCollection);
			response = new CustomAppointment();
			BeanUtil.map(appointmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public CustomAppointment deleteCustomAppointment(String appointmentId, String locationId, String hospitalId,
			String doctorId, Boolean discarded) {
		CustomAppointment response = null;
		try {
			CustomAppointmentCollection customAppointmentCollection = customAppointmentRepository.findByIdAndDoctorIdAndLocationIdAndHospitalId(
					new ObjectId(appointmentId), new ObjectId(doctorId), new ObjectId(locationId),
					new ObjectId(hospitalId));

			if (customAppointmentCollection != null) {
				customAppointmentCollection.setDiscarded(discarded);
				customAppointmentCollection.setUpdatedTime(new Date());
				customAppointmentRepository.save(customAppointmentCollection);
				response = new CustomAppointment();
				BeanUtil.map(customAppointmentCollection, response);
			}

			else {
				logger.warn("No Custom Appointment found for the given id");
				throw new BusinessException(ServiceError.NotFound, "No Custom Appointment found for the given id");
			}
		} catch (Exception e) {
			logger.error("Error while deleting Custom Appointment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while deleting Custom Appointment");
		}
		return response;
	}

	@Override
	public CustomAppointment getCustomAppointmentById(String appointmentId) {
		CustomAppointment response = null;
		try {
			CustomAppointmentCollection customAppointmentCollection = customAppointmentRepository
					.findById(new ObjectId(appointmentId)).orElse(null);
			if (customAppointmentCollection == null) {
				logger.warn("No Custom Appointment found for the given id");
				throw new BusinessException(ServiceError.NotFound, "No Custom Appointment found for the given id");
			}

			BeanUtil.map(customAppointmentCollection, response);
		} catch (Exception e) {
			logger.error("Error while get Custom Appointment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while get Custom Appointment");
		}
		return response;
	}

	@Override
	public List<CustomAppointment> getCustomAppointments(long page, int size, String locationId, String hospitalId,
			String doctorId, String updatedTime, Boolean discarded) {

		List<CustomAppointment> response = null;
		try {

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("updatedTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CustomAppointment> aggregationResults = mongoTemplate.aggregate(aggregation,
					CustomAppointmentCollection.class, CustomAppointment.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting Custom Appointment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Custom Appointment");
		}
		return response;
	}

	@Override
	public AVGTimeDetail getCustomAppointmentAVGTimeDetail(String locationId, String hospitalId, String doctorId) {
		AVGTimeDetail response = new AVGTimeDetail();
		List<CustomAppointment> customAppointments = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			Criteria criteria = new Criteria("discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			}

			customAppointments = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					CustomAppointmentCollection.class, CustomAppointment.class).getMappedResults();

			if (customAppointments != null && !customAppointments.isEmpty()) {
				response.setCountAppointment(customAppointments.size());
				Double waitingTime = 0.0, treatmentTime = 0.0, engageTime = 0.0;

				for (CustomAppointment customAppointment : customAppointments) {
					waitingTime = waitingTime + customAppointment.getWaitingTime();
					treatmentTime = treatmentTime + customAppointment.getTreatmentTime();
					engageTime = engageTime + customAppointment.getEngageTime();
				}
				response.setAvgEngageTime(engageTime / response.getCountAppointment());
				response.setAvgTreatmentTime(treatmentTime / response.getCountAppointment());
				response.setAvgWaitingTime(waitingTime / response.getCountAppointment());
			}

		} catch (Exception e) {
			logger.error("Error while AVG Time Detail of Custom Appointment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while AVG Time Detail of Custom Appointment");
		}
		return response;
	}

	@Override
	public Appointment getPatientLastAppointment(String locationId, String doctorId, String patientId) {
		Appointment response = null;
		try {
			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("locationId")
					.is(new ObjectId(locationId)).and("patientId").is(new ObjectId(patientId));

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime dateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

			criteria.and("fromDate").lte(dateTime);

			List<AppointmentLookupResponse> appointmentLookupResponses = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
							Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime")),
							Aggregation.limit(1)), AppointmentCollection.class, AppointmentLookupResponse.class)
					.getMappedResults();

			if (appointmentLookupResponses == null || appointmentLookupResponses.isEmpty()) {
				criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("patientId")
						.is(new ObjectId(patientId)).and("fromDate").gte(dateTime);

				appointmentLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")), Aggregation.limit(1)),
						AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
			}

			if (appointmentLookupResponses != null) {
				response = new Appointment();
				for (AppointmentLookupResponse collection : appointmentLookupResponses) {
					BeanUtil.map(collection, response);
					if (collection.getDoctor() != null) {
						response.setDoctorName(
								collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error while getting patient last appointment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient last appointment");
		}
		return response;
	}

	@Override
	public Clinic getClinic(String slugUrl) {
		Clinic response = new Clinic();
		Location location = null;
		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			Criteria criteria = new Criteria().andOperator(
					new Criteria("locationSlugUrl").regex("^" + slugUrl + "*", "i"), new Criteria("isClinic").is(true));
			location = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital")), LocationCollection.class, Location.class)
					.getUniqueMappedResult();
			if (location == null) {
				return null;
			} else {
				if (!DPDoctorUtils.anyStringEmpty(location.getLogoUrl()))
					location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				if (!DPDoctorUtils.anyStringEmpty(location.getLogoThumbnailUrl()))
					location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
				if (location.getImages() != null && !location.getImages().isEmpty()) {
					for (ClinicImage clinicImage : location.getImages()) {
						if (!DPDoctorUtils.anyStringEmpty(clinicImage.getImageUrl()))
							clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
						if (!DPDoctorUtils.anyStringEmpty(clinicImage.getThumbnailUrl()))
							clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
					}
				}
				String address = (!DPDoctorUtils.anyStringEmpty(location.getStreetAddress())
						? location.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getLandmarkDetails())
								? location.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getLocality()) ? location.getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getCity()) ? location.getCity() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getState()) ? location.getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getCountry()) ? location.getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getPostalCode()) ? location.getPostalCode() : "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				location.setClinicAddress(address);
				response.setLocation(location);
				if (location.getHospital() != null) {
					response.setHospital(location.getHospital());
				}

				Criteria criteria2 = new Criteria("locationId").is(new ObjectId(location.getId()));

				Criteria criteriaForActive = new Criteria();

				Aggregation aggregation = null;

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria2),
						Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.match(criteriaForActive),
						Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"), Aggregation.unwind("doctor"));

				List<UserLocationWithDoctorClinicProfile> userWithDoctorProfile = mongoTemplate.aggregate(aggregation,
						DoctorClinicProfileCollection.class, UserLocationWithDoctorClinicProfile.class)
						.getMappedResults();

				for (Iterator<UserLocationWithDoctorClinicProfile> iterator = userWithDoctorProfile.iterator(); iterator
						.hasNext();) {
					UserLocationWithDoctorClinicProfile doctorClinicProfileCollection = iterator.next();

					DoctorCollection doctorCollection = doctorClinicProfileCollection.getDoctor();
					UserCollection userCollection = doctorClinicProfileCollection.getUser();
					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
						BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
						doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId());
						doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId());
						doctor.setDoctorClinicProfile(doctorClinicProfile);

						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							@SuppressWarnings("unchecked")
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("superSpeciality"));
							doctor.setSpecialities(specialities);
						}
						if (!DPDoctorUtils.anyStringEmpty(doctor.getImageUrl()))
							doctor.setImageUrl(getFinalImageURL(doctor.getImageUrl()));
						if (!DPDoctorUtils.anyStringEmpty(doctor.getThumbnailUrl()))
							doctor.setThumbnailUrl(getFinalImageURL(doctor.getThumbnailUrl()));
						if (!DPDoctorUtils.anyStringEmpty(doctor.getCoverImageUrl()))
							doctor.setCoverImageUrl(getFinalImageURL(doctor.getCoverImageUrl()));
						if (!DPDoctorUtils.anyStringEmpty(doctor.getCoverThumbnailImageUrl()))
							doctor.setCoverThumbnailImageUrl(getFinalImageURL(doctor.getCoverThumbnailImageUrl()));
						doctors.add(doctor);
					}
				}
			}
			response.setDoctors(doctors);
			if (location != null)
				response.setId(location.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Lab getLab(String slugUrl) {
		Lab response = new Lab();
		Location location = new Location();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {

			Criteria criteria = new Criteria().andOperator(
					new Criteria("locationSlugUrl").regex("^" + slugUrl + "*", "i"), new Criteria("isLab").is(true));
			location = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital")), LocationCollection.class, Location.class)
					.getUniqueMappedResult();

			if (location == null) {
				return null;
			} else {

				location.setClinicAddress(
						(!DPDoctorUtils.anyStringEmpty(location.getStreetAddress()) ? location.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getLandmarkDetails())
										? location.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getLocality())
										? location.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getCity()) ? location.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getState()) ? location.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getCountry())
										? location.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(location.getPostalCode()) ? location.getPostalCode()
										: ""));

				location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
				location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				if (location.getImages() != null && !location.getImages().isEmpty()) {
					for (ClinicImage image : location.getImages()) {
						image.setImageUrl(getFinalImageURL(image.getImageUrl()));
						image.setThumbnailUrl(getFinalImageURL(image.getThumbnailUrl()));

					}
				}
				response.setLocation(location);
				response.setHospital(location.getHospital());

				Criteria criteria2 = new Criteria("locationId").is(new ObjectId(location.getId()));

				Criteria criteriaForActive = new Criteria();

				List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria2),
										Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
										Aggregation.unwind("user"), Aggregation.match(criteriaForActive),
										Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
										Aggregation.unwind("doctor")),

								DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
						.getMappedResults();

				for (Iterator<DoctorClinicProfileLookupResponse> iterator = doctorClinicProfileLookupResponses
						.iterator(); iterator.hasNext();) {
					DoctorClinicProfileLookupResponse doctorClinicProfileCollection = iterator.next();
					DoctorCollection doctorCollection = doctorClinicProfileCollection.getDoctor();
					UserCollection userCollection = doctorClinicProfileCollection.getUser();

					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						if (doctorClinicProfileCollection != null) {
							DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
							BeanUtil.map(location, doctorClinicProfile);
							BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
							doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId().toString());
							doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
							doctor.setDoctorClinicProfile(doctorClinicProfile);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							@SuppressWarnings("unchecked")
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("speciality"));
							doctor.setSpecialities(specialities);
						}
						doctor.setCoverImageUrl(getFinalImageURL(doctor.getCoverImageUrl()));
						doctor.setCoverThumbnailImageUrl(getFinalImageURL(doctor.getCoverImageUrl()));
						doctor.setThumbnailUrl(getFinalImageURL(doctor.getThumbnailUrl()));
						doctor.setImageUrl(getFinalImageURL(doctor.getImageUrl()));

						doctors.add(doctor);
					}
				}
				response.setDoctors(doctors);
				response.setNoOfLabTest(
						(int) mongoTemplate.count(
								new Query(new Criteria("locationId").is(new ObjectId(location.getId()))
										.and("hospitalId").is(new ObjectId(location.getHospitalId()))),
								LabTestCollection.class));
				if (response.getNoOfLabTest() != null && response.getNoOfLabTest() > 0) {
					List<LabTest> labTests = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("locationId").is(new ObjectId(location.getId()))
											.and("hospitalId").is(new ObjectId(location.getHospitalId()))),
									Aggregation.lookup("diagnostic_test_cl", "testId", "_id", "test"),
									Aggregation.unwind("test"), Aggregation.limit(5)),
							LabTestCollection.class, LabTest.class).getMappedResults();
					response.setLabTests(labTests);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Lab");
		}
		return response;
	}

	@Override
	public Appointment updateAppointmentDoctor(String appointmentId, String doctorId) {
		Appointment response = null;
		try {
			AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(appointmentId);
			if (appointmentCollection != null) {
				appointmentCollection.setDoctorId(new ObjectId(doctorId));
				appointmentCollection.setUpdatedTime(new Date());
				appointmentCollection = appointmentRepository.save(appointmentCollection);

				AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
						.findByAppointmentId(appointmentId);
				if (bookedSlotCollection != null) {
					bookedSlotCollection.setDoctorId(new ObjectId(doctorId));
					bookedSlotCollection.setUpdatedTime(new Date());
					appointmentBookedSlotRepository.save(bookedSlotCollection);
				}
				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while updating appointment doctor");
		}
		return response;
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}

	@Override
	@Transactional
	public String printPatientCard(PrintPatientCardRequest request) {
		String response = null;
		JasperReportResponse jasperReportResponse = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "EEE, d MMM yyyy hh:mm aaa";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String age = null;

		try {

			PrintSettingsCollection printSettings = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
					new ObjectId(request.getHospitalId()), ComponentType.ALL.getType());
			if (printSettings == null) {
				printSettings = new PrintSettingsCollection();
				DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
				BeanUtil.map(defaultPrintSettings, printSettings);
			}

			if (request.getPatientName() != null) {
				parameters.put("patientName", "<b>Patient Name :- </b> " + request.getPatientName());
			} else {
				parameters.put("patientName", "<b>Patient Name :- </b>  -- ");
			}
			if (request.getGender() != null) {
				parameters.put("gender", "<b>Gender :- </b> " + request.getGender());
			} else {
				parameters.put("gender", "<b>Gender :- </b> --");
			}
			if (request.getDob() != null && request.getDob().getAge() != null) {
				Age ageObj = request.getDob().getAge();
				if (ageObj.getYears() > 14)
					age = ageObj.getYears() + "yrs";
				else {
					if (ageObj.getYears() > 0)
						age = ageObj.getYears() + "yrs";
					if (ageObj.getMonths() > 0) {
						if (DPDoctorUtils.anyStringEmpty(age))
							age = ageObj.getMonths() + "months";
						else
							age = age + " " + ageObj.getMonths() + " months";
					}
					if (ageObj.getDays() > 0) {
						if (DPDoctorUtils.anyStringEmpty(age))
							age = ageObj.getDays() + "days";
						else
							age = age + " " + ageObj.getDays() + "days";
					}
				}
			}
			if (age != null) {
				parameters.put("age", "<b>Age :- </b> " + age);
			} else {
				parameters.put("age", "<b>Age :- </b> --");
			}
			if (request.getMobileNumber() != null) {
				parameters.put("mobileNumber", "<b>Mobile No. :- </b> " + request.getMobileNumber());
			} else {
				parameters.put("mobileNumber", "<b>Mobile No. :- </b> --");
			}
			if (request.getPatientId() != null) {
				parameters.put("patientId", "<b>Patient Id :- </b> " + request.getPatientId());
			} else {
				parameters.put("patientId", "<b>Patient Id :- </b> --");
			}
			if (request.getAppointmentId() != null) {
				parameters.put("requestId", "<b>Appointment Id :- </b> " + request.getAppointmentId());
			} else {
				parameters.put("requestId", "<b>Appointment Id :- </b>  --");
			}
			if (request.getFromDate() != null) {
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
				parameters.put("fromDate",
						"<b>Appointment Date :- </b>" + simpleDateFormat.format(request.getFromDate()));
			}
			if (request.getGeneralNotes() != null) {
				parameters.put("generalNotes", request.getGeneralNotes());
			}
			patientVisitService.generatePrintSetup(parameters, printSettings, new ObjectId(request.getDoctorId()));

			String pdfName = request.getPatientName() + "-PATIENT-CARD-" + new Date().getTime();
			parameters.put("contentLineSpace",
					(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
							? printSettings.getContentLineSpace()
							: LineSpace.SMALL.name());

			String layout = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
					: "PORTRAIT";
			String pageSize = printSettings != null ? (printSettings.getPageSetup() != null
					? (printSettings.getPageSetup().getPageSize() != null ? printSettings.getPageSetup().getPageSize()
							: "A4")
					: "A4") : "A4";

			Integer topMargin = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
					: 20;
			Integer bottonMargin = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
					: 20;
			Integer leftMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
							? printSettings.getPageSetup().getLeftMargin()
							: 20)
					: 20;
			Integer rightMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
							? printSettings.getPageSetup().getRightMargin()
							: 20)
					: 20;
			jasperReportResponse = jasperReportService.createPDF(ComponentType.PATIENT_CARD, parameters,
					dentalWorksFormA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
					Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	@Override
	public String downloadCalender(List<String> doctorIds, String locationId, String hospitalId, String fromTime,
			String toTime, Boolean isGroupByDoctor, Boolean showMobileNo, Boolean showAppointmentStatus,
			Boolean showNotes, Boolean showPatientGroups) {
		String response = null;
		JasperReportResponse jasperReportResponse = null;
		Date fromDate = null;
		Date toDate = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(fromTime, toTime)) {
				fromDate = new Date(Long.parseLong(fromTime));
				toDate = new Date(Long.parseLong(toTime));
			} else if (!DPDoctorUtils.anyStringEmpty(fromTime)) {
				fromDate = new Date(Long.parseLong(fromTime));
			} else if (!DPDoctorUtils.anyStringEmpty(toTime)) {
				toDate = new Date(Long.parseLong(toTime));
			} else {
				fromDate = new Date();
			}
			List<CalenderResponseForJasper> calenderResponseForJaspers = new LinkedList<CalenderResponseForJasper>();
			List<ObjectId> doctorList = null;

			if (doctorIds != null && !doctorIds.isEmpty()) {
				doctorList = new ArrayList<ObjectId>();
				for (String doctorId : doctorIds)
					doctorList.add(new ObjectId(doctorId));
			} else {
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationIdAndIsActivate(new ObjectId(locationId), true);
				doctorList = new ArrayList<ObjectId>();
				doctorIds = new ArrayList<String>();
				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						if (!DPDoctorUtils.anyStringEmpty(doctorClinicProfileCollection.getDoctorId())) {
							doctorList.add(doctorClinicProfileCollection.getDoctorId());
							doctorIds.add(doctorClinicProfileCollection.getDoctorId().toString());
						}
					}
				}

			}
			if (isGroupByDoctor) {
				for (ObjectId doctorId : doctorList) {
					List<CalenderResponseForJasper> calenderResponseForJasper = getCalenderAppointments(null, doctorId,
							locationId, hospitalId, fromDate, toDate, isGroupByDoctor);
					if (calenderResponseForJasper != null && !calenderResponseForJasper.isEmpty()) {
						calenderResponseForJaspers.addAll(calenderResponseForJasper);
					}
				}

			} else {
				calenderResponseForJaspers = getCalenderAppointments(doctorList, null, locationId, hospitalId, fromDate,
						toDate, isGroupByDoctor);
			}
			if (calenderResponseForJaspers == null || calenderResponseForJaspers.isEmpty()) {
				return "Appointment Not Found";
			}

			jasperReportResponse = createCalenderJasper(calenderResponseForJaspers, doctorIds, locationId, hospitalId,
					isGroupByDoctor, showMobileNo, showAppointmentStatus, showNotes, showPatientGroups, fromDate,
					toDate);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();

			throw new BusinessException(ServiceError.Unknown, "Error while download appointment ");
		}
		return response;
	}

	private List<CalenderResponseForJasper> getCalenderAppointments(List<ObjectId> doctorIds, ObjectId doctorId,
			String locationId, String hospitalId, Date fromTime, Date toTime, Boolean isGroup) {
		List<CalenderResponseForJasper> response = null;
		try {

			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));

			if (doctorIds != null && !doctorIds.isEmpty()) {
				criteria.and("doctorId").in(doctorIds);
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(doctorId);
			}

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			DateTime todate = null;
			DateTime fromdate = null;
			SortOperation sortOperation = null;
			if (fromTime != null && toTime != null) {
				localCalendar.setTime(fromTime);
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);
				fromdate = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").gte(fromdate);

				localCalendar.setTime(toTime);
				currentDay = localCalendar.get(Calendar.DATE);
				currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				currentYear = localCalendar.get(Calendar.YEAR);
				todate = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("toDate").lte(todate);

				if (todate.toDate().equals(fromdate.toDate()))
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "time.fromTime"));
				else {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "time.fromTime", "fromDate"));
				}
			} else {
				localCalendar.setTime(fromTime);
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);
				fromdate = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("fromDate").gte(fromdate);
				todate = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("toDate").lte(todate);
				sortOperation = Aggregation.sort(new Sort(Direction.ASC, "time.fromTime"));
			}

			ProjectionOperation projectListFirst = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("time", "$time"), Fields.field("PID", "$patientCard.PID"),
					Fields.field("fromDate", "$fromDate"), Fields.field("patientName", "$patientCard.localPatientName"),
					Fields.field("patientId", "$patientId"), Fields.field("mobileNumber", "$user.mobileNumber"),
					Fields.field("status", "$status"), Fields.field("notes", "$explanation"),
					Fields.field("state", "$state"), Fields.field("doctorId", "$doctorId")));

			CustomAggregationOperation firstGroupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("time", new BasicDBObject("$first", "$time"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("PID", new BasicDBObject("$first", "$PID"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("notes", new BasicDBObject("$first", "$notes"))
							.append("state", new BasicDBObject("$first", "$state"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))));

			ProjectionOperation projectListsecond = new ProjectionOperation(Fields.from(
					Fields.field("calenderResponse.patientName", "$patientName"),
					Fields.field("calenderResponse.fromDate", "$fromDate"),
					Fields.field("calenderResponse.PID", "$PID"), Fields.field("calenderResponse.time", "$time"),
					Fields.field("calenderResponse.mobileNumber", "$mobileNumber"),
					Fields.field("calenderResponse.status", "$status"),
					Fields.field("calenderResponse.notes", "$notes"),
					Fields.field("calenderResponse.patientId", "$patientId"),
					Fields.field("calenderResponse.state", "$state"),
					Fields.field("calenderResponse.doctorId", "$doctorId"), Fields.field("doctorId", "$doctorId")));

			CustomAggregationOperation secondGroupOperation = null;
			Aggregation aggregation = null;
			if (isGroup) {
				secondGroupOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", "$doctorId")
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("calenderResponse", new BasicDBObject("$push", "$calenderResponse"))));
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$redact",
								new BasicDBObject("$cond",
										new BasicDBObject("if",
												new BasicDBObject("$eq",
														Arrays.asList("$patientCard.locationId", "$locationId")))
																.append("then", "$$KEEP").append("else", "$$PRUNE")))),
						Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						projectListFirst, firstGroupOperation, sortOperation, projectListsecond, secondGroupOperation);
				response = mongoTemplate
						.aggregate(aggregation, AppointmentCollection.class, CalenderResponseForJasper.class)
						.getMappedResults();
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$redact",
								new BasicDBObject("$cond",
										new BasicDBObject("if",
												new BasicDBObject("$eq",
														Arrays.asList("$patientCard.locationId", "$locationId")))
																.append("then", "$$KEEP").append("else", "$$PRUNE")))),
						Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						projectListFirst, firstGroupOperation, sortOperation);

				List<CalenderResponse> calenderResponses = mongoTemplate
						.aggregate(aggregation, AppointmentCollection.class, CalenderResponse.class).getMappedResults();
				if (calenderResponses != null && !calenderResponses.isEmpty()) {
					response = new ArrayList<CalenderResponseForJasper>();
					CalenderResponseForJasper calenderResponseForJasper = new CalenderResponseForJasper();
					calenderResponseForJasper.setCalenderResponse(calenderResponses);
					response.add(calenderResponseForJasper);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting calender response ");

		}
		return response;
	}

	private JasperReportResponse createCalenderJasper(List<CalenderResponseForJasper> calenderResponseForJaspers,
			List<String> doctorList, String locationId, String hospitalId, Boolean isGroupByDoctor,
			Boolean showMobileNo, Boolean showAppointmentStatus, Boolean showNotes, Boolean showPatientGroups,
			Date fromDate, Date toDate) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		UserCollection userCollection = null;
		String pattern = "dd/MM/yyyy";
		String doctors = "";
		Boolean mbnoAvailable = false, noteAvailable = false, statusAvailable = false, groupAvailble = false;
		List<CalenderJasperBean> calenderJasperBeans = null;
		CalenderJasperBean calenderJasperBean = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

		parameters.put("date", "<b>Date :- </b>" + (fromDate != null ? simpleDateFormat.format(fromDate) : "")
				+ (toDate != null ? " To " + simpleDateFormat.format(toDate) : ""));
		List<CalenderJasperBeanList> beanLists = new ArrayList<CalenderJasperBeanList>();
		for (CalenderResponseForJasper calenderResponseForJasper : calenderResponseForJaspers) {
			CalenderJasperBeanList calenderJasperBeanList = new CalenderJasperBeanList();

			calenderJasperBeans = new ArrayList<CalenderJasperBean>();
			for (CalenderResponse calenderResponse : calenderResponseForJasper.getCalenderResponse()) {

				calenderJasperBean = new CalenderJasperBean();
				if (calenderResponse.getTime() != null) {
					int hour, min;
					hour = (calenderResponse.getTime().getFromTime() != null ? calenderResponse.getTime().getFromTime()
							: 0) / 60;
					min = (calenderResponse.getTime().getFromTime() != null ? calenderResponse.getTime().getFromTime()
							: 0) % 60;
					if (hour > 12) {
						hour = (hour % 12);
						calenderJasperBean.setTiming(hour + ":" + (min == 0 ? "00" : min) + " PM - ");

					} else {
						if (hour == 0) {
							hour = 12;
						}
						calenderJasperBean.setTiming(hour + ":" + (min == 0 ? "00" : min) + " AM - ");
					}

					hour = (calenderResponse.getTime().getToTime() != null ? calenderResponse.getTime().getToTime() : 0)
							/ 60;
					min = (calenderResponse.getTime().getToTime() != null ? calenderResponse.getTime().getToTime() : 0)
							% 60;
					if (hour > 12) {
						hour = (hour % 12);
						calenderJasperBean.setTiming(
								calenderJasperBean.getTiming() + hour + ":" + (min == 0 ? "00" : min) + " PM");

					} else {
						if (hour == 0) {
							hour = 12;
						}
						calenderJasperBean.setTiming(
								calenderJasperBean.getTiming() + hour + ":" + (min == 0 ? "00" : min) + " AM");
					}

				}
				if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getPatientId()) && showPatientGroups) {
					List<GroupCollection> groupCollections = getPatientGroup(locationId, calenderResponse.getDoctorId(),
							calenderResponse.getPatientId());
					calenderJasperBean.setGroupName("");
					if (groupCollections != null && !groupCollections.isEmpty()) {

						for (GroupCollection groupCollection : groupCollections) {
							calenderJasperBean.setGroupName(calenderJasperBean.getGroupName()
									+ (!DPDoctorUtils.anyStringEmpty(calenderJasperBean.getGroupName()) ? "," : "")
									+ groupCollection.getName());

						}
						if (!DPDoctorUtils.anyStringEmpty(calenderJasperBean.getGroupName())) {
							groupAvailble = true;

						}
					}
				}

				if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getNotes()) && showNotes) {
					calenderJasperBean.setNotes("<b>Note :- </b>" + calenderResponse.getNotes());
					noteAvailable = true;
				}

				if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getPatientName())) {
					calenderJasperBean.setPatientName(calenderResponse.getPatientName());
					if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getPID())) {
						calenderJasperBean.setPatientName(
								calenderJasperBean.getPatientName() + " (" + calenderResponse.getPID() + ")");

					}
				}

				if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getMobileNumber()) && showMobileNo) {
					calenderJasperBean.setMobileNumber(calenderResponse.getMobileNumber());
					mbnoAvailable = true;
				}

				if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getStatus()) && showAppointmentStatus) {

					if (calenderResponse.getState().equals("CANCEL")) {
						calenderJasperBean.setStatus(calenderResponse.getStatus().replace("_", " ") + " (C)");
					} else {
						calenderJasperBean.setStatus(calenderResponse.getStatus().replace("_", " "));
					}
					statusAvailable = true;
				}

				if (!DPDoctorUtils.anyStringEmpty(calenderResponse.getDoctorId()) && !isGroupByDoctor) {
					doctorList = new ArrayList<String>();
					if (!doctorList.contains(calenderResponse.getDoctorId())) {
						doctorList.add(calenderResponse.getDoctorId());
					}
				}
				calenderJasperBeans.add(calenderJasperBean);
			}

			if (isGroupByDoctor) {
				if (doctorList.contains(calenderResponseForJasper.getDoctorId())) {
					doctors = "";
					userCollection = userRepository.findById(new ObjectId(calenderResponseForJasper.getDoctorId())).orElse(null);
					if (userCollection != null) {
						doctors = (!DPDoctorUtils.anyStringEmpty(doctors) ? "," : "") + " "
								+ (!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle()
										: "DR.")
								+ " " + userCollection.getFirstName();
					}
					doctorList.remove(doctorList.indexOf(calenderResponseForJasper.getDoctorId()));
				}
			} else {
				for (String doctorId : doctorList) {

					userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
					if (userCollection != null) {
						doctors = doctors + (!DPDoctorUtils.anyStringEmpty(doctors) ? "," : "") + " "
								+ (!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle()
										: "DR.")
								+ " " + userCollection.getFirstName();

					}

				}
			}

			calenderJasperBeanList.setCalenders(calenderJasperBeans);
			calenderJasperBeanList.setDoctor("<b>Doctor:- </b>" + doctors);
			beanLists.add(calenderJasperBeanList);
		}

		parameters.put("items", beanLists);
		parameters.put("title", "Schedules For All Doctors");
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;

		parameters.put("showMobileNo", showMobileNo && mbnoAvailable);
		parameters.put("showStatus", showAppointmentStatus && statusAvailable);
		parameters.put("showNotes", showNotes && noteAvailable);
		parameters.put("showGroups", showPatientGroups && groupAvailble);
		parameters.put("isGroup", isGroupByDoctor);

		parameters.put("footerSignature", "");
		parameters.put("bottomSignText", "");
		parameters.put("contentFontSize", 11);
		parameters.put("headerLeftText", "");
		parameters.put("headerRightText", "");
		parameters.put("footerBottomText", "");
		parameters.put("logoURL", "");
		parameters.put("showTableOne", false);
		parameters.put("poweredBy", footerText);
		String pdfName = locationId + "calender-appointments" + new Date().getTime();
		parameters.put("contentLineSpace", LineSpace.SMALL.name());
		response = jasperReportService.createPDF(ComponentType.CALENDER_APPOINTMENT, parameters, calenderA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private List<GroupCollection> getPatientGroup(String locationId, String doctorId, String patirntId) {

		CustomAggregationOperation GroupOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("name", new BasicDBObject("$first", "$name"))));

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.lookup("patient_group_cl", "_id", "groupId", "patientgroup"),
				Aggregation.unwind("patientgroup"),
				Aggregation.match(new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
						.is(new ObjectId(locationId)).and("patientgroup.patientId").is(new ObjectId(patirntId))
						.and("patientgroup.discarded").is(false).and("discarded").is(false)),
				GroupOperation);

		List<GroupCollection> groupCollections = mongoTemplate
				.aggregate(aggregation, GroupCollection.class, GroupCollection.class).getMappedResults();
		return groupCollections;
	}

	@Override
	public Response<Event> getEvents(String locationId, List<String> doctorId, String from, String to, int page, int size,
			String updatedTime, String sortBy, String fromTime, String toTime, Boolean isCalenderBlocked, String state) {
		Response<Event> response = null;
		List<Event> events = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("type").is(AppointmentType.EVENT.getType()).and("updatedTime")
					.gte(new Date(updatedTimeStamp));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId)
					doctorObjectIds.add(new ObjectId(id));
				criteria.and("doctorIds").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(state))
				criteria.and("state").is(state.toUpperCase());
			
			if(isCalenderBlocked) {
				criteria.and("isCalenderBlocked").is(true);
			}
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}

			Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if(count != null && count > 0) {
				response = new Response<>();
				response.setCount(count);
				
				CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
						new BasicDBObject("id", "$_id").append("state", "$state").append("subject", "$subject")
								.append("explanation", "$explanation").append("locationId", "$locationId")
								.append("doctorId", "$doctorId").append("time", "$time")
								.append("isCalenderBlocked", "$isCalenderBlocked").append("fromDate", "$fromDate")
								.append("toDate", "$toDate").append("isAllDayEvent", "$isAllDayEvent")
								.append("isRescheduled", "$isRescheduled").append("doctorIds", "$doctorIds")
								.append("doctors.id", "$doctor._id").append("doctors.firstName", "$doctor.firstName")
								.append("hospitalId", "$hospitalId").append("patientId", "$patientId")
								.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
								.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")));

				CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("id", "$_id").append("state", new BasicDBObject("$first", "$state"))
								.append("subject", new BasicDBObject("$first", "$subject"))
								.append("explanation", new BasicDBObject("$first", "$explanation"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("toDate", new BasicDBObject("$first", "$toDate"))
								.append("isAllDayEvent", new BasicDBObject("$first", "$isAllDayEvent"))
								.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
								.append("doctorIds", new BasicDBObject("$push", "$doctorIds"))
								.append("doctors", new BasicDBObject("$push", "$doctors"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

				if (size > 0) {
					events = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$doctorIds")
													.append("preserveNullAndEmptyArrays", true))),
									Aggregation.lookup("user_cl", "doctorIds", "_id", "doctor"),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
													true))),
							project, group, sortOperation, Aggregation.skip((long)(page) * size),
									Aggregation.limit(size)),
							AppointmentCollection.class, Event.class).getMappedResults();
				} else {
					events = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$doctorIds").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("user_cl", "doctorIds", "_id", "doctor"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						project, group, sortOperation), AppointmentCollection.class, Event.class).getMappedResults();
				}
				if (events != null) {
					for (Event event : events) {
						if (!DPDoctorUtils.anyStringEmpty(event.getPatientId())) {
							PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
									new ObjectId(event.getPatientId()), new ObjectId(locationId),
									new ObjectId(event.getHospitalId()));
							if (patientCollection != null)
								event.setLocalPatientName(patientCollection.getLocalPatientName());
						}
					}
				}
				response.setDataList(events);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Event getEventById(String eventId) {
		Event response = null;
		try {
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("id", "$_id").append("state", "$state").append("subject", "$subject")
							.append("explanation", "$explanation").append("locationId", "$locationId")
							.append("doctorId", "$doctorId").append("time", "$time")
							.append("isCalenderBlocked", "$isCalenderBlocked").append("fromDate", "$fromDate")
							.append("toDate", "$toDate").append("isAllDayEvent", "$isAllDayEvent")
							.append("isRescheduled", "$isRescheduled").append("doctorIds", "$doctorIds")
							.append("localPatientName", "$patientCard.localPatientName")
							.append("doctors.id", "$doctor._id").append("doctors.firstName", "$doctor.firstName")
							.append("hospitalId", "$hospitalId").append("patientId", "$patientId")
							.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$_id").append("state", new BasicDBObject("$first", "$state"))
							.append("subject", new BasicDBObject("$first", "$subject"))
							.append("explanation", new BasicDBObject("$first", "$explanation"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("time", new BasicDBObject("$first", "$time"))
							.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("isAllDayEvent", new BasicDBObject("$first", "$isAllDayEvent"))
							.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
							.append("doctorIds", new BasicDBObject("$push", "$doctorIds"))
							.append("localPatientName", new BasicDBObject("$first", "$patientCard.localPatientName"))
							.append("doctors", new BasicDBObject("$push", "$doctors"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			response = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(eventId))),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$doctorIds").append("preserveNullAndEmptyArrays",
													true))),
									Aggregation.lookup("user_cl", "doctorIds", "_id", "doctor"),
									new CustomAggregationOperation(new Document(
											"$unwind",
											new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
													true))),

									Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$patientCard")
													.append("preserveNullAndEmptyArrays", true))),
									new CustomAggregationOperation(new Document("$redact",
											new BasicDBObject("$cond", new BasicDBObject("if",
													new BasicDBObject("$ne", Arrays.asList("$patientCard", null)))
															.append("then", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$patientCard.locationId",
																					"$locationId")))
																							.append("then", "$$KEEP")
																							.append("else", "$$PRUNE")))
															.append("else", "$$KEEP")))),
									project, group),
							AppointmentCollection.class, Event.class)
					.getUniqueMappedResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Event> getEventsByMonth(String locationId, List<String> doctorId, String from, String to, int page,
			int size, String updatedTime, String sortBy, String fromTime, String toTime, Boolean isCalenderBlocked, String state) {
		Response<Event> response = null;
		List<Event> events = null;
		try {

			if (DPDoctorUtils.allStringsEmpty(from, to)) {
				logger.error("Please specify dates");
				throw new BusinessException(ServiceError.InvalidInput, "Please specify dates");
			}
			long updatedTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("type").is(AppointmentType.EVENT.getType()).and("updatedTime")
					.gte(new Date(updatedTimeStamp));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId)
					doctorObjectIds.add(new ObjectId(id));
				criteria.and("doctorIds").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(state))
				criteria.and("state").is(state.toUpperCase());
			
			if(isCalenderBlocked) {
				criteria.and("isCalenderBlocked").is(true);
			}
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from))
				localCalendar.setTime(new Date(Long.parseLong(from)));
			else
				localCalendar.setTime(new Date(Long.parseLong(to)));

			long dateMonth = localCalendar.get(Calendar.MONTH) + 1;
			long dateYear = localCalendar.get(Calendar.YEAR);

			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}
			Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if(count != null && count > 0) {
				response = new Response<>();
				response.setCount(count);
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
						Fields.field("toDate", "$toDate"), Fields.field("type", "$type"), Fields.field("state", "$state"),
						Fields.field("subject", "$subject"), Fields.field("explanation", "$explanation"),
						Fields.field("locationId", "$locationId"), Fields.field("doctorId", "$doctorId"),
						Fields.field("time", "$time"), Fields.field("isCalenderBlocked", "$isCalenderBlocked"),
						Fields.field("isAllDayEvent", "$isAllDayEvent"), Fields.field("isRescheduled", "$isRescheduled"),
						Fields.field("doctorIds", "$doctorIds"), Fields.field("hospitalId", "$hospitalId"),
						Fields.field("patientId", "$patientId"), Fields.field("adminCreatedTime", "$adminCreatedTime"),
						Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
						Fields.field("createdBy", "$createdBy")));

				CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
						new BasicDBObject("state", "$state").append("fromDateMonth", "$fromDateMonth")
								.append("fromDateYear", "$fromDateYear").append("toDateMonth", "$toDateMonth")
								.append("toDateYear", "$toDateYear").append("subject", "$subject")
								.append("explanation", "$explanation").append("locationId", "$locationId")
								.append("doctorId", "$doctorId").append("time", "$time")
								.append("isCalenderBlocked", "$isCalenderBlocked").append("fromDate", "$fromDate")
								.append("toDate", "$toDate").append("isAllDayEvent", "$isAllDayEvent")
								.append("isRescheduled", "$isRescheduled").append("doctorIds", "$doctorIds")
								.append("doctors.id", "$doctor._id").append("doctors.firstName", "$doctor.firstName")
								.append("hospitalId", "$hospitalId").append("patientId", "$patientId")
								.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
								.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
								.append("patientCard", "$patientCard")));

				CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", "$_id")
								.append("fromDateMonth", new BasicDBObject("$first", "$fromDateMonth"))
								.append("fromDateYear", new BasicDBObject("$first", "$fromDateYear"))
								.append("toDateMonth", new BasicDBObject("$first", "$toDateMonth"))
								.append("toDateYear", new BasicDBObject("$first", "$toDateYear"))
								.append("state", new BasicDBObject("$first", "$state"))
								.append("subject", new BasicDBObject("$first", "$subject"))
								.append("explanation", new BasicDBObject("$first", "$explanation"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("toDate", new BasicDBObject("$first", "$toDate"))
								.append("isAllDayEvent", new BasicDBObject("$first", "$isAllDayEvent"))
								.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
								.append("doctorIds", new BasicDBObject("$push", "$doctorIds"))
								.append("doctors", new BasicDBObject("$push", "$doctors"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy"))
								.append("patientCard", new BasicDBObject("$first", "$patientCard"))));

				if (size > 0) {
					events = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria), projectList

							.and("fromDate").extractMonth().as("fromDateMonth").and("fromDate").extractYear()
							.as("fromDateYear").and("toDate").extractMonth().as("toDateMonth").and("toDate").extractYear()
							.as("toDateYear"),
							new CustomAggregationOperation(new Document("$match",
									new BasicDBObject("$or", Arrays.asList(
											new BasicDBObject("fromDateMonth", dateMonth).append("fromDateYear", dateYear),
											new BasicDBObject("toDateMonth", dateMonth).append("toDateYear", dateYear))))),

							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$doctorIds").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("user_cl", "doctorIds", "_id", "doctor"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
							project, group, sortOperation, Aggregation.skip((page) * size), Aggregation.limit(size)),
							AppointmentCollection.class, Event.class).getMappedResults();
				} else {
					events = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria), projectList

							.and("fromDate").extractMonth().as("fromDateMonth").and("fromDate").extractYear()
							.as("fromDateYear").and("toDate").extractMonth().as("toDateMonth").and("toDate").extractYear()
							.as("toDateYear"),

							new CustomAggregationOperation(new Document("$match",
									new BasicDBObject("$or", Arrays.asList(
											new BasicDBObject("fromDateMonth", dateMonth).append("fromDateYear", dateYear),
											new BasicDBObject("toDateMonth", dateMonth).append("toDateYear", dateYear))))),

							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$doctorIds").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("user_cl", "doctorIds", "_id", "doctor"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
							project, group, sortOperation), "appointment_cl", Event.class).getMappedResults();

				}
				if (events != null) {
					for (Event event : events) {
						if (!DPDoctorUtils.anyStringEmpty(event.getPatientId())) {
							PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
									new ObjectId(event.getPatientId()), new ObjectId(locationId),
									new ObjectId(event.getHospitalId()));
							if (patientCollection != null)
								event.setLocalPatientName(patientCollection.getLocalPatientName());
						}
					}
				}
				response.setDataList(events);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean addEditNutritionAppointment(NutritionAppointment request) {
		Boolean response = false;
		try {
			NutritionAppointmentCollection appointmentCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				appointmentCollection = nutritionAppointmentRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (appointmentCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Appointment not found By Id ");
				}
				request.setCreatedBy(appointmentCollection.getCreatedBy());
				request.setCreatedTime(appointmentCollection.getCreatedTime());
				appointmentCollection = new NutritionAppointmentCollection();
				BeanUtil.map(request, appointmentCollection);

			} else {
				appointmentCollection = new NutritionAppointmentCollection();
				BeanUtil.map(request, appointmentCollection);
				UserCollection userCollection = userRepository.findById(appointmentCollection.getUserId()).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "user not found By Id ");
				}

				appointmentCollection.setCreatedTime(new Date());
				appointmentCollection.setCreatedBy(
						(DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? "" : userCollection.getTitle())
								+ userCollection.getFirstName());

			}
			appointmentCollection = nutritionAppointmentRepository.save(appointmentCollection);
			response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Nutrition Appointment  : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Nutrition Appointment  : " + e.getCause().getMessage());

		}
		return response;
	}


	@Override
	public List<NutritionAppointment> getNutritionAppointments(int page, int size, String userId, String fromDate,
			String toDate) {
		List<NutritionAppointment> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(false);
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(fromDate));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(Long.parseLong(toDate));
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date();
				to = new Date();
			}
			criteria.and("toDate").gte(from).lte(to).and("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "toDate")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "toDate")));

			}
			AggregationResults<NutritionAppointment> aggregationResults = mongoTemplate.aggregate(aggregation,
					NutritionAppointmentCollection.class, NutritionAppointment.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Nutrition Appointment : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Nutrition Appointment : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public NutritionAppointment getNutritionAppointmentById(String appointmentId) {
		NutritionAppointment response = null;
		try {

			NutritionAppointmentCollection nutritionAppointmentCollection = nutritionAppointmentRepository
					.findById(new ObjectId(appointmentId)).orElse(null);
			response = new NutritionAppointment();
			BeanUtil.map(nutritionAppointmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Nutrition Appointment : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Nutrition Appointment : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public NutritionAppointment deleteNutritionAppointment(String appointmentId, Boolean discarded) {
		NutritionAppointment response = null;
		try {
			NutritionAppointmentCollection nutritionAppointmentCollection = nutritionAppointmentRepository
					.findById(new ObjectId(appointmentId)).orElse(null);
			if (nutritionAppointmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Appointment not found with Id ");
			}
			nutritionAppointmentCollection.setUpdatedTime(new Date());
			nutritionAppointmentCollection.setDiscarded(discarded);
			nutritionAppointmentCollection = nutritionAppointmentRepository.save(nutritionAppointmentCollection);
			response = new NutritionAppointment();
			BeanUtil.map(nutritionAppointmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting nutrition Appointment : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting nutrition Appointment : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public Boolean update() {
		List<AppointmentBookedSlotCollection> appointmentBookedSlotCollections = appointmentBookedSlotRepository.findAll();
		for(AppointmentBookedSlotCollection appointmentBookedSlotCollection : appointmentBookedSlotCollections) {
			AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(appointmentBookedSlotCollection.getAppointmentId());
			if(appointmentCollection != null) {
				appointmentBookedSlotCollection.setType(appointmentCollection.getType());
				appointmentBookedSlotCollection = appointmentBookedSlotRepository.save(appointmentBookedSlotCollection);
			}
		}
		return true;
	}

}
