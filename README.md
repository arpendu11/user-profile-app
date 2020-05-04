# User Profile App

A simple indexed and searchable User Profile App using Redisearch as the datastore. This App allows a user to list all the users, add a new user as well as search for a user in one of the fastest mode. Usually search based indexing databases or engines are quite heavyweight and need lot of settings to be done.

I was looking for lightweight Data-structure which can solve the use-case of indexing documents and provide searchable capability. So I came across this [whitepaper](https://redislabs.com/docs/redisearch-a-high-performance-search-engine-as-a-redis-module/) and I was pretty impressed with their [benchmark and simple commands](https://redislabs.com/redis-enterprise/technology/redis-search/).  

With the help of Spring Boot, Thymeleaf and use of [JRedisearch](https://github.com/RediSearch/JRediSearch) library, I was able to create a simple yet high performant searching App.

## Instructions

You can follow the below mentioned simple steps to run this app

**Run Redisearch in docker:**

```docker run -d -p 6379:6379 redislabs/redisearch```

**Build this app using maven**

```mvn clean package```

**Build this app using docker:**

```docker build -t user-profile-app:latest .```

**Build this app using docker:**

``` docker run -d -p 9090:9090 -e REDIS_HOST={REDIS_HOST} -e REDIS_PORT={REDIS_PORT} -t user-profile-app:latest```
