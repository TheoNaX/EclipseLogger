package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.events.WorkingFile;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;


public class DeleteFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "delete_file";
	
	private final String deletedFile;
	private final String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	private FileChanges changes;
	private long workingTime;
	
	// TODO implement count of deleted files within some period ???
	
	public DeleteFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile deletedFile, final IFile previoiusFile, final WorkingFile workFile) {
		super(timeSinceLastAction, previousAction);
		this.deletedFile = deletedFile.getProjectRelativePath().toOSString();
		this.previousFile = previoiusFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(deletedFile, previoiusFile);
		this.sameProject = PackageUtils.checkIfSameProject(deletedFile, previoiusFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(deletedFile, previoiusFile);
		if (workFile != null) {
			this.changes = workFile.getFileChanges();
			this.workingTime = workFile.getWorkingTime();
		}
	}

	public String getDeletedFile() {
		return this.deletedFile;
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	public boolean isSameFileType() {
		return this.sameFileType;
	}
	
	public FileChanges getFileChanges() {
		return this.changes;
	}
	
	public long getFileWorkingTime() {
		return this.workingTime;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.DELETE_FILE;
	}
	
	
}
