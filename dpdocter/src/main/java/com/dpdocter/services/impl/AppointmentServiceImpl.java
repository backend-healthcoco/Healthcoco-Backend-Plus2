package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.beans.RegisteredPatientDetails;
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
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientQueueCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DoctorFacility;
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
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientQueueRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.AppointmentLookupResponse;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.UserLocationWithDoctorClinicProfile;
import com.dpdocter.response.UserRoleResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	private static Logger logger = Logger.getLogger(AppointmentServiceImpl.class.getName());

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LandmarkLocalityRepository landmarkLocalityRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

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
	private RegistrationService registrationService;

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

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private RecommendationsRepository recommendationsRepository;

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
				// TODO Auto-generated catch block
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
			CityCollection cityCollection = cityRepository.findOne(new ObjectId(cityId));
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw be;
		} catch (Exception e) {

			try {
				mailService.sendExceptionMail("Backend Business Exception :: While activating/deactivating city",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
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
			CityCollection city = cityRepository.findOne(new ObjectId(cityId));
			if (city != null) {
				BeanUtil.map(city, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting city", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
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
				cityCollection = cityRepository.findOne(new ObjectId(landmarkLocality.getCityId()));
			}

			List<GeocodedLocation> geocodedLocations = locationServices
					.geocodeLocation((!DPDoctorUtils.anyStringEmpty(landmarkLocalityCollection.getLandmark())
							? landmarkLocalityCollection.getLandmark() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(landmarkLocalityCollection.getLocality())
									? landmarkLocalityCollection.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getCity()) ? cityCollection.getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getState())
									? cityCollection.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(cityCollection.getCountry())
									? cityCollection.getCountry() + ", " : ""));

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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return landmarkLocality;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Clinic getClinic(String locationId, String role) {
		Clinic response = new Clinic();
		Location location = null;
		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			location = mongoTemplate
					.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").is(new ObjectId(locationId))),
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
						? location.getStreetAddress() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(location.getLandmarkDetails())
								? location.getLandmarkDetails() + ", " : "")
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

				Aggregation aggregation = null;
				if (userIds != null && !userIds.isEmpty()) {
					aggregation = Aggregation.newAggregation(
							Aggregation.match(new Criteria("locationId").is(new ObjectId(location.getId()))
									.and("doctorId").in(userIds)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"),
							Aggregation.match(new Criteria("id").in("$doctor.specialities")));
				} else {
					aggregation = Aggregation.newAggregation(
							Aggregation.match(new Criteria("locationId").is(new ObjectId(location.getId()))),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
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
						doctor.setDoctorClinicProfile(doctorClinicProfile);

						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
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
	public Appointment updateAppointment(AppointmentRequest request, Boolean updateVisit) {
		Appointment response = null;
		try {
			AppointmentLookupResponse appointmentLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("appointmentId").is(request.getAppointmentId())),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "patientId", "_id", "patient"), Aggregation.unwind("patient")),
					AppointmentCollection.class, AppointmentLookupResponse.class).getUniqueMappedResult();
			if (appointmentLookupResponse != null) {
				AppointmentCollection appointmentCollection = new AppointmentCollection();
				BeanUtil.map(appointmentLookupResponse, appointmentCollection);
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
						new ObjectId(appointmentLookupResponse.getPatientId()),
						new ObjectId(appointmentLookupResponse.getLocationId()),
						new ObjectId(appointmentLookupResponse.getHospitalId()));

				DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdLocationId(new ObjectId(appointmentLookupResponse.getDoctorId()),
								new ObjectId(appointmentLookupResponse.getLocationId()));

				AppointmentCollection appointmentCollectionToCheck = null;
				if (request.getState().equals(AppointmentState.RESCHEDULE))
					appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(
							new ObjectId(appointmentLookupResponse.getDoctorId()),
							new ObjectId(appointmentLookupResponse.getLocationId()), request.getTime().getFromTime(),
							request.getTime().getToTime(), request.getFromDate(), request.getToDate(),
							AppointmentState.CANCEL.getState());
				if (appointmentCollectionToCheck == null) {
					AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
					BeanUtil.map(appointmentLookupResponse, appointmentWorkFlowCollection);
					appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

					appointmentCollection.setState(request.getState());

					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						if (request.getCancelledBy() != null) {
							if (request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType()))
								appointmentCollection.setCancelledBy(appointmentLookupResponse.getDoctor().getTitle()
										+ " " + appointmentLookupResponse.getDoctor().getFirstName());
							else
								appointmentCollection.setCancelledBy(patientCollection.getLocalPatientName());
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
								bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
								bookedSlotCollection.setToDate(appointmentCollection.getToDate());
								bookedSlotCollection.setTime(request.getTime());
								bookedSlotCollection.setUpdatedTime(new Date());
								appointmentBookedSlotRepository.save(bookedSlotCollection);
							}
						}
					}
					appointmentCollection.setExplanation(request.getExplanation());
					appointmentCollection.setNotifyDoctorByEmail(request.getNotifyDoctorByEmail());
					appointmentCollection.setNotifyDoctorBySms(request.getNotifyDoctorBySms());
					appointmentCollection.setNotifyPatientByEmail(request.getNotifyPatientByEmail());
					appointmentCollection.setNotifyPatientBySms(request.getNotifyPatientByEmail());
					appointmentCollection.setUpdatedTime(new Date());
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

					String patientName = patientCollection.getLocalPatientName() != null
							? patientCollection.getLocalPatientName().split(" ")[0] : "",
							appointmentId = appointmentCollection.getAppointmentId(),
							dateTime = _12HourSDF.format(_24HourDt) + ", "
									+ sdf.format(appointmentCollection.getFromDate()),
							doctorName = appointmentLookupResponse.getDoctor().getTitle() + " "
									+ appointmentLookupResponse.getDoctor().getFirstName(),
							clinicName = appointmentLookupResponse.getLocation().getLocationName(),
							clinicContactNum = appointmentLookupResponse.getLocation().getClinicNumber() != null
									? appointmentLookupResponse.getLocation().getClinicNumber() : "";

					// sendSMS after appointment is saved

					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						if (request.getCancelledBy().equals(AppointmentCreatedBy.DOCTOR.getType())) {
							if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
								sendEmail(doctorName, patientName, dateTime, clinicName,
										"CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR",
										appointmentLookupResponse.getDoctor().getEmailAddress());
							if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
								if (appointmentCollection.getState().getState()
										.equals(AppointmentState.CANCEL.getState()))
									sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
											request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
											appointmentLookupResponse.getDoctor().getMobileNumber(), patientName,
											appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
											appointmentCollection.getId().toString());
							}
							if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
									&& patientCollection.getEmailAddress() != null)
								sendEmail(doctorName, patientName, dateTime, clinicName,
										"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", patientCollection.getEmailAddress());
							if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
								if (appointmentCollection.getState().getState()
										.equals(AppointmentState.CANCEL.getState()))
									sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(),
											"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getDoctorId(),
											request.getLocationId(), request.getHospitalId(), request.getPatientId(),
											appointmentLookupResponse.getPatient().getMobileNumber(), patientName,
											appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
											appointmentCollection.getId().toString());
							}
						} else {
							if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
								sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
										request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
										appointmentLookupResponse.getDoctor().getMobileNumber(), patientName,
										appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
										appointmentCollection.getId().toString());
								sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(),
										"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),
										request.getLocationId(), request.getHospitalId(), request.getPatientId(),
										appointmentLookupResponse.getPatient().getMobileNumber(), patientName,
										appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
										appointmentCollection.getId().toString());
								if (DPDoctorUtils.anyStringEmpty(patientCollection.getEmailAddress()))
									sendEmail(doctorName, patientName, dateTime, clinicName,
											"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT",
											patientCollection.getEmailAddress());
								sendEmail(doctorName, patientName, dateTime, clinicName,
										"CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT",
										appointmentLookupResponse.getDoctor().getEmailAddress());
							}
						}
					} else {
						if (request.getCreatedBy().getType().equals(AppointmentCreatedBy.DOCTOR.getType())) {
							if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
								sendEmail(doctorName, patientName, dateTime, clinicName,
										"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT",
										appointmentLookupResponse.getDoctor().getEmailAddress());

							if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
								if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
									sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
											request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
											appointmentLookupResponse.getDoctor().getMobileNumber(), patientName,
											appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
											appointmentCollection.getId().toString());
								else
									sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
											request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
											appointmentLookupResponse.getDoctor().getMobileNumber(), patientName,
											appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
											appointmentCollection.getId().toString());
							}
							if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
									&& !DPDoctorUtils.allStringsEmpty(
											appointmentLookupResponse.getPatient().getEmailAddress())) {
								sendEmail(doctorName, patientName, dateTime, clinicName,
										"CONFIRMED_APPOINTMENT_TO_PATIENT", patientCollection.getEmailAddress());
							}
							if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
								if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
									sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(),
											"CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),
											request.getLocationId(), request.getHospitalId(), request.getPatientId(),
											appointmentLookupResponse.getPatient().getMobileNumber(), patientName,
											appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
											appointmentCollection.getId().toString());
								else
									sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(),
											"RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getDoctorId(),
											request.getLocationId(), request.getHospitalId(), request.getPatientId(),
											appointmentLookupResponse.getPatient().getMobileNumber(), patientName,
											appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
											appointmentCollection.getId().toString());
							}
						}
					}
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);
					PatientCard patientCard = new PatientCard();
					BeanUtil.map(appointmentLookupResponse.getPatient(), patientCard);
					BeanUtil.map(patientCollection, patientCard);
					patientCard.setUserId(appointmentLookupResponse.getPatient().getId());
					patientCard.setId(appointmentLookupResponse.getPatient().getId());
					patientCard.setColorCode(appointmentLookupResponse.getPatient().getColorCode());
					response.setPatient(patientCard);
					response.setDoctorName(doctorName);
					if (appointmentLookupResponse.getLocation() != null) {
						response.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
						response.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

						String address = (!DPDoctorUtils
								.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
										? appointmentLookupResponse.getLocation().getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils
										.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
												? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
												: "")
								+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
										? appointmentLookupResponse.getLocation().getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
										? appointmentLookupResponse.getLocation().getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
										? appointmentLookupResponse.getLocation().getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
										? appointmentLookupResponse.getLocation().getCountry() + ", " : "")
								+ (!DPDoctorUtils
										.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
												? appointmentLookupResponse.getLocation().getPostalCode() : "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}

						response.setClinicAddress(address);
						response.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
						response.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
					}
					if (appointmentCollection.getState().getState()
							.equalsIgnoreCase(AppointmentState.CONFIRM.getState())) {
						updateQueue(appointmentCollection.getAppointmentId(),
								appointmentCollection.getDoctorId().toString(),
								appointmentCollection.getLocationId().toString(),
								appointmentCollection.getHospitalId().toString(),
								appointmentCollection.getPatientId().toString(), appointmentCollection.getFromDate(),
								appointmentCollection.getTime().getFromTime(), null, false);
					} else if (appointmentCollection.getState().getState()
							.equalsIgnoreCase(AppointmentState.CANCEL.getState())) {
						updateQueue(appointmentCollection.getAppointmentId(),
								appointmentCollection.getDoctorId().toString(),
								appointmentCollection.getLocationId().toString(),
								appointmentCollection.getHospitalId().toString(),
								appointmentCollection.getPatientId().toString(), appointmentCollection.getFromDate(),
								null, 0, false);
					}
				} else {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.InvalidInput, timeSlotIsBooked);
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
	public Appointment addAppointment(AppointmentRequest request) {
		Appointment response = null;
		DoctorClinicProfileCollection clinicProfileCollection = null;
		try {
			// New functionality for registering patient while adding
			// appointment

			if (request.getPatientId() == null || request.getPatientId().isEmpty()) {
				if (request.getLocalPatientName() == null || request.getMobileNumber() == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Patient not selected");
				}
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setFirstName(request.getLocalPatientName());
				patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
				patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = null;
				patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
				if (patientDetails != null) {
					request.setPatientId(patientDetails.getUserId());
				}
			} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				Integer patientCount = patientRepository.findCount(new ObjectId(request.getPatientId()),
						new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()));
				if (patientCount == null || patientCount == 0) {
					PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
					patientRegistrationRequest.setDoctorId(request.getDoctorId());
					patientRegistrationRequest.setUserId(request.getPatientId());
					patientRegistrationRequest.setLocationId(request.getLocationId());
					patientRegistrationRequest.setHospitalId(request.getHospitalId());
					registrationService.registerExistingPatient(patientRegistrationRequest);
				}
			}

			UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			PatientCard patientCard = null;
			List<PatientCard> patientCards = mongoTemplate
					.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
									.and("locationId").is(new ObjectId(request.getLocationId())).and("hospitalId")
									.is(new ObjectId(request.getHospitalId()))),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
							PatientCollection.class, PatientCard.class)
					.getMappedResults();
			if (patientCards != null && !patientCards.isEmpty())
				patientCard = patientCards.get(0);
			AppointmentCollection appointmentCollection = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
					request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(),
					request.getToDate(), AppointmentState.CANCEL.getState());

			if (userCollection != null && locationCollection != null && patientCard != null) {

				clinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(
						new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));

				if (appointmentCollection == null) {
					appointmentCollection = new AppointmentCollection();
					BeanUtil.map(request, appointmentCollection);
					appointmentCollection.setCreatedTime(new Date());
					appointmentCollection.setAppointmentId(
							UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());

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

					String patientName = patientCard.getLocalPatientName() != null
							? patientCard.getLocalPatientName().split(" ")[0] : "",
							appointmentId = appointmentCollection.getAppointmentId(),
							dateTime = _12HourSDF.format(_24HourDt) + ", "
									+ sdf.format(appointmentCollection.getFromDate()),
							doctorName = userCollection.getTitle() + " " + userCollection.getFirstName(),
							clinicName = locationCollection.getLocationName(),
							clinicContactNum = locationCollection.getClinicNumber() != null
									? locationCollection.getClinicNumber() : "";

					if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
						appointmentCollection.setState(AppointmentState.CONFIRM);
						appointmentCollection
								.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
					} else {
						appointmentCollection.setCreatedBy(patientCard.getLocalPatientName());
						if (clinicProfileCollection != null && clinicProfileCollection.getFacility() != null
								&& (clinicProfileCollection.getFacility().getType()
										.equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
							appointmentCollection.setState(AppointmentState.CONFIRM);
						} else {
							appointmentCollection.setState(AppointmentState.NEW);
						}
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
					if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
						if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
							sendEmail(doctorName, patientName, dateTime, clinicName,
									"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", userCollection.getEmailAddress());
						if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
							sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName,
									clinicName, clinicContactNum, appointmentCollection.getId().toString());
						}
						if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
								&& patientCard.getEmailAddress() != null)
							sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
									patientCard.getEmailAddress());
						if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
							sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientCard.getUser().getMobileNumber(), patientName,
									appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
									appointmentCollection.getId().toString());
						}
					} else {
						if (clinicProfileCollection != null && clinicProfileCollection.getFacility() != null
								&& (clinicProfileCollection.getFacility().getType()
										.equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
							sendEmail(doctorName, patientName, dateTime, clinicName,
									"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", userCollection.getEmailAddress());
							sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName,
									clinicName, clinicContactNum, appointmentCollection.getId().toString());
							sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientCard.getUser().getMobileNumber(), patientName,
									appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
									appointmentCollection.getId().toString());
						} else {
							sendEmail(doctorName, patientName, dateTime, clinicName,
									"CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", userCollection.getEmailAddress());
							sendMsg(null, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName,
									clinicName, clinicContactNum, appointmentCollection.getId().toString());
							sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "TENTATIVE_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientCard.getUser().getMobileNumber(), patientName,
									appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
									appointmentCollection.getId().toString());
						}
					}
					if (appointmentCollection != null) {
						response = new Appointment();
						BeanUtil.map(appointmentCollection, response);
						BeanUtil.map(patientCard.getUser(), patientCard);
						patientCard.setUserId(patientCard.getUserId());
						patientCard.setId(patientCard.getUserId());
						patientCard.setColorCode(patientCard.getUser().getColorCode());
						response.setPatient(patientCard);
						if (userCollection != null)
							response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
						if (locationCollection != null) {
							response.setLocationName(locationCollection.getLocationName());
							response.setClinicNumber(locationCollection.getClinicNumber());

							String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
									? locationCollection.getStreetAddress() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
											? locationCollection.getLandmarkDetails() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
											? locationCollection.getLocality() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
											? locationCollection.getCity() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
											? locationCollection.getState() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
											? locationCollection.getCountry() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
											? locationCollection.getPostalCode() : "");

							if (address.charAt(address.length() - 2) == ',') {
								address = address.substring(0, address.length() - 2);
							}

							response.setClinicAddress(address);
							response.setLatitude(locationCollection.getLatitude());
							response.setLongitude(locationCollection.getLongitude());
						}
					}

					if (appointmentCollection.getState().getState()
							.equalsIgnoreCase(AppointmentState.CONFIRM.getState())) {
						updateQueue(appointmentCollection.getAppointmentId(),
								appointmentCollection.getDoctorId().toString(),
								appointmentCollection.getLocationId().toString(),
								appointmentCollection.getHospitalId().toString(),
								appointmentCollection.getPatientId().toString(), appointmentCollection.getFromDate(),
								appointmentCollection.getTime().getFromTime(), null, false);
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

	private void sendEmail(String doctorName, String patientName, String dateTime, String clinicName, String type,
			String emailAddress) throws MessagingException {
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToPatient.vm");
			mailService.sendEmail(emailAddress, appointmentConfirmToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToDoctorByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentConfirmToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRequestToDoctorByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentRequestToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByDoctorToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByPatientToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentRescheduleToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRescheduleByDoctorToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentRescheduleToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		default:
			break;
		}

	}

	private void sendMsg(String formatType, String type, String doctorId, String locationId, String hospitalId,
			String userId, String mobileNumber, String patientName, String appointmentId, String dateTime,
			String doctorName, String clinicName, String clinicContactNum, String id) {
		SMSFormatCollection smsFormatCollection = null;
		if (formatType != null) {
			smsFormatCollection = sMSFormatRepository.find(new ObjectId(doctorId), new ObjectId(locationId),
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
				if (!smsFormatCollection.getContent().equals(SMSContent.CLINIC_CONTACT_NUMBER.getContent())
						|| clinicContactNum == null)
					clinicContactNum = "";
			}
		}
		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment " + appointmentId + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + ".";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + ".";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment " + appointmentId + " @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been sent for confirmation. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName
					+ " has been cancelled as per your request.";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment " + appointmentId + " @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ ". Request you to book again. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName
					+ ", has been cancelled by patient.";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment " + appointmentId + " for " + dateTime + " with " + doctorName
					+ " has been cancelled as per your request. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an upcoming appointment " + appointmentId + " @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ". Download Healthcoco App- "
					+ patientAppBitLink;
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment " + appointmentId + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been rescheduled @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName
					+ ".";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(), id);
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
	public List<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		List<Appointment> response = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toTime);
			}

			List<AppointmentLookupResponse> appointmentLookupResponses = null;

			if (size > 0) {
				appointmentLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.skip((page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime"))),
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

			if (appointmentLookupResponses != null && !appointmentLookupResponses.isEmpty()) {
				response = new ArrayList<Appointment>();

				for (AppointmentLookupResponse collection : appointmentLookupResponses) {
					Appointment appointment = new Appointment();
					PatientCard patient = null;
					if (collection.getType().getType().equals(AppointmentType.APPOINTMENT.getType())) {
						List<PatientCard> patientCards = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(collection.getPatientId()))
								.and("locationId").is(new ObjectId(collection.getLocationId())).and("hospitalId").is(new ObjectId(collection.getHospitalId()))), 
								Aggregation.lookup("user_cl", "userId", "_id", "user"), 
								Aggregation.unwind("user")), PatientCollection.class, PatientCard.class).getMappedResults();
						if(patientCards != null && !patientCards.isEmpty())patient = patientCards.get(0);
						
						patient.setId(patient.getUserId());
						if(patient.getUser() != null)patient.setColorCode(patient.getUser().getColorCode());
						
					}
					BeanUtil.map(collection, appointment);
					appointment.setPatient(patient);
					if (collection.getDoctor() != null) {
						appointment.setDoctorName(
								collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
					}
					if (collection.getLocation() != null) {
						appointment.setLocationName(collection.getLocation().getLocationName());
						appointment.setClinicNumber(collection.getLocation().getClinicNumber());

						String address = (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getStreetAddress())
								? collection.getLocation().getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
										? collection.getLocation().getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
										? collection.getLocation().getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
										? collection.getLocation().getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
										? collection.getLocation().getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
										? collection.getLocation().getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
										? collection.getLocation().getPostalCode() : "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}

						appointment.setClinicAddress(address);
						appointment.setLatitude(collection.getLocation().getLatitude());
						appointment.setLongitude(collection.getLocation().getLongitude());
					}
					response.add(appointment);
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
	public List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		List<Appointment> response = null;
		List<AppointmentLookupResponse> appointmentLookupResponses = null;
		try {

			long updatedTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
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

				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toTime);
			}

			if (size > 0) {
				appointmentLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.skip((page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.from"))),
						AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
			} else {
				appointmentLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.from"))),
						AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
			}

			if (appointmentLookupResponses != null) {
				response = new ArrayList<Appointment>();
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
								? collection.getLocation().getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
										? collection.getLocation().getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
										? collection.getLocation().getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
										? collection.getLocation().getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
										? collection.getLocation().getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
										? collection.getLocation().getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
										? collection.getLocation().getPostalCode() : "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						appointment.setClinicAddress(address);
						appointment.setLatitude(collection.getLocation().getLatitude());
						appointment.setLongitude(collection.getLocation().getLongitude());
					}
					response.add(appointment);
				}
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
	public Lab getLab(String locationId, String patientId) {
		Lab response = new Lab();
		Location location = new Location();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			location = mongoTemplate
					.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").is(new ObjectId(locationId)).and("isLab").is(true)),
							Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital")), LocationCollection.class, Location.class)
					.getUniqueMappedResult();

			if (location == null) {
				return null;
			} else {
				RecommendationsCollection recommendationsCollection = new RecommendationsCollection();
				if (!DPDoctorUtils.anyStringEmpty(patientId)) {
					recommendationsCollection = recommendationsRepository.findByDoctorIdLocationIdAndPatientId(null,
							new ObjectId(locationId), new ObjectId(patientId));
				}
				location.setIsClinicRecommended(!recommendationsCollection.getDiscarded());
				location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
				location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				if(location.getImages() != null && !location.getImages().isEmpty()){
					for (ClinicImage image : location.getImages()) {
						image.setImageUrl(getFinalImageURL(image.getImageUrl()));
						image.setThumbnailUrl(getFinalImageURL(image.getThumbnailUrl()));

					}
				}
				response.setLocation(location);
				response.setHospital(location.getHospital());
				List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate
						.aggregate(Aggregation.newAggregation(
								Aggregation.match(new Criteria("locationId").is(new ObjectId(locationId))),
								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
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
									(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
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

	@Override
	@Transactional
	public SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		SlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(doctorObjectId,
					locationObjectId);
			if (doctorClinicProfileCollection != null) {

				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				String day = sdf.format(date);
				if (doctorClinicProfileCollection.getWorkingSchedules() != null
						&& doctorClinicProfileCollection.getAppointmentSlot() != null) {
					response = new SlotDataResponse();
					response.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
					slotResponse = new ArrayList<Slot>();
					for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
						if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
							List<WorkingHours> workingHours = workingSchedule.getWorkingHours();
							if (workingHours != null && !workingHours.isEmpty()) {
								for (WorkingHours workingHour : workingHours) {
									if (workingHour.getFromTime() != null && workingHour.getToTime() != null
											&& doctorClinicProfileCollection.getAppointmentSlot().getTime() > 0) {
										List<Slot> slots = DateAndTimeUtility.sliceTime(workingHour.getFromTime(),
												workingHour.getToTime(), Math.round(
														doctorClinicProfileCollection.getAppointmentSlot().getTime()));
										if (slots != null)
											slotResponse.addAll(slots);
									}
								}
							}
						}
					}

					Calendar localCalendar = Calendar
							.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
					localCalendar.setTime(date);
					int dayOfDate = localCalendar.get(Calendar.DATE);
					int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
					int yearOfDate = localCalendar.get(Calendar.YEAR);

					if (checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
							doctorClinicProfileCollection.getTimeZone()))
						for (Slot slot : slotResponse) {
							if (slot.getMinutesOfDay() < getMinutesOfDay(date)) {
								slot.setIsAvailable(false);
								slotResponse.set(slotResponse.indexOf(slot), slot);
							}

						}

					DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
							.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
					DateTime end = new DateTime(yearOfDate, monthOfDate, dayOfDate, 23, 59, 59, DateTimeZone
							.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
					List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository
							.findByDoctorLocationId(doctorObjectId, locationObjectId, start, end);
					if (bookedSlots != null && !bookedSlots.isEmpty())
						for (AppointmentBookedSlotCollection bookedSlot : bookedSlots) {
							if (bookedSlot.getTime() != null) {
								if (!bookedSlot.getFromDate().equals(bookedSlot.getToDate())) {
									if (bookedSlot.getIsAllDayEvent()) {
										if (bookedSlot.getFromDate().equals(date))
											bookedSlot.getTime().setToTime(719);
										if (bookedSlot.getToDate().equals(date))
											bookedSlot.getTime().setFromTime(0);
									}
								}
								List<Slot> slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFromTime(),
										bookedSlot.getTime().getToTime(),
										Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
								for (Slot slot : slots) {
									if (slotResponse.contains(slot)) {
										slot.setIsAvailable(false);
										slotResponse.set(slotResponse.indexOf(slot), slot);
									}
								}
							}
						}
					response.setSlots(slotResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment addEvent(EventRequest request) {
		Appointment response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());

			UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));

			AppointmentCollection appointmentCollection = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(
					doctorObjectId, locationObjectId, request.getTime().getFromTime(), request.getTime().getToTime(),
					request.getFromDate(), request.getToDate(), AppointmentState.CANCEL.getState());

			if (userCollection != null) {
				if (appointmentCollection == null || !request.getIsCalenderBlocked()) {
					appointmentCollection = new AppointmentCollection();
					BeanUtil.map(request, appointmentCollection);
					appointmentCollection.setAppointmentId(
							UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());
					appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setType(AppointmentType.EVENT);
					appointmentCollection.setCreatedTime(new Date());
					appointmentCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
					appointmentCollection.setId(null);
					appointmentCollection = appointmentRepository.save(appointmentCollection);

					if (request.getIsCalenderBlocked()) {
						AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
						BeanUtil.map(appointmentCollection, bookedSlotCollection);
						bookedSlotCollection.setId(null);
						bookedSlotCollection.setAppointmentId(appointmentCollection.getAppointmentId());
						appointmentBookedSlotRepository.save(bookedSlotCollection);
					}

					if (appointmentCollection != null) {
						response = new Appointment();
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
	public Appointment updateEvent(EventRequest request) {
		Appointment response = null;
		try {
			AppointmentCollection appointmentCollection = appointmentRepository.findOne(new ObjectId(request.getId()));
			if (appointmentCollection != null) {
				AppointmentCollection appointmentCollectionToCheck = null;
				if (request.getState().equals(AppointmentState.RESCHEDULE)) {
					appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(
							appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(),
							request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(),
							request.getToDate(), AppointmentState.CANCEL.getState());
					if (appointmentCollectionToCheck != null)
						if (!request.getIsCalenderBlocked())
							appointmentCollectionToCheck = null;
				}
				if (appointmentCollectionToCheck == null) {
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
						if (request.getState().equals(AppointmentState.RESCHEDULE)) {
							appointmentCollection.setIsRescheduled(true);
							appointmentCollection.setState(AppointmentState.CONFIRM);
							AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
									.findByAppointmentId(appointmentCollection.getAppointmentId());

							if (request.getIsCalenderBlocked()) {
								if (bookedSlotCollection != null) {
									bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
									bookedSlotCollection.setToDate(appointmentCollection.getToDate());
									bookedSlotCollection.setTime(appointmentCollection.getTime());
									bookedSlotCollection.setUpdatedTime(new Date());
									appointmentBookedSlotRepository.save(bookedSlotCollection);
								}
							} else {
								if (bookedSlotCollection != null)
									appointmentBookedSlotRepository.delete(bookedSlotCollection);
							}
						}
					}
					appointmentCollection.setUpdatedTime(new Date());
					appointmentCollection = appointmentRepository.save(appointmentCollection);
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);
				} else {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
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
					PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
							new ObjectId(appointmentLookupResponse.getPatientId()),
							new ObjectId(appointmentLookupResponse.getLocationId()),
							new ObjectId(appointmentLookupResponse.getHospitalId()));

					Location locationCollection = appointmentLookupResponse.getLocation();
					if (doctor != null && locationCollection != null && patient != null) {
						DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
								.findByDoctorIdLocationId(new ObjectId(appointmentLookupResponse.getDoctorId()),
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
										? locationCollection.getClinicNumber() : "";
						sendMsg(SMSFormatType.APPOINTMENT_REMINDER.getType(), "APPOINTMENT_REMINDER_TO_PATIENT",
								appointmentLookupResponse.getDoctorId().toString(),
								appointmentLookupResponse.getLocationId().toString(),
								appointmentLookupResponse.getHospitalId().toString(),
								appointmentLookupResponse.getPatientId().toString(), patient.getMobileNumber(),
								patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum,
								appointmentLookupResponse.getId().toString());
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
	public List<PatientQueue> getPatientQueue(String doctorId, String locationId, String hospitalId) {
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

			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("doctorId").is(doctorObjectId)
									.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
									.and("date").gt(start).lte(end).and("discarded").is(false)),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"),
							Aggregation.lookup("user_cl", "patientId", "_id", "patient.user"),
							Aggregation.unwind("patient.user"),
							// Aggregation.match(new
							// Criteria("$patient.locationId").is(locationObjectId).and("$patient.hospitalId").is(hospitalObjectId)),
							Aggregation.sort(new Sort(Direction.DESC, "sequenceNo"))),
					PatientQueueCollection.class, PatientQueue.class).getMappedResults();
			for (PatientQueue collection : response) {
				if (collection.getPatient().getUser() != null)
					collection.getPatient().setColorCode(collection.getPatient().getUser().getColorCode());
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

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			localCalendar.setTime(date);
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			PatientQueueCollection patientQueueCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(appointmentId))
				patientQueueCollection = patientQueueRepository.find(appointmentId);
			else
				patientQueueCollection = patientQueueRepository.find(doctorObjectId, locationObjectId, hospitalObjectId,
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

			patientQueueCollections = patientQueueRepository.find(doctorObjectId, locationObjectId, hospitalObjectId,
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
						if (collection.getPatient().getUser() != null)
							collection.getPatient().setColorCode(collection.getPatient().getUser().getColorCode());
						collection.getPatient().setId(collection.getPatient().getUserId());
						collection.getPatient().setImageUrl(getFinalImageURL(collection.getPatient().getImageUrl()));
						collection.getPatient()
								.setThumbnailUrl(getFinalImageURL(collection.getPatient().getThumbnailUrl()));
					}
			} else {
				patientQueueCollections = patientQueueRepository.find(doctorObjectId, locationObjectId,
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

	private Integer getMinutesOfDay(Date date) {
		DateTime dateTime = new DateTime(new Date(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
		;
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

	private Boolean checkToday(int dayOfDate, int yearOfDate, String timeZone) {
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
		AppointmentLookupResponse appointmentLookupResponse = mongoTemplate.aggregate(
				Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(appointmentId)),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
						Aggregation.unwind("patient")),
				AppointmentCollection.class, AppointmentLookupResponse.class).getUniqueMappedResult();

		if (appointmentLookupResponse != null) {
			appointment = new Appointment();
			BeanUtil.map(appointmentLookupResponse, appointment);
			PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
					new ObjectId(appointmentLookupResponse.getPatientId()),
					new ObjectId(appointmentLookupResponse.getLocationId()),
					new ObjectId(appointmentLookupResponse.getHospitalId()));

			PatientCard patient = new PatientCard();
			BeanUtil.map(patientCollection, patient);
			patient.setUserId(patient.getUserId());
			patient.setId(patient.getUserId());
			if (patient.getUser() != null)
				patient.setColorCode(patient.getUser().getColorCode());
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
								? appointmentLookupResponse.getLocation().getStreetAddress() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
								? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
								? appointmentLookupResponse.getLocation().getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
								? appointmentLookupResponse.getLocation().getCity() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
								? appointmentLookupResponse.getLocation().getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
								? appointmentLookupResponse.getLocation().getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
								? appointmentLookupResponse.getLocation().getPostalCode() : "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}

				appointment.setClinicAddress(address);
				appointment.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
				appointment.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
			}
		}
		return appointment;
	}
}
