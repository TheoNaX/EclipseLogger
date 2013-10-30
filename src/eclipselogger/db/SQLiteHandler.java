package eclipselogger.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import eclipselogger.events.actions.EclipseAction;


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
	
	private void checkConnection() throws Exception {
		try {
			if (conn == null || conn.isClosed()) {
				initConnection();
			}
		} catch (SQLException e) {
			throw new Exception("Failed to initiate database connection!!!");
		}
	}
	
	public void executeSQLInsert(String sqlInsert) throws Exception {
		checkConnection();
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(sqlInsert);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to execute SQL: " + sqlInsert);
		}
	}
	
	public PreparedStatement prepareStatement(String sql) throws Exception {
		checkConnection();
		PreparedStatement pst = conn.prepareStatement(sql);
		
		return pst;
	}
	
	public int getNextSequenceNumberForAction() throws Exception {
		int seqNo = 0;
		checkConnection();
		String sql = "SELECT seq FROM SQLITE_SEQUENCE WHERE name='" + EclipseAction.TABLE_NAME + "';";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (rs.next()) {
			seqNo = rs.getInt(1) + 1;
			return seqNo;
		} else {
			throw new Exception("Failed to get sequence number");
		}
		
		
	}
	
	
	
	
}
