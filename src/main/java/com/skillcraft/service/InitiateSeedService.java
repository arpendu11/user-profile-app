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
        logger.info("Indexing the fake users");
        seedRediSearch.createSearchableIndexUsers();
        logger.info("Priming the suggestions");
        seedRediSearch.primeSuggestions();
    }
}
