package com.api.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	public Connection getConnection() throws Exception{
		try {
			String connectionURL = "jdbc:mysql://localhost:3306/URL_DATASTORE";
			Connection conn = null;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(connectionURL,"root","wolf");
			return conn;
		}catch(SQLException e) {
			throw e;
		}
		catch(Exception e) {
			throw e;
		}
	}

}
