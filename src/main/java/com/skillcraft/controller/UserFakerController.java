package com.skillcraft.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.skillcraft.model.User;

import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/random")
public class UserFakerController {
	
	@Autowired
	private Environment env;
	
	private static UserFakerController instance = null;
	
	public static UserFakerController getInstance() {
		if (instance == null) {
			synchronized (UserFakerController.class) {
				instance = new UserFakerController();
			}
		}
		return instance;
	}
	
	@GetMapping("/users")
	public List<User> getRandomUsers() throws ParseException {
		String host = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : env.getProperty("spring.redis.host");
		Faker faker = new Faker();
		List<User> userCollection = new ArrayList<User>();
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
		final Date dateObjFrom = sdf.parse("2001-01-01T01:37:56");
		final Date dateObjTo = sdf.parse("2020-03-31T01:37:56");
		
		for (int i = 0; i < 500; i++) {
			User user = new User(faker.idNumber().ssnValid(),
					faker.name().username(),
					faker.name().firstName(),
					faker.name().lastName(),
					faker.job().title(),
					faker.company().name(),
					faker.address().cityName(),
					faker.address().country(),
					faker.internet().emailAddress(),
					faker.internet().url(),
					faker.avatar().image(),
//					"https://source.unsplash.com/random/200x200?person&sig=" + faker.random().nextInt(1, 1000),
					faker.company().catchPhrase(),
					faker.internet().password(),
					faker.date().between(dateObjFrom, dateObjTo).toString());
			userCollection.add(user);
		}
		Jedis jedis = new Jedis(host);
		String json = new Gson().toJson(userCollection);
		jedis.del("all");
		jedis.set("all", json);
		if(jedis != null && jedis.isConnected()) {
			jedis.close();
		}
		
		return userCollection;		
		
	}

}
