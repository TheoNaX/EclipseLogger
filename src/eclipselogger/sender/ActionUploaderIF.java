package eclipselogger.sender;

public interface ActionUploaderIF {
	void uploadEclipseActionToServer(String xmlFile) throws UploadActionException;
}
