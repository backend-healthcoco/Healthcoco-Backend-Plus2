package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CaloriesCounter;
import com.dpdocter.beans.ExerciseCounter;
import com.dpdocter.beans.MealCounter;
import com.dpdocter.beans.WaterCounter;
import com.dpdocter.beans.WaterCounterSetting;
import com.dpdocter.beans.WeightCounter;
import com.dpdocter.beans.WeightCounterSetting;
import com.dpdocter.collections.CaloriesCounterCollection;
import com.dpdocter.collections.ExerciseCounterCollection;
import com.dpdocter.collections.MealCounterCollection;
import com.dpdocter.collections.WaterCounterCollection;
import com.dpdocter.collections.WaterCounterSettingCollection;
import com.dpdocter.collections.WeightCounterCollection;
import com.dpdocter.collections.WeightCounterSettingCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CaloriesCounterRepository;
import com.dpdocter.repository.ExerciseCounterRepository;
import com.dpdocter.repository.MealCounterRepository;
import com.dpdocter.repository.WaterCounterRepository;
import com.dpdocter.repository.WaterCounterSettingRepository;
import com.dpdocter.repository.WeightCounterRepository;
import com.dpdocter.repository.WeightCounterSettingRepository;
import com.dpdocter.services.CounterService;

import common.util.web.DPDoctorUtils;

@Service
public class CounterServiceImpl implements CounterService {

	private static Logger logger = Logger.getLogger(CounterServiceImpl.class.getName());

	@Autowired
	private WaterCounterRepository waterCounterRepository;

	@Autowired
	private WaterCounterSettingRepository waterCounterSettingRepository;

	@Autowired
	private WeightCounterRepository weightCounterRepository;

	@Autowired
	private MealCounterRepository mealCounterRepository;

	@Autowired
	private ExerciseCounterRepository exerciseCounterRepository;

	@Autowired
	private WeightCounterSettingRepository weightCounterSettingRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private CaloriesCounterRepository caloriesCounterRepository;

