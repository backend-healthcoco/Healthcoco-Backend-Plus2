package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.Date;

import com.dpdocter.enums.MealType;

public class Meal {
	
	private MealType mealtype;
	private Date mealtime;
	private ArrayList<String> mealcontent = new ArrayList<String>();
	
	
	
	public MealType getMealtype() {
		return mealtype;
	}
	public void setMealtype(MealType mealtype) {
		this.mealtype = mealtype;
	}
	
	public Date getMealtime() {
		return mealtime;
	}
	
	public void setMealtime(Date mealtime) {
		this.mealtime = mealtime;
	}
	
	public ArrayList<String> getMealcontent() {
		return mealcontent;
	}
	public void setMealcontent(ArrayList<String> mealcontent) {
		this.mealcontent = mealcontent;
	}
	
	@Override
	public String toString() {
		return "Meal [mealtype=" + mealtype + ", mealtime=" + mealtime + ", mealcontent=" + mealcontent + "]";
	}
	

	
	
}
