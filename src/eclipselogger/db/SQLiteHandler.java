package eclipselogger.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import eclipselogger.events.actions.EclipseAction;


public class SQLiteHandler {
	private final String sDriverName = "org.sqlite.JDBC";
	
	private final String dbName = "eclipselog.db";
	private final String sJdbc = "jdbc:sqlite";
	private final String dbUrl = this.sJdbc + ":" + this.dbName;
	
	private Connection conn;
	
	public void initConnection() throws Exception {
		Class.forName(this.sDriverName);
		this.conn = DriverManager.getConnection(this.dbUrl);
		this.conn.setAutoCommit(true);
		System.out.println(">>>>>>>>>>>>> Connection to database established !!!");
	}
	
	private void checkConnection() throws Exception {
		try {
			if (this.conn == null || this.conn.isClosed()) {
				initConnection();
			}
		} catch (final SQLException e) {
			throw new Exception("Failed to initiate database connection!!!");
		}
	}
	
	public Connection getConnection() throws Exception {
		checkConnection();
		return this.conn;
	}
	
	public void executeSQLInsert(final String sqlInsert) throws Exception {
		checkConnection();
		Statement statement;
		try {
			statement = this.conn.createStatement();
			statement.executeUpdate(sqlInsert);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to execute SQL: " + sqlInsert);
		}
	}
	
	public PreparedStatement prepareStatement(final String sql) throws Exception {
		checkConnection();
		final PreparedStatement pst = this.conn.prepareStatement(sql);
		
		return pst;
	}
	
	public int getNextSequenceNumberForAction() throws Exception {
		int seqNo = 0;
		checkConnection();
		final String sql = "SELECT seq FROM SQLITE_SEQUENCE WHERE name='" + EclipseAction.TABLE_NAME + "';";
		final ResultSet rs = this.conn.createStatement().executeQuery(sql);
		if (rs.next()) {
			seqNo = rs.getInt(1) + 1;
			return seqNo;
		} else {
			System.out.println(">>>>>>>> Resultset empty, returning 1 !!!");
			return 1;
		}
		
	}
	
	public void disposeConnection() {
		try {
			if (this.conn != null) {
				this.conn.close();
			}
		} catch (final Exception e) {
			// ignore
		}
	}
	
	
}
