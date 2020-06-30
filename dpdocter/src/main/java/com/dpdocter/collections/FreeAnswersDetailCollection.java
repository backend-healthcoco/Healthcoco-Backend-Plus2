package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class FreeAnswersDetailCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId questionId;
	@Field
	private String nextStep;
	@Field
	private String answerDesc;
	@Field
	private String helpfulTips;
	@Field
	private Long time;
	@Field
	private Boolean isHelpful = false;
	@Field
	private String reasonForflag;
	@Field
	private Boolean isPrivateConsultationAllow = false;
	@Field
	private String reasonForNotHelpful;
	
	
	
}
