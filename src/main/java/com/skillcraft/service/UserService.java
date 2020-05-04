package com.skillcraft.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillcraft.model.AjaxAutoComplete;
import com.skillcraft.model.AjaxResult;
import com.skillcraft.model.AjaxResults;
import com.skillcraft.model.Suggestions;
import com.skillcraft.model.User;

import io.redisearch.SearchResult;

@Service
public class UserService {

	Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private RediSearchService searchService;
	
	public AjaxResults findByTerm(String term) {
		logger.info("Searching for the term: " + term);
		SearchResult searchResult = searchService.search(term);
		List<AjaxResult> results = searchResult.docs.stream().map(result ->
        	AjaxResult.newBuilder()
        		.withId(result.getId())
        		.withUserName((String) result.get("username"))
        		.withFirstName((String) result.get("firstName"))
        		.withLastName((String) result.get("lastName"))
        		.withDesignation((String) result.get("designation"))
        		.withCompany((String) result.get("company"))
        		.withCity((String) result.get("city"))
        		.withCountry((String) result.get("country"))
        		.withEmail((String) result.get("email"))
        		.withWebsite((String) result.get("website"))
        		.withPhoto((String) result.get("photo"))
        		.withInfo((String) result.get("info"))
                .withScore(result.getScore())
                .build()
				).collect(Collectors.toList());
		return new AjaxResults(searchResult.totalResults, results);
	}

	public Suggestions suggestTerm(String partialTerm) {
		return new Suggestions(searchService.getSuggestions(partialTerm).stream().map(suggestion -> 
				new AjaxAutoComplete(suggestion.getString(), suggestion.getScore() + ""))
				.collect(Collectors.toList()));
	}
	
	public boolean indexNewUser(User user) {
		try {
			searchService.createSearchableIndexUser(user);
			searchService.createSuggestion(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

}
