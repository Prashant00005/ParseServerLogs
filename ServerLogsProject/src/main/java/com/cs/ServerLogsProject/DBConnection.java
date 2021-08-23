package com.cs.ServerLogsProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

	static Connection con;
	static String dbUrl = "jdbc:hsqldb:file:db_data/csdatabase";

	public static Connection setupDB() {

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("In exception");
		}

		try {
			//Connecting to the database with default HSQLDB username and password
			con = DriverManager.getConnection(dbUrl, "SA", "");

			Statement stmt = con.createStatement();
			{
				
				//Id is considered primary key as only one entry is required for flagged events
				String sql = "CREATE TABLE IF NOT EXISTS LOGS" + "(id VARCHAR(255) not NULL, " + " duration BIGINT, "
						+ " type VARCHAR(255), " + " host INTEGER, " + " alert VARCHAR(255)," + " PRIMARY KEY ( id ))";

				stmt.executeUpdate(sql);
			}
			
			Statement stmt1 = con.createStatement();
			
			//If running the application again, start with empty Logs table otherwise primary key constraint
			String sql1 = "TRUNCATE TABLE LOGS";
			stmt1.executeUpdate(sql1);
			
		} catch (SQLException e) {
			System.out.println("Error in creating LOGS table in the database");

		}
		return con;
	}

}
