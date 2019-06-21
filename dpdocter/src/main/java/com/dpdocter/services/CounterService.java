package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CaloriesCounter;
import com.dpdocter.beans.ExerciseCounter;
import com.dpdocter.beans.MealCounter;
import com.dpdocter.beans.WaterCounter;
import com.dpdocter.beans.WaterCounterSetting;
import com.dpdocter.beans.WeightCounter;
import com.dpdocter.beans.WeightCounterSetting;

public interface CounterService {

	public WeightCounter deleteWeightCounter(String trackerId, Boolean discarded);

	public List<WaterCounter> getWaterCounters(int page, int size, String userId, String fromDate, String toDate);

	public WaterCounter getWaterCounterById(String trackerId);

	public WaterCounter deleteWaterCounter(String trackerId, Boolean discarded);

	public WaterCounterSetting addEditWaterCounterSetting(WaterCounterSetting request);

	public WaterCounterSetting getWaterCounterSetting(String userId);

	public WeightCounterSetting addEditWeightCounterSetting(WeightCounterSetting request);

	public WeightCounter getWeightCounterById(String trackerId);

	public List<WeightCounter> getWeightCounters(int page, int size, String userId, String fromDate, String toDate);

	public WeightCounter addEditWeightCounter(WeightCounter request);

	public WeightCounterSetting getWeightCounterSetting(String userId);

	public WaterCounter addEditWaterCounter(WaterCounter request);

	public MealCounter deleteMealCounter(String trackerId, Boolean discarded);

	public MealCounter getMealCounterById(String trackerId);

	public List<MealCounter> getMealCounters(int page, int size, String userId, String fromDate, String toDate,
			String mealtime);

	public MealCounter addEditMealCounter(MealCounter request);

	public ExerciseCounter deleteExerciseCounter(String trackerId, Boolean discarded);

	public ExerciseCounter getExerciseCounterById(String trackerId);

	public List<ExerciseCounter> getExerciseCounters(int page, int size, String userId, String fromDate, String toDate);

	public ExerciseCounter addEditExerciseCounter(ExerciseCounter request);

	public CaloriesCounter deleteColariesCounter(String counterId, Boolean discarded);

	public CaloriesCounter getCaloriesCounterById(String counterId);

	public List<CaloriesCounter> getCaloriesCounters(int page, int size, String userId, String fromDate, String toDate);

	public CaloriesCounter addEditCaloriesCounter(CaloriesCounter request);

}
