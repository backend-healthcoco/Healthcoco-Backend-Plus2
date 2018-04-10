package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.Community;
import com.dpdocter.beans.FoodPreferences;
import com.dpdocter.beans.GeographicalArea;
import com.dpdocter.beans.LaptopUsage;
import com.dpdocter.beans.Meal;
import com.dpdocter.beans.MobilePhoneUsage;
import com.dpdocter.beans.PatientInfo;
import com.dpdocter.beans.PrimaryDetail;
import com.dpdocter.beans.Sleep;
import com.dpdocter.beans.TvUsage;
import com.dpdocter.beans.WorkHistory;
import com.dpdocter.collections.PatientInfoCollection;
import com.dpdocter.repository.PatientInfoRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.PatientInfoServices;

@Service
public class PatientInfoServicesImpl implements PatientInfoServices {

	private static Logger logger = Logger.getLogger(PatientInfoServicesImpl.class.getName());

	@Autowired
	private PatientInfoRepository patientInfoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;


	public PatientInfo addPatient(PatientInfo request) {

		PatientInfo response = null;

		PatientInfoCollection patientInfoCollection = new PatientInfoCollection();

		PatientInfo patientInfo = new PatientInfo();
		patientInfoCollection.setPatientId(new ObjectId());
		patientInfo.setPatientId(patientInfoCollection.getPatientId().toString());
		
		patientInfoCollection.setDoctorId(new ObjectId(request.getDoctorId()));
		patientInfo.setDoctorId(patientInfoCollection.getDoctorId().toString());
		
		patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

		response = patientInfo;

		return response;
	}


