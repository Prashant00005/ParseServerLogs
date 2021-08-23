package com.cs.ServerLogsProject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CreateServerLogs {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		JSONParser parser = new JSONParser();
		try {
			// Read log file path from command line
			JSONArray serverLogs = (JSONArray) parser.parse(new FileReader(args[0]));

			List<JSONObject> jsonValues = new ArrayList<JSONObject>();

			// Add each JSON object(entry in log file) to arrayList for sorting
			for (Object obj : serverLogs) {
				JSONObject log = (JSONObject) obj;
				jsonValues.add(log);

			}

			Collections.sort(jsonValues, new Comparator<JSONObject>() {

				// Sort JSON objects by id
				private static final String SORT_COLUMN = "id";

				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {
					String id1 = new String();
					String id2 = new String();

					try {
						id1 = (String) obj1.get(SORT_COLUMN);
						id2 = (String) obj2.get(SORT_COLUMN);
					} catch (Exception e) {
						System.out.println("Error in comparing JSON objects");
					}

					// Sorting based on id column in order to find the duration
					return id1.compareToIgnoreCase(id2);
				}
			});

			// Get database connection
			Connection con = DBConnection.setupDB();

			// Calculate duration between two timestamps with same id
			for (int i = 0; i < jsonValues.size(); i += 2) {
				Long timestamp1 = Long.parseLong((String) (jsonValues.get(i).get("timestamp")));
				Long timestamp2 = Long.parseLong((String) (jsonValues.get(i + 1).get("timestamp")));

				// Find absolute value of duration to ignore negative value
				Long duration = Math.abs(timestamp1 - timestamp2);

				if (duration > 4) {

					// Write the event to the database
					writeToDB(con, (String) jsonValues.get(i).get("id"), duration,
							(String) jsonValues.get(i).get("type"), (String) jsonValues.get(i).get("host"), "true");
				}

			}

			//Print flagged events to the user
			showLogs(con);

		} catch (FileNotFoundException e) {

			System.out.println("Please provide valid log file path");

		} catch (IOException e) {

			System.out.println("Error while reading log file");

		} catch (org.json.simple.parser.ParseException e) {

			System.out.println("Invalid JSON format of the log file");
		} catch (SQLException e) {

			System.out.println("Exception while writing to the database");
		}

	}

	/**
	 * Insert flagged events to the database
	 * @param con
	 * @param id
	 * @param duration
	 * @param type
	 * @param host
	 * @param alert
	 * @throws SQLException
	 */
	public static void writeToDB(Connection con, String id, Long duration, String type, String host, String alert)
			throws SQLException {

		try {
			// Insert into database using preparedStatement to avoid SQL Injection
			PreparedStatement pstmt = con
					.prepareStatement("INSERT INTO LOGS (id,duration,type,host,alert) VALUES (?, ?, ?, ?, ?)");

			//Setting the values to be inserted to the database
			pstmt.setString(1, id);
			pstmt.setLong(2, duration);
			pstmt.setString(3, type);
			pstmt.setString(4, host);
			pstmt.setString(5, alert);
			
			//Run the SQL query
			pstmt.executeUpdate();

		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Cannot insert flagged events twice");
		}
		

	}

	/**
	 * Display flagged events to the user
	 * @param con
	 * @throws SQLException
	 */
	public static void showLogs(Connection con) throws SQLException {
		try {
			String query = "select * from LOGS";
			Statement stmt2 = con.createStatement();
			ResultSet rs = stmt2.executeQuery(query);
			
			System.out.println("Id" + "\t\t" + "Duration" + "\t" + "Type" + "\t\t\t" + "Host" + "\t" + "Alert" + "\t");
			
			while (rs.next()) {
				String showId = rs.getString("id");
				Long showDuration = rs.getLong("duration");
				String showType = rs.getString("type");
				String showHost = rs.getString("host");
				String showAlert = rs.getString("alert");
				System.out.println(
						"\n" + showId + "\t" + showDuration + "\t\t" + showType + "\t\t" + showHost + "\t" + showAlert);
			}

		} catch (SQLException e) {
			System.out.println("Error while displaying flagged events");
		} finally {
			con.close();
		}
	}

}
