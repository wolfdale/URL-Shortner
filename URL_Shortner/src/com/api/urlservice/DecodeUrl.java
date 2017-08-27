package com.api.urlservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.api.dao.DatabaseConnector;

@Path("/decode")
public class DecodeUrl {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response decodeUrlService(UrlEncodeDecodeJSON postData){
		String url = postData.getURL();
		UrlDecoderJSON urlInfoResponse = new UrlDecoderJSON();
		urlInfoResponse.setUrl(url);
		urlInfoResponse = searchUrlEntry(urlInfoResponse);
		if(!urlInfoResponse.isEntryPresent()) {
			return Response.status(404).entity(postData).build();
		}
		else {
			return Response.ok(urlInfoResponse).build();
		}
		
	}
	
	private synchronized UrlDecoderJSON searchUrlEntry(UrlDecoderJSON urlInfoData) {
		
		String SEARCH_URL_QUERY = "SELECT * FROM url_info WHERE url = ?;";

		try {
			DatabaseConnector dbconn = new DatabaseConnector();
			Connection conn = dbconn.getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(SEARCH_URL_QUERY);
			preparedStmt.setString(1, urlInfoData.getUrl());
			ResultSet rs = preparedStmt.executeQuery();
			if (!rs.next()) {
				urlInfoData.setEntryPresent(false);
				return urlInfoData;
			}
			else{
				do{
					 urlInfoData.setShortUrl(rs.getString("short_url"));
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
	
	

}
