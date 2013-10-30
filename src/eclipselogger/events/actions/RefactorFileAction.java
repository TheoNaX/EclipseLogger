package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

public class RefactorFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "refactor_file";
	
	public static final String FILE_RENAME = "FILE_RENAME";
	public static final String FILE_MOVE = "FILE_MOVE";
	
	private final String oldFilePath;
	private final String newFilePath;
	private final String refactorType;
	private boolean samePackage;
	private boolean sameProject;
	private String previousFile;
	private String refactoredFile;
	
	// TODO same package + same project 
	
	
	public RefactorFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile oldFile, final IFile newFile) {
		super(timeSinceLastAction, previousAction);
		this.oldFilePath = oldFile.getProjectRelativePath().toOSString();
		this.newFilePath = newFile.getProjectRelativePath().toOSString();
		this.refactorType = resolveRefactorType(oldFile, newFile);
	}

	public String getOldFilePath() {
		return this.oldFilePath;
	}

	public String getNewFilePath() {
		return this.newFilePath;
	}

	public String getRefactorType() {
		return this.refactorType;
	}
	
	private static String resolveRefactorType(final IFile oldFile, final IFile newFile) {
		if (oldFile == null || newFile == null) {
			return null;
		}
		if (oldFile.getName().equals(newFile.getName())) {
			return FILE_MOVE;
		} else {
			return FILE_RENAME;
		}
	}
	
	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	public String getPreviousFile() {
		return this.previousFile;
	}
	
	public String getRefactoredFile() {
		return this.refactoredFile;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.REFACTOR_FILE;
	}
	
	
}
