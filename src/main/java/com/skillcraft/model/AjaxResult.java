package com.skillcraft.model;

import lombok.Getter;

@Getter
public class AjaxResult {

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
    private double score;

    private AjaxResult(Builder builder) {
    	id = builder.id;
    	userName = builder.userName;
    	firstName = builder.firstName;
    	lastName = builder.lastName;
    	designation = builder.designation;
    	company = builder.company;
    	city = builder.city;
    	country = builder.country;
    	email = builder.email;
    	website = builder.website;
    	photo = builder.photo;
    	info = builder.info;
        score = builder.score;
        
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(AjaxResult copy) {
        Builder builder = new Builder();
        builder.id = copy.getId();
        builder.userName = copy.getUserName();
        builder.firstName = copy.getFirstName();
        builder.lastName = copy.getLastName();
        builder.designation = copy.getDesignation();
        builder.company = copy.getCompany();
        builder.city = copy.getCity();
        builder.country = copy.getCountry();
        builder.email = copy.getEmail();
        builder.website = copy.getWebsite();
        builder.photo = copy.getPhoto();
        builder.info = copy.getInfo();
        builder.score = copy.getScore();
        
        return builder;
    }


    /**
     * {@code AjaxResult} builder static inner class.
     */
    public static final class Builder {
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
        private double score;

        private Builder() {
        }

        public Builder withId(String val) {
            id = val;
            return this;
        }
        
        public Builder withUserName(String val) {
        	userName = val;
            return this;
        }

        public Builder withFirstName(String val) {
        	firstName = val;
            return this;
        }

        public Builder withLastName(String val) {
        	lastName = val;
            return this;
        }
        
        public Builder withDesignation(String val) {
        	designation = val;
            return this;
        }
        
        public Builder withCompany(String val) {
        	company = val;
            return this;
        }
        
        public Builder withCity(String val) {
        	city = val;
            return this;
        }
        
        public Builder withCountry(String val) {
        	country = val;
            return this;
        }

        public Builder withEmail(String val) {
        	email = val;
            return this;
        }
        
        public Builder withPhoto(String val) {
        	photo = val;
            return this;
        }
        
        public Builder withInfo(String val) {
        	info = val;
            return this;
        }
        
        public Builder withWebsite(String val) {
        	website = val;
            return this;
        }

        public Builder withScore(double val) {
            score = val;
            return this;
        }

        public AjaxResult build() {
            return new AjaxResult(this);
        }
    }
}
