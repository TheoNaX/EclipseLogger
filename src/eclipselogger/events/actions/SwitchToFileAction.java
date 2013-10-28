package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.FileValidator;
import eclipselogger.utils.PackageUtils;

public class SwitchToFileAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_file";
	
	private IFile switchedToFile;
	private IFile previousFile;
	private boolean samePackage;
	private boolean sameProject;
	private boolean sameFileType;
	
	
	public SwitchToFileAction(long timeSinceLastAction, EclipseAction previousAction, IFile openedFile, IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.switchedToFile = openedFile;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(openedFile, previousFile);
		sameProject = PackageUtils.checkIfSameProject(openedFile, previousFile);
		sameFileType = FileValidator.haveFilesTheSameExtension(openedFile, previousFile);
	}
	
	public boolean openedInSamePackage() {
		return samePackage;
	}
	
	public int getPackageDistance() {
		// TODO implement package distances
		return 0;
	}
	
	public IFile getSwitchedToFile() {
		return this.switchedToFile;
	}
	
	public IFile getPreviousOpenedFile() {
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
		return ActionType.SWITCH_FILE;
	}
	
}
