package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.RankingCount;

public interface RankingAlgorithmsServices {

	List<RankingCount> getDoctorsRankingCount(int page, int size);

	void calculateRankingOfResources();

}
