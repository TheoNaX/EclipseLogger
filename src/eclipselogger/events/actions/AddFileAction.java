package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class AddFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_file";
	
	private IFile previousFile;
	private IFile addedFile;
	private boolean samePackage;
	private boolean sameProject;
	private boolean sameFileType;
	
	
	public AddFileAction(long timeSinceLastAction, EclipseAction previousAction, IFile addedFile, IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.addedFile = addedFile;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(addedFile, previousFile);
		sameProject = PackageUtils.checkIfSameProject(addedFile, previousFile);
		sameFileType = FileValidator.haveFilesTheSameExtension(addedFile, previousFile);
	}

	public IFile getPreviousFile() {
		return previousFile;
	}

	public IFile getAddedFile() {
		return addedFile;
	}

	public boolean isSamePackage() {
		return samePackage;
	}

	public boolean isSameProject() {
		return sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_FILE;
	}
	
	public boolean isSameFileType() {
		return this.sameFileType;
	}
}
