package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Code;

@Document(collection = "generic_code_with_reaction_cl")
public class GenericCodeWithReactionCollection extends GenericCollection {

	@Id
	private String id;
	
	@Field
	private List<Code> codes;
	
	@Field
	private String reactionType;
	
	@Field
	private String explanation;

	public GenericCodeWithReactionCollection() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Code> getCodes() {
		return codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public String getReactionType() {
		return reactionType;
	}

	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public String toString() {
		return "GenericCodeWithReactionCollection [id=" + id + ", codes=" + codes + ", reactionType=" + reactionType
				+ ", explanation=" + explanation + "]";
	}
}
