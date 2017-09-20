package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FoodReminder;
import com.dpdocter.beans.MedicineReminder;
import com.dpdocter.beans.WalkReminder;
import com.dpdocter.beans.WaterReminder;
import com.dpdocter.beans.WorkoutReminder;

@Document(collection = "user_reminders_cl")
public class UserRemindersCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private ObjectId userId;
	
	@Field
	private WaterReminder waterReminder;

	@Field
	private FoodReminder foodReminder;

	@Field
	private MedicineReminder medicineReminder;
	
	@Field
	private WorkoutReminder workoutReminder;

	@Field
	private WalkReminder walkReminder;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
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
		return "UserRemindersCollection [id=" + id + ", userId=" + userId + ", waterReminder=" + waterReminder
				+ ", foodReminder=" + foodReminder + ", medicineReminder=" + medicineReminder + ", workoutReminder="
				+ workoutReminder + ", walkReminder=" + walkReminder + "]";
	}

}
