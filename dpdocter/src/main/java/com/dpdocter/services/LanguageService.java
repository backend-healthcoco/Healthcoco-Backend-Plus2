package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Language;

public interface LanguageService {

	public List<Language> getLanguages(int size, int page, Boolean discarded,String searchTerm);
	public Language getLanguage(String id);
	public Integer countLanguage(Boolean discarded, String searchTerm);
}
