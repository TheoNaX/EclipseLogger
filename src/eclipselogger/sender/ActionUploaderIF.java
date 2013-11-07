package eclipselogger.sender;

public interface ActionUploaderIF {
	public static final String SFTP_SENDER = "SFTP";
	public static final String LOCAL_SENDER = "LOCAL";
	public static final String REST_SENDER = "REST";
	
	void uploadEclipseActionToServer(String xmlFile) throws UploadActionException;
}
