package com.dpdocter.repository;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DrugTypeTestRepository {

	private DBCollection collection;

    public DrugTypeTestRepository(DBCollection collection) {
        this.collection = collection;
    }
    
    public JSONObject add(String user) throws JSONException  {
        DBObject object = (DBObject) JSON.parse(user);
        collection.insert(object);
        return new JSONObject(JSON.serialize(collection.find(object).one()));
    }

    public JSONObject findBy(String field, Object value) throws JSONException {
        BasicDBObject query = new BasicDBObject();
        query.put(field, value);
        DBObject found = collection.findOne(query);
        return new JSONObject(JSON.serialize(found));
    }
}
