package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.RankingCount;
import com.dpdocter.services.RankingAlgorithmsServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.RANKING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.RANKING_BASE_URL, description = "Endpoint for ranking")
public class RankingApi {

//private static Logger logger = LogManager.getLogger(RankingApi.class.getName());
	
	@Autowired
	RankingAlgorithmsServices rankingAlgorithmsServices;
	
	
	@GetMapping(value = PathProxy.RankingUrls.GET_DOCTORS_RANKING)
	@ApiOperation(value = PathProxy.RankingUrls.GET_DOCTORS_RANKING, notes = PathProxy.RankingUrls.GET_DOCTORS_RANKING)
	public Response<RankingCount> getDoctorsRankingCount(@RequestParam("page") long page, @RequestParam("size") int size){
		
		List<RankingCount> rankingCounts = rankingAlgorithmsServices.getDoctorsRankingCount(page, size);
		Response<RankingCount> response = new Response<RankingCount>();
		response.setDataList(rankingCounts);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.RankingUrls.CALCULATE_RANKING)
	@ApiOperation(value = PathProxy.RankingUrls.CALCULATE_RANKING, notes = PathProxy.RankingUrls.CALCULATE_RANKING)
	public Response<Boolean> calculateRankingOfResources(){
		
		rankingAlgorithmsServices.calculateRankingOfResources();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
}
