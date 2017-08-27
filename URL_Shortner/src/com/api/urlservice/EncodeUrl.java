package com.api.urlservice;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


@Path("/encode")
public class EncodeUrl {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response encodeUrlService(UrlEncodeDecodeJSON postData) {
		String url = postData.getURL();
		String encodedUrl = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(url.getBytes(),0,url.length());
			encodedUrl = new BigInteger(1,md.digest()).toString(16);
		}catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		}
		
		int hitCount = checkEntryInDatabase(url);
		
		if(hitCount>0) {
			updateHitCounter(url,hitCount);
		}
		else{
			setEntryInDatabase(url, encodedUrl.substring(5, 12));
		}
		return Response.ok(postData).build();
		
	}
	
	private synchronized void setEntryInDatabase(final String url, final String encodedUrl) {
		Timestamp createdOn = new Timestamp(System.currentTimeMillis());
		final String BUILD_ENTRY_SQL_QUERY = "INSERT INTO url_info (url,short_url,created_on,accessed_on,count)"
											 + "VALUES(?,?,?,?,?);";
	    
		try {
			DatabaseConnector dbconn = new DatabaseConnector();
			Connection conn = dbconn.getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(BUILD_ENTRY_SQL_QUERY);
			preparedStmt.setString(1, url);
			preparedStmt.setString(2, encodedUrl);
			preparedStmt.setTimestamp(3, createdOn);
			preparedStmt.setTimestamp(4, createdOn);
			preparedStmt.setInt(5, 1);
			preparedStmt.execute();
			conn.close();
		}catch (Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
		}
	}
	
	private synchronized int checkEntryInDatabase(final String Url) {
		final String CHECK_ENTRY_QUERY = "SELECT COUNT FROM url_info WHERE url = ?;";
		try {
			DatabaseConnector dbconn = new DatabaseConnector();
			Connection conn = dbconn.getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(CHECK_ENTRY_QUERY);
			preparedStmt.setString(1, Url);
			ResultSet rs = preparedStmt.executeQuery();
			if (!rs.next()) {
				return 0;
			}else{
				do{
					int hitCount = rs.getInt("count");
					return hitCount;
				  }while (rs.next());
			}
			
		}catch (Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
		}
		return 0;
		
	}
	
	private synchronized void updateHitCounter(final String url, int hitCount) {
		final String UPDATE_HIT_COUNTER = "UPDATE url_info SET COUNT = ?, accessed_on = ?  WHERE url = ?;";
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