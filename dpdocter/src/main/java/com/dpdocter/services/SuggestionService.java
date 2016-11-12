package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Suggestion;

public interface SuggestionService {
	public List<Suggestion> getSuggestion(int page, int size, String userId, String suggetionType, String state,
			String searchTerm);

	public Suggestion AddEditSuggestion(Suggestion request);

}
