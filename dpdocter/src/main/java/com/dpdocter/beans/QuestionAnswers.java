package com.dpdocter.beans;

import java.util.List;

public class QuestionAnswers {

	private String question;
	
	private List<String> answers;

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

	@Override
	public String toString() {
		return "QuestionAnswers [question=" + question + ", answers=" + answers + "]";
	}
}
