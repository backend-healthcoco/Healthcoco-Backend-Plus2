package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class UserReminders extends GenericCollection{

	private String id;

	private String userId;
	
	private WaterReminder waterReminder;

	private FoodReminder foodReminder;

	private MedicineReminder medicineReminder;
	
	private WorkoutReminder workoutReminder;

	private WalkReminder walkReminder;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public WaterReminder getWaterReminder() {
		return waterReminder;
	}

	public void setWaterReminder(WaterReminder waterReminder) {
		this.waterReminder = waterReminder;
	}

	public FoodReminder getFoodReminder() {
		return foodReminder;
	}

	public void setFoodReminder(FoodReminder foodReminder) {
		this.foodReminder = foodReminder;
	}

	public MedicineReminder getMedicineReminder() {
		return medicineReminder;
	}

	public void setMedicineReminder(MedicineReminder medicineReminder) {
		this.medicineReminder = medicineReminder;
	}

	public WorkoutReminder getWorkoutReminder() {
		return workoutReminder;
	}

	public void setWorkoutReminder(WorkoutReminder workoutReminder) {
		this.workoutReminder = workoutReminder;
	}

	public WalkReminder getWalkReminder() {
		return walkReminder;
	}

	public void setWalkReminder(WalkReminder walkReminder) {
		this.walkReminder = walkReminder;
	}

	@Override
	public String toString() {
		return "UserReminders [id=" + id + ", userId=" + userId + ", waterReminder=" + waterReminder + ", foodReminder="
				+ foodReminder + ", medicineReminder=" + medicineReminder + ", workoutReminder=" + workoutReminder
				+ ", walkReminder=" + walkReminder + "]";
	}
}
