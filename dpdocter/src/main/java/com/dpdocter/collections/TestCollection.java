//package com.dpdocter.collections;
//
//import org.bson.types.ObjectId;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import com.bol.secure.Encrypted;
//
//@Document(collection = "test_cl")
//public class TestCollection {
//
//	@Id
//	private ObjectId id;
//
//	@Field
//	@Encrypted
//	private String accNumber;
//
//	public ObjectId getId() {
//		return id;
//	}
//
//	public void setId(ObjectId id) {
//		this.id = id;
//	}
//
//	public String getAccNumber() {
//		return accNumber;
//	}
//
//	public void setAccNumber(String accNumber) {
//		this.accNumber = accNumber;
//	}
//
//	@Override
//	public String toString() {
//		return "TestCollection [id=" + id + ", accNumber=" + accNumber + "]";
//	}
//	
//	
//}
