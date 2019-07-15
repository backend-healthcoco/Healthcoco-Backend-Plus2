package com.dpdocter.services.impl;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.dpdocter.response.URLShortnerResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import common.util.web.JacksonUtil;

public class URLShortnerServiceImpl {

	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		try {
			HttpResponse<JsonNode> response = Unirest.post("https://url-shortener-service.p.rapidapi.com/shorten")
					.header("X-RapidAPI-Host", "url-shortener-service.p.rapidapi.com")
					.header("X-RapidAPI-Key", "75cbae716dmsh3e15bc0ab75221ep189f87jsnf7a1406117b4")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.field("url", "https://www.healthcoco.com/nagpur/dentist")
					.asJson();
		URLShortnerResponse shortnerResponse = JacksonUtil.json2Object(response.getBody().toString(), URLShortnerResponse.class);
		System.out.println(shortnerResponse.getResult_url());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
