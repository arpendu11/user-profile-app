package com.skillcraft.service;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skillcraft.controller.UserFakerController;
import com.skillcraft.model.User;

import io.redisearch.Client;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.SearchResult;
import io.redisearch.Suggestion;
import io.redisearch.client.AddOptions;
import io.redisearch.client.SuggestionOptions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

@Service
public class RediSearchService {
	Logger logger = LoggerFactory.getLogger(RediSearchService.class);
	
	@Autowired
	private Environment env;

	private Client client;
	private Schema schema = new Schema()
			.addTextField("id", 0.5)
			.addTextField("username", 1.0)
			.addTextField("firstName", 1.0)
			.addTextField("lastName", 1.0)
			.addTextField("designation", 0.5)
			.addTextField("company", 0.5)
			.addTextField("city", 0.5)
			.addTextField("country", 0.5)
			.addTextField("email", 1.0)
			.addTextField("website", 1.0)
			.addTextField("photo", 0.2)
			.addTextField("info", 0.2)
			.addTextField("password", 0.2)
			.addTextField("createdAt", 0.2);
	
	public List<User> getFakeUsers() throws ParseException {
		String host = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : env.getProperty("spring.redis.host");
		Jedis jedis = new Jedis(host);		
		String redisget = jedis.get("all");
		Type collectionType = new TypeToken<List<User>>(){}.getType();
		List<User> fakeUsers = new Gson().fromJson(redisget, collectionType);
		if(jedis != null && jedis.isConnected()) {
			jedis.close();
		}
		return fakeUsers;
	}
	
	public Client getClient() {
		String host = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : env.getProperty("spring.redis.host");
		int port = System.getenv("REDIS_PORT") != null ? Integer.parseInt(System.getenv("REDIS_PORT")) : Integer.parseInt(env.getProperty("spring.redis.port"));
		if (client == null) {
			client = new io.redisearch.client.Client("users", host, port, 7200, 10);
		}
		return client;
	}
	
	public boolean checkConnection() {
        boolean flag = true;
        try {
            getClient().getInfo();
        } catch (JedisConnectionException je) {
            flag = false;
            logger.error(je.getMessage());
        } catch (JedisDataException jex) {
        	logger.error(jex.getMessage());
        }
        return flag;
    }
	
	public void createSearchableIndexUsers() throws ParseException {
        if (checkConnection()) {
            // clean up from other examples but leave it for the rest of the example
        	logger.info("Dropping the old index.");
            getClient().dropIndex(true);
            logger.info("Creating new index.");
            UserFakerController.getInstance().getRandomUsers();
            getClient().createIndex(schema, io.redisearch.client.Client.IndexOptions.defaultOptions());
            seedDocuments().forEach(doc -> {
                getClient().addDocument(doc, new AddOptions());
            });
            logger.info("Done with indexing of seed Users.");
        } else {
            throw new RuntimeException("Unable to load index of the users :(");
        }
    }
	
	public void createSearchableIndexUser(User user) throws ParseException {
        if (checkConnection()) {
        	Map<String, Object> fields = new HashMap<>();
            fields.put("username", user.getUserName());
            fields.put("firstName", user.getFirstName());
            fields.put("lastName", user.getLastName());
            fields.put("designation", user.getDesignation());
            fields.put("company", user.getCompany());
            fields.put("city", user.getCity());
            fields.put("country", user.getCountry());
            fields.put("email", user.getEmail());
            fields.put("website", user.getWebsite());
            fields.put("photo", user.getPhoto());
            fields.put("info", user.getInfo());
            fields.put("password", user.getPassword());
            fields.put("createdAt", user.getCreatedAt());
            Document doc = new Document(user.getId(), fields);
            logger.info("Adding a document to existing index.");
            getClient().addDocument(doc, new AddOptions());
            logger.info("Done with indexing of document.");
        } else {
            throw new RuntimeException("Unable to load index of the users :(");
        }
    }
	
	private List<Document> seedDocuments() throws ParseException {
		final List<Document> documents = new ArrayList<>();
		getFakeUsers().forEach(user -> {
            Map<String, Object> fields = new HashMap<>();
            fields.put("username", user.getUserName());
            fields.put("firstName", user.getFirstName());
            fields.put("lastName", user.getLastName());
            fields.put("designation", user.getDesignation());
            fields.put("company", user.getCompany());
            fields.put("city", user.getCity());
            fields.put("country", user.getCountry());
            fields.put("email", user.getEmail());
            fields.put("website", user.getWebsite());
            fields.put("photo", user.getPhoto());
            fields.put("info", user.getInfo());
            fields.put("password", user.getPassword());
            fields.put("createdAt", user.getCreatedAt());
            documents.add(new Document(user.getId(), fields));
        });
        return documents;
    }
	
	public void primeSuggestions() throws ParseException {
		logger.info("Started with creating suggestions of seed Users.");
        createSuggestionSet().forEach(suggestion -> {
            getClient().addSuggestion(suggestion, false);
        });
        logger.info("Done with creating suggestions of seed Users.");
    }
	
