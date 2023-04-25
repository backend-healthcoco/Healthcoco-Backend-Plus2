package com.dpdocter.response;

public class DrugInteractionResposne {

	private String text;

	private String reaction;

	private String explanation;

	public DrugInteractionResposne(String text, String reaction, String explanation) {
		super();
		this.text = text;
		this.reaction = reaction;
		this.explanation = explanation;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	@Override
	public String toString() {
		return "DrugInteractionResposne [text=" + text + ", reaction=" + reaction + ", explanation=" + explanation
				+ "]";
	}

}
