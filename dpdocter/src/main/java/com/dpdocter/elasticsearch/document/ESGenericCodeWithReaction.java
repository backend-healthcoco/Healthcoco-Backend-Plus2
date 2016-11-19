package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Document(indexName = "generic_codes_with_reaction_in", type = "generic_codes_with_reaction")
public class ESGenericCodeWithReaction {

	@Id
	private String id;
	
	@MultiField(mainField = @Field(type = FieldType.String))
	private List<String> codes;
	
	@Field(type = FieldType.String)
	private String reactionType;
	
	@Field(type = FieldType.String)
	private String explanation;

    @Field(type = FieldType.Date)
    private Date updatedTime = new Date();

	public ESGenericCodeWithReaction() {
		super();
	}

	public ESGenericCodeWithReaction(String id, List<String> codes, String reactionType, String explanation) {
		super();
		this.id = id;
		this.codes = codes;
		this.reactionType = reactionType;
		this.explanation = explanation;
	}

	public ESGenericCodeWithReaction(List<String> codes, String reactionType) {
		super();
		this.codes = codes;
		this.reactionType = reactionType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "ESGenericCodeWithReaction [id=" + id + ", codes=" + codes + ", reactionType=" + reactionType
				+ ", explanation=" + explanation + ", updatedTime=" + updatedTime + "]";
	}

}
