package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.events.WorkingFile;
import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class CloseFileAction extends EclipseAction {
	private IFile closedFile;
	private IFile previousFile;
	private boolean samePackage;
	private boolean sameProject;
	private boolean sameFileType;
	private WorkingFile workFile;
	
	public CloseFileAction(long timeSinceLastAction, EclipseAction previousAction, IFile closedFile, IFile previousFile, WorkingFile workFile) {
		super(timeSinceLastAction, previousAction);
		this.closedFile = closedFile;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(closedFile, previousFile);
		sameProject = PackageUtils.checkIfSameProject(closedFile, previousFile);
		sameFileType = FileValidator.haveFilesTheSameExtension(closedFile, previousFile);
		this.workFile = workFile;
	}
	
	public boolean closedInSamePackage() {
		return samePackage;
	}
	
	public int getPackageDistance() {
		// TODO implement package distances
		return 0;
	}
	
	public IFile getClosedFile() {
		return this.closedFile;
	}
	
	public IFile getPreviousWorkingFile() {
		return this.previousFile;
	}
	
	public boolean closedInSameProject() {
		return this.sameProject;
	}
	
	public boolean isTheSameTypeAsPreviuos() {
		return this.sameFileType;
	}
	
	public WorkingFile getWorkingFile() {
		return this.workFile;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.CLOSE_FILE;
	}
}
