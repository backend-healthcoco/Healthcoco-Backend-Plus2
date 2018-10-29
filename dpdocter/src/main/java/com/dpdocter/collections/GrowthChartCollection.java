package com.dpdocter.collections;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.BloodPressure;

public class GrowthChartCollection extends GenericCollection{

	private ObjectId id;
	private ObjectId patientId;
	private ObjectId doctorId;
	private ObjectId locationId;
	private ObjectId hospitalId;
	private Integer height;
	private Double weight;
	private Double bmi;
	private Integer skullCircumference;
	private String progress;
	private Age age;
	private String temperature;
	private BloodPressure bloodPressure;
	private String bloodSugarF;
	private String bloodSugarPP;
	private String bmd;
	
}
