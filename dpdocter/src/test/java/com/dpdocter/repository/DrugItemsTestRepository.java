package com.dpdocter.repository;

import java.util.Date;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DrugItemsTestRepository {

    private DBCollection collection;

    public DrugItemsTestRepository(DBCollection collection) {
	this.collection = collection;
    }

    public JSONObject add(String drug) throws JSONException {
	DBObject object = (DBObject) JSON.parse(drug);
	collection.insert(object);
	DBObject found = collection.find(object) != null ? collection.find(object).one() : null;
	if (found == null)
	    return null;
	else
	    return new JSONObject(JSON.serialize(found));
    }

    public JSONObject findBy(String doctorId, String hospitalId, String locationId, Date createdTime, boolean discarded) throws JSONException {
	BasicDBObject query = new BasicDBObject();

	if (doctorId != null && hospitalId != null && locationId != null) {
	    query.put("doctorId", doctorId);
	    query.put("hospitalId", hospitalId);
	    query.put("locationId", locationId);
	}
	if (!discarded)
	    query.put("deleted", discarded);

	if (createdTime != null)
	    query.put("createdTime", createdTime.getTime());

	DBObject found = collection.findById(query);
	if (found == null)
	    return null;
	else
	    return new JSONObject(JSON.serialize(found));
    }

    public JSONObject findBy(String field, String value) throws JSONException {
	BasicDBObject query = new BasicDBObject();
	query.put(field, value);
	DBObject found = collection.findById(query);
	if (found == null)
	    return null;
	else
	    return new JSONObject(JSON.serialize(found));
    }
}
