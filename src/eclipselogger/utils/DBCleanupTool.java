package eclipselogger.utils;

import java.sql.ResultSet;
import java.sql.Statement;

public class DBCleanupTool {

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ignore) {}
	}
	
	public static void closeStatement(Statement s) {
		try {
			if (s != null) {
				s.close();
			}
		} catch (Exception ignore) {}
	}
	
}
