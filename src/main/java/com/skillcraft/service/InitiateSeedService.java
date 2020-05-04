package com.skillcraft.service;

import java.text.ParseException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitiateSeedService {
	Logger logger = LoggerFactory.getLogger(InitiateSeedService.class);
	
	@Autowired
	RediSearchService seedRediSearch;

		@PostConstruct
	    private void initSeedForIndexing() throws ParseException {
			long startTime = System.nanoTime();
	        logger.info("Indexing the fake users");
	        seedRediSearch.createSearchableIndexUsers();
	        long indexTime   = System.nanoTime();
	        double seconds = (double)(indexTime - startTime)/1000000000.0;
	        logger.info("Completed indexing in " + seconds + " seconds!");
	        logger.info("Priming the suggestions");
	        seedRediSearch.primeSuggestions();
	        long suggestionTime   = System.nanoTime();
	        double suggestionSeconds = (double)(suggestionTime - indexTime)/1000000000.0;
	        logger.info("Completed suggestion building in " + suggestionSeconds + " seconds!");
	    }
}
