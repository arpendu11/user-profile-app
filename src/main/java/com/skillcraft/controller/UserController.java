package com.skillcraft.controller;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skillcraft.model.AjaxResponseBody;
import com.skillcraft.model.AjaxResults;
import com.skillcraft.model.SearchCriteria;
import com.skillcraft.model.User;
import com.skillcraft.service.UserService;

import redis.clients.jedis.Jedis;

@Controller
@RequestMapping("/users")
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private Environment env;
	
	@Autowired
	UserFakerController fakerController;
	
	@Autowired
	UserService userService;
	
	@RequestMapping("/new")
	public String displayForm(Model model) {
		User aUser = new User();		
		model.addAttribute("user", aUser);
		return "users/new-user";		
	}
	
	@GetMapping("/all")
	public String displayUsers(Model model) throws ParseException {
		String host = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : env.getProperty("spring.redis.host");
		Jedis jedis = new Jedis(host);		
		String redisget = jedis.get("all");
		Type collectionType = new TypeToken<List<User>>(){}.getType();
		List<User> users = new Gson().fromJson(redisget, collectionType);
		if(jedis != null && jedis.isConnected()) {
			jedis.close();
		}
		model.addAttribute("users", users);
		return "users/list-users";
	}
	
	@PostMapping("/api/save")
	public String createUser(User user, Model model) throws ParseException {
		String host = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : env.getProperty("spring.redis.host");
		Faker faker = new Faker();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		user.setCreatedAt(sdf.format(date));
		user.setPhoto(faker.avatar().image());
		user.setId(faker.idNumber().ssnValid());
		Jedis jedis = new Jedis(host);		
		String redisget = jedis.get("all");
		Type collectionType = new TypeToken<List<User>>(){}.getType();
		List<User> users = new Gson().fromJson(redisget, collectionType);
		users.add(user);
		String json = new Gson().toJson(users);
		jedis.del("all");
		jedis.set("all", json);
		if(jedis != null && jedis.isConnected()) {
			jedis.close();
		}
		userService.indexNewUser(user);
		return "redirect:/users/all";
				
	}
	
	@PostMapping("/api/search")
	public ResponseEntity<?> getSearchResults(@Valid @RequestBody SearchCriteria search, Errors error) {
		String msg = "found";
		if (error.hasErrors()) {
			msg = error.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(","));
			logger.error("An error occured with input: " + msg);
			return ResponseEntity.badRequest().body(new AjaxResponseBody("error", null));
		}
		AjaxResults results = userService.findByTerm(search.getTerm());
		if (results.getTotal() < 1) {
			msg = "missed";
		}
		return ResponseEntity.ok(new AjaxResponseBody(msg, results));
	}
	
	@GetMapping("/api/partial")
	public ResponseEntity<?> getSuggestions(@RequestParam("partialTerm") String partialTerm) {
		return ResponseEntity.ok(userService.suggestTerm(partialTerm));
	}
}
