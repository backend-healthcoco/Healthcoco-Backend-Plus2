package com.dpdocter.test;

import static org.junit.Assert.*;

import java.net.UnknownHostException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dpdocter.repository.DrugTypeTestRepository;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import common.util.web.JacksonUtil;

public class DrugTypeTests {
	DB db;
    DrugTypeTestRepository repository;
    
    @Before
    public void setUp() throws UnknownHostException, MongoException
    {
        @SuppressWarnings("deprecation")
		Mongo connection = new Mongo("127.0.0.1");
        db = connection.getDB("dpdocter_dp");
        DBCollection collection = db.getCollection("drug_type_cl");
        repository = new DrugTypeTestRepository(collection);
    }

    @Test
    public void testForSave() {
        String user = null;
		try {
			
			DrugTypeAddEditRequest request=new DrugTypeAddEditRequest();
			
			//EQUALS
			request.setType("SYRUP");
			user = JacksonUtil.obj2Json(request);
			JSONObject added = repository.add(user);
			JSONObject retrieved = repository.findBy("type", "SYRUP");
	       
	        assertEquals("true",retrieved.toString(),added.toString());
		
			//Not EQUALS
//			request.setType("CAPSULE");
//			user = JacksonUtil.obj2Json(request);
//			JSONObject added = repository.add(user);
//			JSONObject retrieved = repository.findBy("type", "SYRUP");
//	       
//			assertEquals("true",retrieved.toString(),added.toString());
//	   
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
    
    @After
    public void tearDown() {
        db.dropDatabase();
        db = null;
    }

}
