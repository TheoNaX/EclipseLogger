package eclipselogger.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

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
	
	
	
	
}
