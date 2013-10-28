package eclipselogger.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLiteHandler {
	private String sDriverName = "org.sqlite.JDBC";
	
	private String dbName = "eclipselog.db";
	private String sJdbc = "jdbc:sqlite";
	private String dbUrl = sJdbc + ":" + dbName;
	
	private Connection conn;
	
	public void initConnection() throws Exception {
		Class.forName(sDriverName);
		conn = DriverManager.getConnection(dbUrl);
	}
	
	public void executeSQLInsert(String sqlInsert) throws Exception {
		try {
			if (conn == null || conn.isClosed()) {
				initConnection();
			}
		} catch (SQLException e) {
			throw new Exception("Failed to initiate database connection!!!");
		}
		
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(sqlInsert);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to execute SQL: " + sqlInsert);
		}
	}
	
	
	
	
}
