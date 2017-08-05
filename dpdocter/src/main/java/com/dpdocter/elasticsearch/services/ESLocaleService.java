package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.request.UserSearchRequest;

public interface ESLocaleService {
	
	boolean addLocale(ESUserLocaleDocument request);

	List<ESUserLocaleDocument> getLocale(UserSearchRequest userSearchRequest, Integer distance);

	Boolean updateStatus(String localeId, Boolean isOpen);
	
	public Boolean updateLocale(String localeId, LocaleCollection localeCollection);

}
