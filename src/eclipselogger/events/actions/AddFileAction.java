package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class AddFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_file";
	
	private final String previousFile;
	private final String addedFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	
	
	public AddFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile addedFile, final IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.addedFile = addedFile.getProjectRelativePath().toOSString();
		this.previousFile = previousFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(addedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(addedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(addedFile, previousFile);
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public String getAddedFile() {
		return this.addedFile;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_FILE;
	}
	
	public boolean isSameFileType() {
		return this.sameFileType;
	}
}
