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
import com.api.utils.EncodeUrlResponse;
import com.api.utils.ErrorHandler;
import com.api.utils.UrlEncodeDecodeJSON;


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
			ErrorHandler err = new ErrorHandler();
			err.setErrMsg("Problem Encoding Url.");
			return Response.ok(err).build();
		}
		String fullEncodedUrl = "http://localhost:8080/URL_Shortner/" + encodedUrl.substring(5, 12);
		EncodeUrlResponse sendResponse = checkEntryInDatabase(url);
		
		if(sendResponse != null) {
			return Response.ok(sendResponse).build();
		}
		else{
			boolean status = setEntryInDatabase(url, fullEncodedUrl);
			if(status) {
				EncodeUrlResponse sendResp = new EncodeUrlResponse();
				sendResp.setEncodedUrl(fullEncodedUrl);
				return Response.ok(sendResp).build();
			}
			else {
				ErrorHandler err = new ErrorHandler();
				err.setErrMsg("DB Error has occured.");
				return Response.ok(err).build();
			}
		}
		
	}
	
	private synchronized boolean setEntryInDatabase(final String url, final String encodedUrl) {
		boolean queryStatus = false;
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
			queryStatus = true;
			return queryStatus;
		}catch (Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
			queryStatus = false;
			return queryStatus;
		}
	}
	
	
	private synchronized EncodeUrlResponse checkEntryInDatabase(final String Url) {
		final String CHECK_ENTRY_QUERY = "SELECT * FROM url_info WHERE url = ?;";
		try {
			DatabaseConnector dbconn = new DatabaseConnector();
			Connection conn = dbconn.getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(CHECK_ENTRY_QUERY);
			preparedStmt.setString(1, Url);
			ResultSet rs = preparedStmt.executeQuery();
			if (!rs.next()) {
				return null;
			}else{
				do{
					//Get encoded url form MySql
					//Lets not update accessed_on time and hit counter value and created on value
					EncodeUrlResponse encodeUrlResponse = new EncodeUrlResponse();
					encodeUrlResponse.setEncodedUrl(rs.getString("short_url"));
					return encodeUrlResponse;
				  }while (rs.next());
			}
			
		}catch (Exception e) {
			System.out.println("Got Exception!");
			System.err.println(e.getMessage());
		}
		return null;
		
	}
}