package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.utils.PackageUtils;

public class AddPackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_package";
	
	private IFile previousFile;
	private IFolder addedPackage;
	private boolean samePackage;
	private boolean sameProject;
	
	public AddPackageAction(long timeSinceLastAction, EclipseAction previousAction, IFolder addedPackage, IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.addedPackage = addedPackage;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(addedPackage, previousFile);
		sameProject = PackageUtils.checkIfSameProject(addedPackage, previousFile);
	}

	public IFile getPreviousFile() {
		return previousFile;
	}

	public IFolder getAddedPackage() {
		return addedPackage;
	}

	public boolean isSamePackage() {
		return samePackage;
	}

	public boolean isSameProject() {
		return sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_PACKAGE;
	}
	
	
}
