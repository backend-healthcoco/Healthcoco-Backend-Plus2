package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.UserSymptom;

public interface UserSymptomService {
 
	UserSymptom addEditUserSymptoms(UserSymptom request);
	 
	 List<UserSymptom> getUserSymptoms(int size, int page, Boolean discarded,String searchTerm);
	 public Integer countUserSymptom(Boolean discarded, String searchTerm);

}
