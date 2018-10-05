package com.dpdocter.beans;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import com.mongodb.DBObject;

public class CustomAggregationOperation implements AggregationOperation {
    private DBObject operation;

    public CustomAggregationOperation (DBObject operation) {
        this.operation = operation;
    }

	@Override
	public Document toDocument(AggregationOperationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

//    @Override
//    public DBObject toDBObject(AggregationOperationContext context) {
//        return context.getMappedObject(operation);
//    }
}
