package com.api.urlservice;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.api.dao.DatabaseConnector;

@Path("/")
public class RedirectUrlService {
	@GET
	public Response redirectShortUrl(@Context UriInfo uriInfo) {
		String shortUrl = uriInfo.getRequestUri().toString();
		String redirectToUrl =  searchUrlEntry(shortUrl);
		if(redirectToUrl != null) {
			 URI uri = UriBuilder.fromUri(redirectToUrl).build();
			 return Response.seeOther(uri).build();
		}
		
		return Response.status(404).build();
	}


private synchronized String searchUrlEntry(String shortUrl) {
	
	String SEARCH_URL_QUERY = "SELECT * FROM url_info WHERE short_url = ?;";

	try {
		DatabaseConnector dbconn = new DatabaseConnector();
		Connection conn = dbconn.getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(SEARCH_URL_QUERY);
		preparedStmt.setString(1, shortUrl);
		ResultSet rs = preparedStmt.executeQuery();
		if (!rs.next()) {
			return null;
		}
		else{
			do{
				 String redirectUrl = rs.getString("url");
				 return redirectUrl;
			  }while (rs.next());
		}
	}catch (Exception e) {
		System.out.println("Got Exception!");
		System.err.println(e.getMessage());
		}
	
	return null;
	
	}
}
