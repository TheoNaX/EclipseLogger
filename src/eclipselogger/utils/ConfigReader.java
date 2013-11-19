package eclipselogger.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ConfigReader {
	
	public static final String DB_NAME = "DB_NAME";
	
	
	public static final String FILE_UPLOADER_TYPE = "FILE_UPLOADER_TYPE";
	public static final String FILE_SEND_INTERVAL = "FILE_SEND_INTERVAL";
	public static final String LOGGER_TYPES = "LOGGER_TYPES";
	
	// SFTP specific
	public static final String SFTP_SERVER_IP = "SFTP_SERVER_IP";
	public static final String SFTP_SERVER_PORT = "SFTP_SERVER_PORT";
	public static final String SFTP_USER_NAME = "SFTP_USER_NAME";
	public static final String SFTP_PASSWORD = "SFTP_PASSWORD";
	public static final String SFTP_TARGET_DIRECTORY = "SFTP_TARGET_DIRECTORY";
	
	// REST specific 
	// TODO
	
	// local store specific
	public static final String LOCAL_FILE_DIRECTORY = "LOCAL_FILE_DIRECTORY";
	
	// action properties
	public static final String RECENT_SAME_TYPE_INTERVAL = "RECENT_SAME_TYPE_INTERVAL";
	public static final String RECENT_ACTIONS_COUNT = "RECENT_ACTIONS_COUNT";
	
	private static Properties properties;
	
	static {
		readConfig();
	}
	
	private static void readConfig() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
		} catch (final Exception ignore) {
			ignore.printStackTrace();
		} // we ignore this exception as default values are provided if load fails
	}
	
	public static String getSftpFileTargetDirectory() {
		return properties.getProperty(SFTP_TARGET_DIRECTORY, "/var/tmp");
	}
	
	public static String getLocalFileTargetDirectory() {
		return properties.getProperty(LOCAL_FILE_DIRECTORY, "c:\\eclipselog\\");
	}
	
	public static String getFileUploaderType() {
		return properties.getProperty(FILE_UPLOADER_TYPE, "SFTP");
	}
	
	public static int getFilesSendIntervalSeconds() {
		final String value = properties.getProperty(FILE_SEND_INTERVAL, "300");
		return Integer.parseInt(value);
	}
	
	public static List<String> getLoggers() {
		final String value = properties.getProperty(LOGGER_TYPES, "log4j");
		final String[] tmp = value.split(",");
		final List<String> loggers = new ArrayList<String>();
		for (int i=0; i<tmp.length; i++) {
			loggers.add(tmp[i]);
		}
		
		return loggers;
	}
	
	public static String getSftpServerIPAddress() {
		return properties.getProperty(SFTP_SERVER_IP);
	}
	
	public static int getSftpServerPort() {
		final String portValue = properties.getProperty(SFTP_SERVER_PORT);
		return (portValue != null) ? Integer.parseInt(portValue) : 22;
	}
	
	public static String getSftpTargetDir() {
		return properties.getProperty(SFTP_TARGET_DIRECTORY, "");
	}

	public static String getSftpUserName() {
		return properties.getProperty(SFTP_USER_NAME);
	}

	public static String getSftpPassword() {
		return properties.getProperty(SFTP_PASSWORD);
	}
	
	public static int getRecentSameTypeActionsCountInterval() {
		final String strValue = properties.getProperty(RECENT_SAME_TYPE_INTERVAL, "300");
		return Integer.parseInt(strValue);
	}
	
	public static int getCountOfLastActions() {
		final String strValue = properties.getProperty(RECENT_ACTIONS_COUNT, "20");
		return Integer.parseInt(strValue);
	}
	
	public static String getDbName() {
		return properties.getProperty(DB_NAME, "eclipselog.db");
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
}
