package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

public class RefactorFileAction extends EclipseAction {
	
	public static final String FILE_RENAME = "FILE_RENAME";
	public static final String FILE_MOVE = "FILE_MOVE";
	
	private String oldFilePath;
	private String newFilePath;
	private String refactorType;
	
	// TODO implement type of refactoring
	
	public RefactorFileAction(long timeSinceLastAction, EclipseAction previousAction, IFile oldFile, IFile newFile) {
		super(timeSinceLastAction, previousAction);
		this.oldFilePath = oldFile.getProjectRelativePath().toOSString();
		this.newFilePath = newFile.getProjectRelativePath().toOSString();
		this.refactorType = resolveRefactorType(oldFile, newFile);
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
	
	private static String resolveRefactorType(IFile oldFile, IFile newFile) {
		if (oldFile == null || newFile == null) {
			return null;
		}
		if (oldFile.getName().equals(newFile.getName())) {
			return FILE_MOVE;
		} else {
			return FILE_RENAME;
		}
	}

	@Override
	public ActionType getActionType() {
		return ActionType.REFATOR_FILE;
	}
	
	
}
