package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.events.WorkingFile;
import eclipselogger.utils.FileChanges;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class CloseFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "close_file";
	
	private final String closedFile;
	private final String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	private FileChanges fileChanges;
	private long workingTime;
	
	public CloseFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile closedFile, final IFile previousFile, final WorkingFile workFile) {
		super(timeSinceLastAction, previousAction);
		this.closedFile = closedFile.getProjectRelativePath().toOSString();
		this.previousFile = previousFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(closedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(closedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(closedFile, previousFile);
		if (workFile != null) {
			this.fileChanges = workFile.getFileChanges();
			this.workingTime = workFile.getWorkingTime();
		}
	}
	
	public boolean closedInSamePackage() {
		return this.samePackage;
	}
	
	public int getPackageDistance() {
		// TODO implement package distances
		return 0;
	}
	
	public String getClosedFile() {
		return this.closedFile;
	}
	
	public String getPreviousFile() {
		return this.previousFile;
	}
	
	public boolean closedInSameProject() {
		return this.sameProject;
	}
	
	public boolean isTheSameTypeAsPreviuos() {
		return this.sameFileType;
	}
	
	public long getFileWorkingTime() {
		return this.workingTime;
	}
	
	public FileChanges getFileChanges() {
		return this.fileChanges;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.CLOSE_FILE;
	}
}
