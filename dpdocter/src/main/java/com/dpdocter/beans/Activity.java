package com.dpdocter.beans;

import java.util.Arrays;
import java.util.HashMap;

import com.dpdocter.enums.LifeStyle;

public class Activity {
	
	private LifeStyle lifestyle;

	private  ExerciseType exerciseType;

	public LifeStyle getLifestyle() {
		return lifestyle;
	}

	public void setLifestyle(LifeStyle lifestyle) {
		this.lifestyle = lifestyle;
	}

	public ExerciseType getExerciseType() {
		return exerciseType;
	}

	public void setExerciseType(ExerciseType exerciseType) {
		this.exerciseType = exerciseType;
	}

	@Override
	public String toString() {
		return "Activity [lifestyle=" + lifestyle + ", exerciseType=" + exerciseType + "]";
	}
	

}
