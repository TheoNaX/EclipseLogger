package eclipselogger.events.actions;

import org.eclipse.core.resources.IFolder;

public class RefactorPackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "refactor_package";
	
	public static final String PACKAGE_RENAME = "PACKAGE_RENAME";
	public static final String PACKAGE_MOVE = "PACKAGE_MOVE";
	
	private String oldPackagePath;
	private String newPackagePath;
	private String refactorType;
	
	public RefactorPackageAction(long timeSinceLastAction, EclipseAction previousAction, IFolder oldPackage, IFolder newPackage) {
		super(timeSinceLastAction, previousAction);
		this.oldPackagePath = oldPackage.getProjectRelativePath().toOSString();
		this.newPackagePath = newPackage.getProjectRelativePath().toOSString();
		refactorType = resolveRefactorType(oldPackage, newPackage);
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
	
	private static String resolveRefactorType(IFolder oldFolder, IFolder newFolder) {
		if (oldFolder == null || newFolder == null) {
			return null;
		}
		if (oldFolder.getName().equals(newFolder.getName())) {
			return PACKAGE_MOVE;
		} else {
			return PACKAGE_RENAME;
		}
	}

	@Override
	public ActionType getActionType() {
		return ActionType.REFACTOR_PACKAGE;
	}
	

}
