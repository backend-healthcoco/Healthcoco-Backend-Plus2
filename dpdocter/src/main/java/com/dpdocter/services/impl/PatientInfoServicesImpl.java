package com.repository;



import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bean.Activity;
import com.bean.Community;
import com.bean.FoodPreferences;
import com.bean.GeographicalArea;
import com.bean.LaptopUsage;
import com.bean.Meal;
import com.bean.MobilePhoneUsage;
import com.bean.PatientInfo;
import com.bean.PrimaryDetail;
import com.bean.Sleep;
import com.bean.TvUsage;
import com.bean.WorkHistory;
import com.collection.PatientInfoCollection;

import exceptions.BusinessException;
import exceptions.ServiceError;
import reflections.BeanUtil;


@Service
public class PatientInfoServicesImpl implements PatientInfoServices {

	private static Logger logger = Logger.getLogger(PatientInfoServicesImpl.class.getName());

	@Autowired
	private PatientInfoRepository patientInfoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;


	@Transactional
	public Activity updateActivity(PatientInfo request) {
		Activity response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
					
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getActivity() != null) {

					patientInfoCollection.getActivity().setLifestyle(request.getActivity().getLifestyle());
					patientInfoCollection.getActivity().setExerciseType(request.getActivity().getExerciseType());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getActivity();
				}
				else {
					Activity activity = new Activity();
					activity.setLifestyle(request.getActivity().getLifestyle());
					activity.setExerciseType(request.getActivity().getExerciseType());
					patientInfoCollection.setActivity(activity);     
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public Activity getActivity(PatientInfo request) {

		Activity response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getActivity() != null) {

					response = patientInfoCollection.getActivity();
				}
				else {
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

	public Community updateCommunity(PatientInfo request) 
	{	
		Community response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getCommunity() != null) {

					patientInfoCollection.getCommunity().setCommunity(request.getCommunity().getCommunity());
					
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getCommunity();
				}
				else {
					Community community = new Community();
					community.setCommunity(request.getCommunity().getCommunity());
					patientInfoCollection.setCommunity(community);     
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public Community getCommunity(PatientInfo request) {

		Community response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getCommunity() != null) {

					response = patientInfoCollection.getCommunity();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getFoodPreferences() != null) {

					patientInfoCollection.getFoodPreferences().setFoodPref(request.getFoodPreferences().getFoodPref());
					
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getFoodPreferences();
				}
				else {
					FoodPreferences foodpref = new FoodPreferences();
					foodpref.setFoodPref(request.getFoodPreferences().getFoodPref());
					patientInfoCollection.setFoodPreferences(foodpref);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public FoodPreferences getFoodPreferences(PatientInfo request) {

		FoodPreferences response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getFoodPreferences() != null) {

					response = patientInfoCollection.getFoodPreferences();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getGeographicalArea() != null) {

					patientInfoCollection.getGeographicalArea().setGeographicalArea(request.getGeographicalArea().getGeographicalArea());
					
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getGeographicalArea();
				}
				else {
					GeographicalArea geographicalArea = new GeographicalArea();
					geographicalArea.setGeographicalArea(request.getGeographicalArea().getGeographicalArea());
					patientInfoCollection.setGeographicalArea(geographicalArea);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public GeographicalArea getGeographicalArea(PatientInfo request) {

		GeographicalArea response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getGeographicalArea() != null) {

					response = patientInfoCollection.getGeographicalArea();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getLaptopUsage() != null) {

					patientInfoCollection.getLaptopUsage().setLaptopInBedroom(request.getLaptopUsage().getLaptopInBedroom());
					patientInfoCollection.getLaptopUsage().setHoursperday(request.getLaptopUsage().getHoursperday());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getLaptopUsage();
				}
				else {
					LaptopUsage laptopUsage = new LaptopUsage();
					laptopUsage.setHoursperday(request.getLaptopUsage().getHoursperday());
					laptopUsage.setLaptopInBedroom(request.getLaptopUsage().getLaptopInBedroom());
					patientInfoCollection.setLaptopUsage(laptopUsage);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public LaptopUsage getLaptopUsage(PatientInfo request) {
		LaptopUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getLaptopUsage() != null) {

					response = patientInfoCollection.getLaptopUsage();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMeal() != null) {

					patientInfoCollection.getMeal().setMealcontent(request.getMeal().getMealcontent());
					patientInfoCollection.getMeal().setMealtime(request.getMeal().getMealtime());
					patientInfoCollection.getMeal().setMealtype(request.getMeal().getMealtype());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getMeal();
				}
				else {
					Meal meal = new Meal();
					meal.setMealcontent(request.getMeal().getMealcontent());
					meal.setMealtime(request.getMeal().getMealtime());
					meal.setMealtype(request.getMeal().getMealtype());
					patientInfoCollection.setMeal(meal);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public Meal getMeal(PatientInfo request) {

		Meal response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMeal() != null) {

					response = patientInfoCollection.getMeal();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMobilePhoneUsage() != null) {

					patientInfoCollection.getMobilePhoneUsage().setHoursperday(request.getMobilePhoneUsage().getHoursperday());
					patientInfoCollection.getMobilePhoneUsage().setTalkFrom(request.getMobilePhoneUsage().getTalkFrom());
					patientInfoCollection.getMobilePhoneUsage().setTalkTo(request.getMobilePhoneUsage().getTalkTo());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getMobilePhoneUsage();
				}
				else {
					MobilePhoneUsage mobilePhoneUsage = new MobilePhoneUsage();
					mobilePhoneUsage.setHoursperday(request.getMobilePhoneUsage().getHoursperday());
					mobilePhoneUsage.setTalkFrom(request.getMobilePhoneUsage().getTalkFrom());
					mobilePhoneUsage.setTalkTo(request.getMobilePhoneUsage().getTalkTo());
					patientInfoCollection.setMobilePhoneUsage(mobilePhoneUsage);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public MobilePhoneUsage getMobilePhoneUsage(PatientInfo request) {

		MobilePhoneUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getMobilePhoneUsage() != null) {

					response = patientInfoCollection.getMobilePhoneUsage();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getPrimaryDetail() != null) {

					patientInfoCollection.getPrimaryDetail().setDateOfBirth(request.getPrimaryDetail().getDateOfBirth());
					patientInfoCollection.getPrimaryDetail().setGender(request.getPrimaryDetail().getGender());
					patientInfoCollection.getPrimaryDetail().setMobilenumber(request.getPrimaryDetail().getMobilenumber());
					patientInfoCollection.getPrimaryDetail().setName(request.getPrimaryDetail().getName());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getPrimaryDetail();
				}
				else {
					PrimaryDetail primaryDetail = new PrimaryDetail();
					primaryDetail.setName(request.getPrimaryDetail().getName());
					primaryDetail.setGender(request.getPrimaryDetail().getGender());
					primaryDetail.setMobilenumber(request.getPrimaryDetail().getMobilenumber());
					primaryDetail.setDateOfBirth(request.getPrimaryDetail().getDateOfBirth());
					patientInfoCollection.setPrimaryDetail(primaryDetail);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public PrimaryDetail getPrimaryDetail(PatientInfo request) {

		PrimaryDetail response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getPrimaryDetail() != null) {

					response = patientInfoCollection.getPrimaryDetail();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getSleep() != null) {

					patientInfoCollection.getSleep().setDuration(request.getSleep().getDuration());
					patientInfoCollection.getSleep().setSleepFrom(request.getSleep().getSleepFrom());
					patientInfoCollection.getSleep().setSleepTo(request.getSleep().getSleepTo());
					patientInfoCollection.getSleep().setsleepWhen(request.getSleep().getsleepWhen());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getSleep();
				}
				else {
					Sleep sleep = new Sleep();
					sleep.setDuration(request.getSleep().getDuration());
					sleep.setSleepFrom(request.getSleep().getSleepFrom());
					sleep.setSleepTo(request.getSleep().getSleepTo());
					sleep.setsleepWhen(request.getSleep().getsleepWhen());
					patientInfoCollection.setSleep(sleep);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public Sleep getSleep(PatientInfo request) {

		Sleep response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getSleep() != null) {

					response = patientInfoCollection.getSleep();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
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

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getTvUsage();
				}
				else {
					TvUsage tvUsage = new TvUsage();
					tvUsage.setHoursperday(request.getTvUsage().getHoursperday());
					tvUsage.setTv_in_bedroom(request.getTvUsage().isTv_in_bedroom());
					tvUsage.setWatchFrom(request.getTvUsage().getWatchFrom());
					tvUsage.setWatchTo(request.getTvUsage().getWatchTo());
					patientInfoCollection.setTvUsage(tvUsage);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public TvUsage getTvUsage(PatientInfo request) {


		TvUsage response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getTvUsage() != null) {

					response = patientInfoCollection.getTvUsage();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getWorkHistory() != null) {

					patientInfoCollection.getWorkHistory().setProfession(request.getWorkHistory().getProfession());
					patientInfoCollection.getWorkHistory().setOffDays(request.getWorkHistory().getOffDays());
					patientInfoCollection.getWorkHistory().setWorkFrom(request.getWorkHistory().getWorkFrom());
					patientInfoCollection.getWorkHistory().setWorkTo(request.getWorkHistory().getWorkTo());
					patientInfoCollection = patientInfoRepository.save(patientInfoCollection);

					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
					response = patientInfoCollection.getWorkHistory();
				}
				else {
					WorkHistory workHistory = new WorkHistory();
					workHistory.setProfession(request.getWorkHistory().getProfession());
					workHistory.setOffDays(request.getWorkHistory().getOffDays());
					workHistory.setWorkFrom(request.getWorkHistory().getWorkFrom());
					workHistory.setWorkTo(request.getWorkHistory().getWorkTo());
					patientInfoCollection.setWorkHistory(workHistory);
					patientInfoCollection = patientInfoRepository.insert(patientInfoCollection);
					PatientInfoCollection patientInfoCollection1 = new PatientInfoCollection();
					BeanUtil.map(patientInfoCollection, patientInfoCollection1);
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

	public WorkHistory getWorkHistory(PatientInfo request) {

		WorkHistory response = null;
		try {
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
			if (patientInfoCollection == null) {
				logger.warning("Patient not found");
				throw new BusinessException(ServiceError.NotFound, "Patient not found");
			} else {
				if (patientInfoCollection.getWorkHistory() != null) {

					response = patientInfoCollection.getWorkHistory();
				}
				else {
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
			PatientInfoCollection patientInfoCollection = patientInfoRepository.getBypatientId(new ObjectId(request.getPatientId()) );
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



}
