package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NmcHcm;

public interface NmcHcmServices {

	NmcHcm addDetails(NmcHcm request);
	
	List<NmcHcm> getDetails(int page, int size, String searchTerm, String type);

	Integer countNmcData(String type, String searchTerm,Boolean discarded);
}