	@Transactional
	public Activity updateActivity(PatientInfo request) {
		Activity response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));

			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getActivity() != null) {

					patientInfoCollection.getActivity().setLifestyle(request.getActivity().getLifestyle());
					patientInfoCollection.getActivity().setExerciseType(request.getActivity().getExerciseType());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getActivity();
				} else {
					Activity activity = new Activity();
					//activity.setLifestyle(request.getActivity().getLifestyle());
					//activity.setExerciseType(request.getActivity().getExerciseType());
					patientInfoCollection.setActivity(activity);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getActivity();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Activity getActivity(String request) {

		Activity response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getActivity() != null) {

					response = patientInfoCollection.getActivity();
				} else {
					Activity activity = new Activity();
					patientInfoCollection.setActivity(activity);
					response = patientInfoCollection.getActivity();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Community updateCommunity(PatientInfo request) {
		Community response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getCommunity() != null) {

					patientInfoCollection.getCommunity().setCommunity(request.getCommunity().getCommunity());

					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);



					response = patientInfoCollection.getCommunity();
				} else {
					Community community = new Community();
					//community.setCommunity(request.getCommunity().getCommunity());
					patientInfoCollection.setCommunity(community);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getCommunity();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Community getCommunity(String request) {

		Community response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getCommunity() != null) {

					response = patientInfoCollection.getCommunity();
				} else {
					Community community = new Community();
					patientInfoCollection.setCommunity(community);
					response = patientInfoCollection.getCommunity();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public FoodPreferences updateFoodPreferences(PatientInfo request) {

		FoodPreferences response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getFoodPreferences() != null) {

					patientInfoCollection.getFoodPreferences().setFoodpref(request.getFoodPreferences().getFoodpref());

					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getFoodPreferences();
				} else {
					FoodPreferences foodpref = new FoodPreferences();
					//foodpref.setFoodpref(request.getFoodPreferences().getFoodpref());
					patientInfoCollection.setFoodPreferences(foodpref);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getFoodPreferences();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public FoodPreferences getFoodPreferences(String request) {

		FoodPreferences response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getFoodPreferences() != null) {

					response = patientInfoCollection.getFoodPreferences();
				} else {
					FoodPreferences foodpreferences = new FoodPreferences();
					patientInfoCollection.setFoodPreferences(foodpreferences);
					response = patientInfoCollection.getFoodPreferences();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public GeographicalArea updateGeographicalArea(PatientInfo request) {

		GeographicalArea response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getGeographicalArea() != null) {

					patientInfoCollection.getGeographicalArea()
					.setGeographicalArea(request.getGeographicalArea().getGeographicalArea());

					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getGeographicalArea();
				} else {
					GeographicalArea geographicalArea = new GeographicalArea();
					//geographicalArea.setGeographicalArea(request.getGeographicalArea().getGeographicalArea());
					patientInfoCollection.setGeographicalArea(geographicalArea);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getGeographicalArea();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public GeographicalArea getGeographicalArea(String request) {

		GeographicalArea response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getGeographicalArea() != null) {

					response = patientInfoCollection.getGeographicalArea();
				} else {
					GeographicalArea geographicalArea = new GeographicalArea();
					patientInfoCollection.setGeographicalArea(geographicalArea);
					response = patientInfoCollection.getGeographicalArea();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public LaptopUsage updateLaptopUsage(PatientInfo request) {

		LaptopUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getLaptopUsage() != null) {

					patientInfoCollection.getLaptopUsage()
					.setLaptopInBedroom(request.getLaptopUsage().getLaptopInBedroom());
					patientInfoCollection.getLaptopUsage().setHoursperday(request.getLaptopUsage().getHoursperday());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getLaptopUsage();
				} else {
					LaptopUsage laptopUsage = new LaptopUsage();
					//laptopUsage.setHoursperday(request.getLaptopUsage().getHoursperday());
					//laptopUsage.setLaptopInBedroom(request.getLaptopUsage().getLaptopInBedroom());
					patientInfoCollection.setLaptopUsage(laptopUsage);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getLaptopUsage();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public LaptopUsage getLaptopUsage(String request) {
		LaptopUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getLaptopUsage() != null) {

					response = patientInfoCollection.getLaptopUsage();
				} else {
					LaptopUsage laptopUsage = new LaptopUsage();
					patientInfoCollection.setLaptopUsage(laptopUsage);
					response = patientInfoCollection.getLaptopUsage();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public Meal updateMeal(PatientInfo request) {

		Meal response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMeal() != null) {

					patientInfoCollection.getMeal().setMealcontent(request.getMeal().getMealcontent());
					patientInfoCollection.getMeal().setMealtime(request.getMeal().getMealtime());
					patientInfoCollection.getMeal().setMealtype(request.getMeal().getMealtype());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getMeal();
				} else {
					Meal meal = new Meal();
					//meal.setMealcontent(request.getMeal().getMealcontent());
					//meal.setMealtime(request.getMeal().getMealtime());
					//meal.setMealtype(request.getMeal().getMealtype());
					patientInfoCollection.setMeal(meal);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getMeal();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public Meal getMeal(String request) {

		Meal response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMeal() != null) {

					response = patientInfoCollection.getMeal();
				} else {
					Meal meal = new Meal();
					patientInfoCollection.setMeal(meal);
					response = patientInfoCollection.getMeal();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public MobilePhoneUsage updateMobilePhoneUsage(PatientInfo request) {

		MobilePhoneUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMobilePhoneUsage() != null) {

					patientInfoCollection.getMobilePhoneUsage()
					.setHoursperday(request.getMobilePhoneUsage().getHoursperday());
					patientInfoCollection.getMobilePhoneUsage()
					.setTalkFrom(request.getMobilePhoneUsage().getTalkFrom());
					patientInfoCollection.getMobilePhoneUsage().setTalkTo(request.getMobilePhoneUsage().getTalkTo());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getMobilePhoneUsage();
				} else {
					MobilePhoneUsage mobilePhoneUsage = new MobilePhoneUsage();
					//mobilePhoneUsage.setHoursperday(request.getMobilePhoneUsage().getHoursperday());
					//mobilePhoneUsage.setTalkFrom(request.getMobilePhoneUsage().getTalkFrom());
					//mobilePhoneUsage.setTalkTo(request.getMobilePhoneUsage().getTalkTo());
					patientInfoCollection.setMobilePhoneUsage(mobilePhoneUsage);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getMobilePhoneUsage();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public MobilePhoneUsage getMobilePhoneUsage(String request) {

		MobilePhoneUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMobilePhoneUsage() != null) {

					response = patientInfoCollection.getMobilePhoneUsage();
				} else {
					MobilePhoneUsage mobilePhoneUsage = new MobilePhoneUsage();
					patientInfoCollection.setMobilePhoneUsage(mobilePhoneUsage);
					response = patientInfoCollection.getMobilePhoneUsage();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public PrimaryDetail updatePrimaryDetail(PatientInfo request) {

		PrimaryDetail response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getPrimaryDetail() != null) {

					patientInfoCollection.getPrimaryDetail().setDateOfBirth(request.getPrimaryDetail().getDateOfBirth());
					patientInfoCollection.getPrimaryDetail().setGender(request.getPrimaryDetail().getGender());
					patientInfoCollection.getPrimaryDetail().setMobilenumber(request.getPrimaryDetail().getMobilenumber());
					patientInfoCollection.getPrimaryDetail().setName(request.getPrimaryDetail().getName());
					patientInfoCollection.getPrimaryDetail().setAge(request.getPrimaryDetail().getAge());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getPrimaryDetail();
				} else {
					PrimaryDetail primaryDetail = new PrimaryDetail();
					
					//primaryDetail.setName(request.getPrimaryDetail().getName());
					//primaryDetail.setGender(request.getPrimaryDetail().getGender());
					//primaryDetail.setMobilenumber(request.getPrimaryDetail().getMobilenumber());
					//primaryDetail.setDateOfBirth(request.getPrimaryDetail().getDateOfBirth());
					//primaryDetail.setAge(request.getPrimaryDetail().getAge());
					patientInfoCollection.setPrimaryDetail(primaryDetail);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getPrimaryDetail();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public PrimaryDetail getPrimaryDetail(String request) {

		PrimaryDetail response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getPrimaryDetail() != null) {

					response = patientInfoCollection.getPrimaryDetail();
				} else {
					PrimaryDetail primaryDetail = new PrimaryDetail();
					patientInfoCollection.setPrimaryDetail(primaryDetail);
					response = patientInfoCollection.getPrimaryDetail();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Sleep updateSleep(PatientInfo request) {

		Sleep response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getSleep() != null) {

					
					patientInfoCollection.getSleep().setSleepFrom(request.getSleep().getSleepFrom());
					patientInfoCollection.getSleep().setSleepTo(request.getSleep().getSleepTo());
					patientInfoCollection.getSleep().setSleepWhen(request.getSleep().getSleepWhen());
					patientInfoCollection.getSleep().setDuration(request.getSleep().getDuration());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getSleep();
				} else {
					Sleep sleep = new Sleep();
					
					//sleep.setSleepFrom(request.getSleep().getSleepFrom());
					//sleep.setSleepTo(request.getSleep().getSleepTo());
					//sleep.setSleepWhen(request.getSleep().getSleepWhen());
					//sleep.setDuration(request.getSleep().getDuration());
					patientInfoCollection.setSleep(sleep);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getSleep();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Sleep getSleep(String request) {

		Sleep response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getSleep() != null) {

					response = patientInfoCollection.getSleep();
				} else {
					Sleep sleep = new Sleep();
					patientInfoCollection.setSleep(sleep);
					response = patientInfoCollection.getSleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public TvUsage updateTvUsage(PatientInfo request) {

		TvUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getTvUsage() != null) {

					patientInfoCollection.getTvUsage().setHoursperday(request.getTvUsage().getHoursperday());
					patientInfoCollection.getTvUsage().setTv_in_bedroom(request.getTvUsage().isTv_in_bedroom());
					patientInfoCollection.getTvUsage().setWatchFrom(request.getTvUsage().getWatchFrom());
					patientInfoCollection.getTvUsage().setWatchTo(request.getTvUsage().getWatchTo());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


					response = patientInfoCollection.getTvUsage();
				} else {
					TvUsage tvUsage = new TvUsage();
					//tvUsage.setHoursperday(request.getTvUsage().getHoursperday());
					//tvUsage.setTv_in_bedroom(request.getTvUsage().isTv_in_bedroom());
					//tvUsage.setWatchFrom(request.getTvUsage().getWatchFrom());
					//tvUsage.setWatchTo(request.getTvUsage().getWatchTo());
					patientInfoCollection.setTvUsage(tvUsage);
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					response = patientInfoCollection.getTvUsage();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public TvUsage getTvUsage(String request) {

		TvUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getTvUsage() != null) {

					response = patientInfoCollection.getTvUsage();
				} else {
					TvUsage tvUsage = new TvUsage();
					patientInfoCollection.setTvUsage(tvUsage);
					response = patientInfoCollection.getTvUsage();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public WorkHistory updateWorkHistory(PatientInfo request) {

		WorkHistory response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else if (patientInfoCollection.getWorkHistory() != null) {

				patientInfoCollection.getWorkHistory().setProfession(request.getWorkHistory().getProfession());
				patientInfoCollection.getWorkHistory().setOffDays(request.getWorkHistory().getOffDays());
				patientInfoCollection.getWorkHistory().setWorkFrom(request.getWorkHistory().getWorkFrom());
				patientInfoCollection.getWorkHistory().setWorkTo(request.getWorkHistory().getWorkTo());
				patientInfoCollection = patientInfoRepository.save(patientInfoCollection);


				response = patientInfoCollection.getWorkHistory();
			} else {
				WorkHistory workHistory = new WorkHistory();
				//workHistory.setProfession(request.getWorkHistory().getProfession());
				//workHistory.setOffDays(request.getWorkHistory().getOffDays());
				//workHistory.setWorkFrom(request.getWorkHistory().getWorkFrom());
				//workHistory.setWorkTo(request.getWorkHistory().getWorkTo());
				patientInfoCollection.setWorkHistory(workHistory);
				patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

				response = patientInfoCollection.getWorkHistory();

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	public WorkHistory getWorkHistory(String request) {

		WorkHistory response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getWorkHistory() != null) {

					response = patientInfoCollection.getWorkHistory();
				} else {
					WorkHistory workHistory = new WorkHistory();
					patientInfoCollection.setWorkHistory(workHistory);
					response = patientInfoCollection.getWorkHistory();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;

	}

	public Boolean deletePatient(PatientInfo request) {

		Boolean response = false;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository
					.getBypatientId(new ObjectId(request.getPatientId()));
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				patientInfoCollection.setIsPatientDiscarded(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}


	@Override
	public PatientInfo findById(String patientInfoId) {

		PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(patientInfoId));
		if (patientInfoCollection == null || patientInfoCollection.getIsPatientDiscarded() == true) {
			logger.warning("Patient not found");
			throw new BusinessException(ServiceError.NotFound, "Patient not found");
		} else {
			PatientInfo patientInfo = new PatientInfo();
			BeanUtil.map(patientInfoCollection, patientInfo);
			return patientInfo;
		}
	}
	
	@Override
	public List<PatientInfo> findAll(String doctorId) {

		List<PatientInfoCollection> patientInfoCollection = patientInfoRepository.find(new ObjectId(doctorId));
		if (patientInfoCollection == null ) {
			logger.warning("Patient not found");
			throw new BusinessException(ServiceError.NotFound, "Patient not found");
		} else {
			List<PatientInfo> patientInfo =  new ArrayList<PatientInfo>();
			BeanUtil.map(patientInfoCollection, patientInfo);
			return patientInfo;
		}
	}

}