	@Override
	public WaterCounter addEditWaterCounter(WaterCounter request) {
		WaterCounter response = null;
		try {
			DateTime fromTime = null;
			DateTime toTime = null;
			WaterCounterCollection counterCollection = null;

			fromTime = DPDoctorUtils.getStartTime(request.getDate());
			toTime = DPDoctorUtils.getEndTime(request.getDate());
			counterCollection = waterCounterRepository.findByUserIdAndDateGreaterThanAndDiscardedIsFalse(
					new ObjectId(request.getUserId()), fromTime, toTime);
			if (counterCollection != null) {
				request.setId(counterCollection.getId().toString());
				request.setUpdatedTime(new Date());
				counterCollection = new WaterCounterCollection();
				BeanUtil.map(request, counterCollection);
			} else {
				counterCollection = new WaterCounterCollection();
				BeanUtil.map(request, counterCollection);
				counterCollection.setCreatedTime(new Date());
			}

			counterCollection = waterCounterRepository.save(counterCollection);

			if (counterCollection != null) {
				response = new WaterCounter();
				BeanUtil.map(counterCollection, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Water counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Water counter : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public List<WaterCounter> getWaterCounters(int page, int size, String userId, String fromDate, String toDate) {
		List<WaterCounter> response = null;
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

			criteria.and("date").gte(from).lte(to).and("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));

			}
			AggregationResults<WaterCounter> aggregationResults = mongoTemplate.aggregate(aggregation,
					WaterCounterCollection.class, WaterCounter.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Water counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Water counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WaterCounter getWaterCounterById(String counterId) {
		WaterCounter response = null;
		try {
			WaterCounterCollection counterCollection = waterCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection != null) {
				response = new WaterCounter();
				BeanUtil.map(counterCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Water counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Water counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WaterCounter deleteWaterCounter(String counterId, Boolean discarded) {
		WaterCounter response = null;
		try {
			WaterCounterCollection trackerCollection = waterCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (trackerCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, " water tracker not found with Id ");
			}
			trackerCollection.setDiscarded(discarded);
			trackerCollection.setUpdatedTime(new Date());
			trackerCollection = waterCounterRepository.save(trackerCollection);
			response = new WaterCounter();
			BeanUtil.map(trackerCollection, response);
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(e + "Error while deleting Water counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Water counter : " + e.getCause().getMessage());

		}
		return response;

	}

	@Override
	public WaterCounterSetting addEditWaterCounterSetting(WaterCounterSetting request) {
		WaterCounterSetting response = null;
		try {
			WaterCounterSettingCollection counterSettingCollection = null;

			counterSettingCollection = waterCounterSettingRepository.findByUserId(new ObjectId(request.getUserId()));
			if (counterSettingCollection != null) {
				request.setId(counterSettingCollection.getId().toString());
				request.setCreatedTime(counterSettingCollection.getCreatedTime());
				counterSettingCollection = new WaterCounterSettingCollection();
				BeanUtil.map(request, counterSettingCollection);
			} else {
				counterSettingCollection = new WaterCounterSettingCollection();
				BeanUtil.map(request, counterSettingCollection);
				counterSettingCollection.setCreatedTime(new Date());

			}
			counterSettingCollection = waterCounterSettingRepository.save(counterSettingCollection);
			response = new WaterCounterSetting();
			BeanUtil.map(counterSettingCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Water counter setting : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Water counter setting : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WaterCounterSetting getWaterCounterSetting(String userId) {
		WaterCounterSetting response = null;
		try {
			WaterCounterSettingCollection counterSettingCollection = waterCounterSettingRepository
					.findByUserId(new ObjectId(userId));
			if (counterSettingCollection != null) {
				response = new WaterCounterSetting();
				BeanUtil.map(counterSettingCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Water counter setting : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Water counter setting : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WeightCounterSetting addEditWeightCounterSetting(WeightCounterSetting request) {
		WeightCounterSetting response = null;
		try {
			WeightCounterSettingCollection counterSettingCollection = null;

			counterSettingCollection = weightCounterSettingRepository.findByUserId(new ObjectId(request.getUserId()));
			if (counterSettingCollection != null) {
				request.setCreatedTime(counterSettingCollection.getCreatedTime());

				request.setId(counterSettingCollection.getId().toString());

				counterSettingCollection = new WeightCounterSettingCollection();
				BeanUtil.map(request, counterSettingCollection);
			} else {
				counterSettingCollection = new WeightCounterSettingCollection();
				BeanUtil.map(request, counterSettingCollection);
				counterSettingCollection.setCreatedTime(new Date());
			}

			counterSettingCollection = weightCounterSettingRepository.save(counterSettingCollection);
			response = new WeightCounterSetting();
			BeanUtil.map(counterSettingCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Weight Counter setting : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Weight Counter setting : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WeightCounterSetting getWeightCounterSetting(String userId) {
		WeightCounterSetting response = null;
		try {
			WeightCounterSettingCollection trackerSettingCollection = weightCounterSettingRepository
					.findByUserId(new ObjectId(userId));

			if (trackerSettingCollection != null) {
				response = new WeightCounterSetting();
				BeanUtil.map(trackerSettingCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Weight counter setting : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Weight counter setting : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public WeightCounter addEditWeightCounter(WeightCounter request) {
		WeightCounter response = null;
		try {
			DateTime fromTime = null;
			DateTime toTime = null;
			WeightCounterCollection counterCollection = null;

			fromTime = DPDoctorUtils.getStartTime(request.getDate());
			toTime = DPDoctorUtils.getEndTime(request.getDate());
			counterCollection = weightCounterRepository.findByUserIdAndDateGreaterThanAndDiscardedIsFalse(
					new ObjectId(request.getUserId()), fromTime, toTime);
			if (counterCollection != null) {
				request.setId(counterCollection.getId().toString());
				request.setCreatedTime(counterCollection.getCreatedTime());
				BeanUtil.map(request, counterCollection);

			} else {
				counterCollection = new WeightCounterCollection();
				BeanUtil.map(request, counterCollection);
				counterCollection.setCreatedTime(new Date());

			}

			counterCollection = weightCounterRepository.save(counterCollection);
			response = new WeightCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Weight Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Weight Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<WeightCounter> getWeightCounters(int page, int size, String userId, String fromDate, String toDate) {
		List<WeightCounter> response = null;
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
			criteria.and("date").gte(from).lte(to).and("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));

			}
			AggregationResults<WeightCounter> aggregationResults = mongoTemplate.aggregate(aggregation,
					WeightCounterCollection.class, WeightCounter.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Weight Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Weight Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WeightCounter getWeightCounterById(String counterId) {
		WeightCounter response = null;
		try {

			WeightCounterCollection counterCollection = weightCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection != null) {
				response = new WeightCounter();
				BeanUtil.map(counterCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Weight Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Weight Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public WeightCounter deleteWeightCounter(String trackerId, Boolean discarded) {
		WeightCounter response = null;
		try {
			WeightCounterCollection counterCollection = weightCounterRepository.findById(new ObjectId(trackerId))
					.orElse(null);
			if (counterCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, " Weight Counter not found with Id ");
			}
			counterCollection.setDiscarded(discarded);
			counterCollection = weightCounterRepository.save(counterCollection);
			response = new WeightCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Weight Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Weight Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public MealCounter addEditMealCounter(MealCounter request) {
		MealCounter response = null;
		try {
			DateTime fromTime = null;
			DateTime toTime = null;
			MealCounterCollection counterCollection = null;

			fromTime = DPDoctorUtils.getStartTime(request.getDate());
			toTime = DPDoctorUtils.getEndTime(request.getDate());
			counterCollection = mealCounterRepository.findByUserIdDateBetweenAndMealTimeAndDiscardedIsFalse(
					new ObjectId(request.getUserId()), fromTime, toTime, request.getMealTime().toString());
			if (counterCollection != null) {
				request.setId(counterCollection.getId().toString());
				request.setCreatedTime(counterCollection.getCreatedTime());
				counterCollection = new MealCounterCollection();
				BeanUtil.map(request, counterCollection);

			} else {
				counterCollection = new MealCounterCollection();
				BeanUtil.map(request, counterCollection);
				counterCollection.setCreatedTime(new Date());

			}

			counterCollection = mealCounterRepository.save(counterCollection);
			response = new MealCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Meal Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Meal Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<MealCounter> getMealCounters(int page, int size, String userId, String fromDate, String toDate,
			String mealTime) {
		List<MealCounter> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(mealTime)) {
				criteria.and("mealTime").is(mealTime.toUpperCase());
			}
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
			criteria.and("date").gte(from).lte(to).and("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));

			}
			AggregationResults<MealCounter> aggregationResults = mongoTemplate.aggregate(aggregation,
					MealCounterCollection.class, MealCounter.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Meal Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Meal Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public MealCounter getMealCounterById(String counterId) {
		MealCounter response = null;
		try {

			MealCounterCollection counterCollection = mealCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection != null) {
				response = new MealCounter();
				BeanUtil.map(counterCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Meal Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Meal Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public MealCounter deleteMealCounter(String counterId, Boolean discarded) {
		MealCounter response = null;
		try {
			MealCounterCollection counterCollection = mealCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Meal Counter not found with Id ");
			}
			counterCollection.setUpdatedTime(new Date());
			counterCollection.setDiscarded(discarded);
			counterCollection = mealCounterRepository.save(counterCollection);
			response = new MealCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Meal Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Meal Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public ExerciseCounter addEditExerciseCounter(ExerciseCounter request) {
		ExerciseCounter response = null;
		try {
			DateTime fromTime = null;
			DateTime toTime = null;
			ExerciseCounterCollection counterCollection = null;

			fromTime = DPDoctorUtils.getStartTime(request.getDate());
			toTime = DPDoctorUtils.getEndTime(request.getDate());
			counterCollection = exerciseCounterRepository
					.findByUserIdAndDateBetweenAndDiscardedIsFalse(new ObjectId(request.getUserId()), fromTime, toTime);
			if (counterCollection != null) {
				request.setId(counterCollection.getId().toString());
				request.setCreatedTime(counterCollection.getCreatedTime());
				counterCollection = new ExerciseCounterCollection();
				BeanUtil.map(request, counterCollection);

			} else {
				counterCollection = new ExerciseCounterCollection();
				BeanUtil.map(request, counterCollection);
				counterCollection.setCreatedTime(new Date());

			}

			counterCollection = exerciseCounterRepository.save(counterCollection);
			response = new ExerciseCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Exercise Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Exercise Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<ExerciseCounter> getExerciseCounters(int page, int size, String userId, String fromDate,
			String toDate) {
		List<ExerciseCounter> response = null;
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
			criteria.and("date").gte(from).lte(to).and("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));

			}
			AggregationResults<ExerciseCounter> aggregationResults = mongoTemplate.aggregate(aggregation,
					ExerciseCounterCollection.class, ExerciseCounter.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Exercise Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Exercise Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public ExerciseCounter getExerciseCounterById(String counterId) {
		ExerciseCounter response = null;
		try {

			ExerciseCounterCollection counterCollection = exerciseCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection != null) {
				response = new ExerciseCounter();
				BeanUtil.map(counterCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Exercise Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Exercise Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public ExerciseCounter deleteExerciseCounter(String trackerId, Boolean discarded) {
		ExerciseCounter response = null;
		try {
			ExerciseCounterCollection counterCollection = exerciseCounterRepository.findById(new ObjectId(trackerId))
					.orElse(null);
			if (counterCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "counter not found with Id ");
			}
			counterCollection.setUpdatedTime(new Date());
			counterCollection.setDiscarded(discarded);
			counterCollection = exerciseCounterRepository.save(counterCollection);
			response = new ExerciseCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Exercise Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Exercise Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public CaloriesCounter addEditCaloriesCounter(CaloriesCounter request) {
		CaloriesCounter response = null;
		try {
			DateTime fromTime = null;
			DateTime toTime = null;
			CaloriesCounterCollection counterCollection = null;

			fromTime = DPDoctorUtils.getStartTime(request.getDate());
			toTime = DPDoctorUtils.getEndTime(request.getDate());
			counterCollection = caloriesCounterRepository
					.findByUserIdAndDateBetweenAndDiscardedIsFalse(new ObjectId(request.getUserId()), fromTime, toTime);
			if (counterCollection != null) {
				request.setId(counterCollection.getId().toString());
				request.setCreatedTime(counterCollection.getCreatedTime());
				counterCollection = new CaloriesCounterCollection();
				BeanUtil.map(request, counterCollection);

			} else {
				counterCollection = new CaloriesCounterCollection();
				BeanUtil.map(request, counterCollection);
				counterCollection.setCreatedTime(new Date());

			}

			counterCollection = caloriesCounterRepository.save(counterCollection);
			response = new CaloriesCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Calorie Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Calorie Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<CaloriesCounter> getCaloriesCounters(int page, int size, String userId, String fromDate,
			String toDate) {
		List<CaloriesCounter> response = null;
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
			criteria.and("date").gte(from).lte(to).and("userId").is(new ObjectId(userId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "date")));

			}
			AggregationResults<CaloriesCounter> aggregationResults = mongoTemplate.aggregate(aggregation,
					CaloriesCounterCollection.class, CaloriesCounter.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Calorie Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Calorie Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public CaloriesCounter getCaloriesCounterById(String counterId) {
		CaloriesCounter response = null;
		try {
			CaloriesCounterCollection counterCollection = caloriesCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection != null) {
				response = new CaloriesCounter();
				BeanUtil.map(counterCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Calorie Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Calorie Counter : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public CaloriesCounter deleteColariesCounter(String counterId, Boolean discarded) {
		CaloriesCounter response = null;
		try {
			CaloriesCounterCollection counterCollection = caloriesCounterRepository.findById(new ObjectId(counterId))
					.orElse(null);
			if (counterCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Calorie counter not found with Id ");
			}
			counterCollection.setUpdatedTime(new Date());
			counterCollection.setDiscarded(discarded);
			counterCollection = caloriesCounterRepository.save(counterCollection);
			response = new CaloriesCounter();
			BeanUtil.map(counterCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Calorie Counter : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Calorie Counter : " + e.getCause().getMessage());

		}
		return response;
	}
}
