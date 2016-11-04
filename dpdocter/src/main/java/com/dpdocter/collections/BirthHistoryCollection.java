package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.BirthType;
import com.dpdocter.enums.GestationType;
@Document(collection = "birth_history_cl")
public class BirthHistoryCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private String birthPlace;
	@Field
	private String obstetricianName;
	@Field
	private GestationType gestationType;
	@Field
	private Integer prematureWeeks;
	@Field
	private BirthType birthType;
	@Field
	private Integer height;
	@Field
	private Double weight;
	@Field
	private Integer birthOrder;
	@Field
	private String birthProblem;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;

	@Override
	public String toString() {
		return "BirthHistoryCollection [id=" + id + ", birthPlace=" + birthPlace + ", obstetricianName="
				+ obstetricianName + ", gestationType=" + gestationType + ", prematureWeeks=" + prematureWeeks
				+ ", birthType=" + birthType + ", height=" + height + ", weight=" + weight + ", birthOrder="
				+ birthOrder + ", birthProblem=" + birthProblem + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + "]";
	}

}
