package com.api.dao;

import redis.clients.jedis.Jedis;

public class RedisDatabaseConnector {
	
	public Jedis getRedisConnection() {
		final String HOST = "localhost"; 
		try {
			Jedis redis = new Jedis(HOST);
			//NON cluster Mode
			redis.select(2);
			return redis;
		}
		catch(Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
		}
		return null;
	}
}
