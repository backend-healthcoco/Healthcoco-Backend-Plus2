package com.dpdocter.response;

public class DrugInteractionResposne {

	private String text;
	
	private String reaction;

	public DrugInteractionResposne(String text, String reaction) {
		super();
		this.text = text;
		this.reaction = reaction;
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
		return "DrugInteractionResposne [text=" + text + ", reaction=" + reaction + "]";
	}
}
