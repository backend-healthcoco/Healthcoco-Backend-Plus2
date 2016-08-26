package com.dpdocter.beans;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Duration {
    private String value;

    @Field(type = FieldType.Nested)
    private DrugDurationUnit durationUnit;

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public DrugDurationUnit getDurationUnit() {
	return durationUnit;
    }

    public void setDurationUnit(DrugDurationUnit durationUnit) {
	this.durationUnit = durationUnit;
    }

    @Override
    public String toString() {
	return "Duration [value=" + value + ", durationUnit=" + durationUnit + "]";
    }

}
