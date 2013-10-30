package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class OpenNewFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "open_file";
	
	private final String openedFile;
	private final String previousFile;
	private final boolean samePackage;
	private final boolean sameProject;
	private final boolean sameFileType;
	
	public OpenNewFileAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFile openedFile, final IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.openedFile = openedFile.getProjectRelativePath().toOSString();
		this.previousFile = previousFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(openedFile, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(openedFile, previousFile);
		this.sameFileType = FileValidator.haveFilesTheSameExtension(openedFile, previousFile);
	}
	
	public boolean openedInSamePackage() {
		return this.samePackage;
	}
	
	public int getPackageDistance() {
		// TODO implement package distances
		return 0;
	}
	
	public String getOpenedFile() {
		return this.openedFile;
	}
	
	public String getPreviousOpenedFile() {
		return this.previousFile;
	}
	
	public boolean openedInSameProject() {
		return this.sameProject;
	}
	
	public boolean isTheSameTypeAsPreviuos() {
		return this.sameFileType;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_FILE;
	}
	
	
	
}
