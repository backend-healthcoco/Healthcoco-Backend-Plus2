package com.dpdocter.beans;

import java.util.List;

public class QuestionAnswers {

	private String question;
	
	private String questionType;
	
	private List<String> answers;

	private Boolean isNone = false;
	
	private Boolean isAnswerNone = false;
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Boolean getIsNone() {
		return isNone;
	}

	public void setIsNone(Boolean isNone) {
		this.isNone = isNone;
	}

	public Boolean getIsAnswerNone() {
		return isAnswerNone;
	}

	public void setIsAnswerNone(Boolean isAnswerNone) {
		this.isAnswerNone = isAnswerNone;
	}

	@Override
	public String toString() {
		return "QuestionAnswers [question=" + question + ", questionType=" + questionType + ", answers=" + answers
				+ ", isNone=" + isNone + ", isAnswerNone=" + isAnswerNone + "]";
	}
}
