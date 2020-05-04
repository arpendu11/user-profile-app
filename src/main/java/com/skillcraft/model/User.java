package com.skillcraft.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

	private String id;
	private String userName;
	private String firstName;
	private String lastName;
	private String designation;
	private String company;
	private String city;
	private String country;
	private String email;
	private String website;
	private String photo;
	private String info;
	private String password;
	private String createdAt;
	
}