	private Set<Suggestion> createSuggestionSet() throws ParseException {
        final Set<Suggestion> suggestions = new HashSet<>();
        getFakeUsers().forEach(user -> {
        	List<String> suggestionListHeavyWeight = new ArrayList<String>();
        	suggestionListHeavyWeight.add(user.getUserName());
        	String[] firstName = StringUtils.split(user.getFirstName());
        	Collections.addAll(suggestionListHeavyWeight, firstName);
        	String[] lastName = StringUtils.split(user.getLastName());
        	Collections.addAll(suggestionListHeavyWeight, lastName);
        	suggestionListHeavyWeight.add(user.getEmail());
        	suggestionListHeavyWeight.add(user.getWebsite());
            for (int i = 0; i < suggestionListHeavyWeight.size(); i++) {
                // cleanse from things like colons
                if (StringUtils.isAsciiPrintable(suggestionListHeavyWeight.get(i))
                		&& StringUtils.isNotBlank(suggestionListHeavyWeight.get(i))) {
                	suggestions.add(Suggestion.builder().str(suggestionListHeavyWeight.get(i)).score(0.5).build());
                }
            }
            
            List<String> suggestionListLowWeight = new ArrayList<String>();
            suggestionListLowWeight.add(user.getId());
            String[] designation = StringUtils.split(user.getId());
            String[] company = StringUtils.split(user.getCompany());
            String[] city = StringUtils.split(user.getCity());
            String[] country = StringUtils.split(user.getCountry());
            String[] info = StringUtils.split(user.getInfo());
            Collections.addAll(suggestionListLowWeight, designation);
            Collections.addAll(suggestionListLowWeight, company);
            Collections.addAll(suggestionListLowWeight, city);
            Collections.addAll(suggestionListLowWeight, country);
            Collections.addAll(suggestionListLowWeight, info);
            for (int i = 0; i < suggestionListHeavyWeight.size(); i++) {
                // cleanse from things like colons
                if (StringUtils.isAsciiPrintable(suggestionListLowWeight.get(i))
                		&& StringUtils.isNotBlank(suggestionListLowWeight.get(i))) {
                	suggestions.add(Suggestion.builder().str(suggestionListLowWeight.get(i)).score(0.2).build());
                }
            }
        });
        return suggestions;
    }

	public void createSuggestion(User user) throws ParseException {
        final Set<Suggestion> suggestions = new HashSet<>();
        List<String> suggestionListHeavyWeight = new ArrayList<String>();
    	suggestionListHeavyWeight.add(user.getUserName());
    	String[] firstName = StringUtils.split(user.getFirstName());
    	Collections.addAll(suggestionListHeavyWeight, firstName);
    	String[] lastName = StringUtils.split(user.getLastName());
    	Collections.addAll(suggestionListHeavyWeight, lastName);
    	suggestionListHeavyWeight.add(user.getEmail());
    	suggestionListHeavyWeight.add(user.getWebsite());
        for (int i = 0; i < suggestionListHeavyWeight.size(); i++) {
            // cleanse from things like colons
            if (StringUtils.isAsciiPrintable(suggestionListHeavyWeight.get(i))
            		&& StringUtils.isNotBlank(suggestionListHeavyWeight.get(i))) {
            	suggestions.add(Suggestion.builder().str(suggestionListHeavyWeight.get(i)).score(0.5).build());
            }
        }
        
        List<String> suggestionListLowWeight = new ArrayList<String>();
        suggestionListLowWeight.add(user.getId());
        String[] designation = StringUtils.split(user.getId());
        String[] company = StringUtils.split(user.getCompany());
        String[] city = StringUtils.split(user.getCity());
        String[] country = StringUtils.split(user.getCountry());
        String[] info = StringUtils.split(user.getInfo());
        Collections.addAll(suggestionListLowWeight, designation);
        Collections.addAll(suggestionListLowWeight, company);
        Collections.addAll(suggestionListLowWeight, city);
        Collections.addAll(suggestionListLowWeight, country);
        Collections.addAll(suggestionListLowWeight, info);
        for (int i = 0; i < suggestionListHeavyWeight.size(); i++) {
            // cleanse from things like colons
            if (StringUtils.isAsciiPrintable(suggestionListLowWeight.get(i))
            		&& StringUtils.isNotBlank(suggestionListLowWeight.get(i))) {
            	suggestions.add(Suggestion.builder().str(suggestionListLowWeight.get(i)).score(0.2).build());
            }
        }
        logger.info("Started with creating suggestions of new user.");
        suggestions.forEach(suggestion -> {
            getClient().addSuggestion(suggestion, false);
        });
        logger.info("Done with creating suggestions of new User.");
    }
	
	public SearchResult search(String term) {
		Query query = new Query(term).limit(0, 500).setWithScores();
		return getClient().search(query);
	}

	public List<Suggestion> getSuggestions(String partial) {
		List<Suggestion> suggestions = getClient().getSuggestion(partial, SuggestionOptions.builder().fuzzy().build());
		suggestions.stream().map(suggestion -> suggestion.getString()).collect(Collectors.toList());
		return suggestions;
	}
}
