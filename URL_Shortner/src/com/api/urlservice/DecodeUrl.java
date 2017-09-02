package com.api.urlservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.api.dao.DatabaseConnector;
import com.api.dao.RedisDatabaseConnector;
import com.api.utils.UrlDecoderJSON;
import com.api.utils.UrlEncodeDecodeJSON;

import redis.clients.jedis.Jedis;

@Path("/admin")
public class DecodeUrl {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response decodeUrlService(UrlEncodeDecodeJSON postData){
		String url = postData.getURL();
		UrlDecoderJSON urlInfoResponse = new UrlDecoderJSON();
		urlInfoResponse.setShortUrl(url);
		//Check in Redis for Entry
		String originalUrl = chekRedisForEntry(url);
		
		if(originalUrl != null) {
			// count always remain 10 ----Problem
			urlInfoResponse.setUrl(originalUrl);
			urlInfoResponse.setShortUrl(url);
			urlInfoResponse.setEntryPresent(true);
			urlInfoResponse.setFetchedVia("Redis");
			return Response.ok(urlInfoResponse).build();
		}
		
		//Now check MySql
		urlInfoResponse = searchUrlEntry(urlInfoResponse);
		
		if(!urlInfoResponse.isEntryPresent()) {
			//if no Entry in Redis and MySql
			return Response.status(404).entity("URL not found.").build();
		}
		else {
			//Do caching in Redis if count > 10
			if(urlInfoResponse.getCount() > 10) {
				RedisDatabaseConnector rc = new RedisDatabaseConnector();
				Jedis rd = rc.getRedisConnection();
				rd.set(urlInfoResponse.getShortUrl(), urlInfoResponse.getUrl());
				rd.disconnect();
			}
			//Update Count
			updateHitCounter(url, urlInfoResponse.getCount());
			return Response.ok(urlInfoResponse).build();
		}
		
		// update hit count
		// add entry in redis
		
	
	}
	
	private String chekRedisForEntry(final String url) {
		RedisDatabaseConnector rc = new RedisDatabaseConnector();
		Jedis rd = rc.getRedisConnection();
		String originalUrl = rd.get(url);
		rd.disconnect();
		return originalUrl;
	}
	
	private synchronized UrlDecoderJSON searchUrlEntry(UrlDecoderJSON urlInfoData) {
		
		String SEARCH_URL_QUERY = "SELECT * FROM url_info WHERE short_url = ?;";

		try {
			DatabaseConnector dbconn = new DatabaseConnector();
			Connection conn = dbconn.getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(SEARCH_URL_QUERY);
			preparedStmt.setString(1, urlInfoData.getShortUrl());
			ResultSet rs = preparedStmt.executeQuery();
			if (!rs.next()) {
				urlInfoData.setEntryPresent(false);
				return urlInfoData;
			}
			else{
				do{
					 urlInfoData.setUrl(rs.getString("url"));
					 urlInfoData.setCreatedOn(rs.getTimestamp("created_on"));
					 urlInfoData.setAccessedOn(rs.getTimestamp("accessed_on"));
					 urlInfoData.setCount(rs.getInt("count"));
					 urlInfoData.setEntryPresent(true);
					 urlInfoData.setFetchedVia("MySQL");
				  }while (rs.next());
			}
			return urlInfoData;
		}catch (Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
		}
		urlInfoData.setEntryPresent(false);
		return urlInfoData;
	}
	
	private synchronized void updateHitCounter(final String url, int hitCount) {
		final String UPDATE_HIT_COUNTER = "UPDATE url_info SET COUNT = ?, accessed_on = ?  WHERE short_url = ?;";
		try {
			DatabaseConnector dbconn = new DatabaseConnector();
			Connection conn = dbconn.getConnection();
			Timestamp accessedOn = new Timestamp(System.currentTimeMillis());
			PreparedStatement preparedStmt = conn.prepareStatement(UPDATE_HIT_COUNTER);
			preparedStmt.setInt(1, hitCount+1);
			preparedStmt.setTimestamp(2, accessedOn);
			preparedStmt.setString(3, url);
			preparedStmt.execute();
			conn.close();
		}catch (Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
		}
	}

}
