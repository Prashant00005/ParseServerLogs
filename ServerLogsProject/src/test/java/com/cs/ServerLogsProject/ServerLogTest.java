package com.cs.ServerLogsProject;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class ServerLogTest {

	@Test
	public void insertFlaggedEventTest() throws SQLException {
		Connection con = DBConnection.setupDB();
		try {
			CreateServerLogs.writeToDB(con, "testID", 7l, null, null, "false");
			assertTrue(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	@Test
	public void displayFlaggedEventTest() throws SQLException {
		Connection con = DBConnection.setupDB();
		try {
			CreateServerLogs.writeToDB(con, "testID1", 3l, "APPLICATION_LOG", "12345", "false");

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM LOGS WHERE id='testID1'");
			assertTrue(rs.next());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			con.close();
		}
	}
}
