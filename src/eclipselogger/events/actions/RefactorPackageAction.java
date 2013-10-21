package eclipselogger.events.actions;

public class RefactorPackageAction extends EclipseAction {
	
	public static final String PACKAGE_RENAME = "PACKAGE_RENAME";
	public static final String PACKAGE_MOVE = "PACKAGE_MOVE";
	
	private String oldPackagePath;
	private String newPackagePath;
	private String refactorType;
	
	public RefactorPackageAction(String oldPackagePath, String newPackagePath) {
		this.oldPackagePath = oldPackagePath;
		this.newPackagePath = newPackagePath;
	}

	public String getOldFilePath() {
		return oldPackagePath;
	}

	public String getNewFilePath() {
		return newPackagePath;
	}

	public String getRefactorType() {
		return refactorType;
	}
	

}
