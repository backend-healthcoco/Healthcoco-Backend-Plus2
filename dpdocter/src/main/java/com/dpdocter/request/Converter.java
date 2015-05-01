package com.dpdocter.request;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Referrence;
import com.dpdocter.webservices.PathProxy;

public class Converter {

	public static String ObjectToJSON(Object value) {
		ObjectMapper objectMapper = new ObjectMapper();
		String JSONResult = "";
		try {
			JSONResult = objectMapper.writeValueAsString(value);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JSONResult;
	}

	public static void main(String[] args) {
		String JSONResult = ObjectToJSON(new Referrence());

		System.out.println(JSONResult);
	}
}
