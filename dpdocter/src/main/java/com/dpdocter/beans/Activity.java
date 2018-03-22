package com.bean;

import java.util.Arrays;

import com.Enum.LifeStyle;

public class Activity {
	
	private LifeStyle lifestyle;
	private String ExerciseType[][]={{"Gym","false"} ,
			{"Yoga","false"} ,
			{"Walking","false"}  ,
			{"Running","false"}  , 
			{"Brisk Walk","false"}  ,
			{"Cycling","false"}  
			  ,{"Weight Lifting","false"} 
			  , {"Swimming","false"}};

	

	public LifeStyle getLifestyle() {
		return lifestyle;
	}

	public void setLifestyle(LifeStyle lifestyle) {
		this.lifestyle = lifestyle;
	}

	public String[][] getExerciseType() {
		return ExerciseType;
	}

	public void setExerciseType(String[][] exerciseType) {
		ExerciseType = exerciseType;
	}

	@Override
	public String toString() {
		return "Activity [lifestyle=" + lifestyle + ", ExerciseType=" + Arrays.toString(ExerciseType) + "]";
	}
	

}
