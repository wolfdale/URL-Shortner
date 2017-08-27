package com.api.dao;

import redis.clients.jedis.Jedis;

public class RedisDatabaseConnector {
	Jedis redis = new Jedis("localhost");
	
}
