package eclipselogger.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ConfigReader {
	
	public static final String SFTP_FILE_DIRECTORY = "SFTP_FILE_DIRECTORY";
	public static final String LOCAL_FILE_DIRECTORY = "LOCAL_FILE_DIRECTORY";
	public static final String FILE_UPLOADER_TYPE = "FILE_UPLOADER_TYPE";
	public static final String FILE_SEND_INTERVAL = "FILE_SEND_INTERVAL";
	public static final String LOGGER_TYPES = "LOGGER_TYPES";
	
	private static Properties properties;
	
	static {
		readConfig();
	}
	
	private static void readConfig() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("config.properties"));
		} catch (final Exception ignore) {} // we ignore this exception as default values are provided if load fails
	}
	
	public static String getSftpFileTargetDirectory() {
		return properties.getProperty(SFTP_FILE_DIRECTORY, "/var/tmp");
	}
	
	public static String getLocalFileTargetDirectory() {
		return properties.getProperty(LOCAL_FILE_DIRECTORY, "actionfiles/");
	}
	
	public static String getFileUploaderType() {
		return properties.getProperty(FILE_UPLOADER_TYPE, "SFTPActionUploader");
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
	
}
