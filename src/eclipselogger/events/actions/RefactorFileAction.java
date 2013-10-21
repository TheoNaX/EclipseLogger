package eclipselogger.events.actions;

public class RefactorFileAction extends EclipseAction {
	
	public static final String FILE_RENAME = "FILE_RENAME";
	public static final String FILE_MOVE = "FILE_MOVE";
	
	private String oldFilePath;
	private String newFilePath;
	private String refactorType;
	
	public RefactorFileAction(String oldFilePath, String newFilePath) {
		this.oldFilePath = oldFilePath;
		this.newFilePath = newFilePath;
	}

	public String getOldFilePath() {
		return oldFilePath;
	}

	public String getNewFilePath() {
		return newFilePath;
	}

	public String getRefactorType() {
		return refactorType;
	}
	
	
	
	
}
