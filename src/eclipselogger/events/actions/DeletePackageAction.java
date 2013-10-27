package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.utils.PackageUtils;

public class DeletePackageAction extends EclipseAction {
	private IFile previousFile;
	private IFolder deletedPackage;
	private boolean samePackage;
	private boolean sameProject;
	private int filesCount;
	
	// TODO implement number of files in package
	
	public DeletePackageAction(long timeSinceLastAction, EclipseAction previousAction, IFolder deletedPackage, IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.deletedPackage = deletedPackage;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(deletedPackage, previousFile);
		sameProject = PackageUtils.checkIfSameProject(deletedPackage, previousFile);
	}

	public IFile getPreviousFile() {
		return previousFile;
	}

	public IFolder getAddedPackage() {
		return deletedPackage;
	}

	public boolean isSamePackage() {
		return samePackage;
	}

	public boolean isSameProject() {
		return sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.DELETE_PACKAGE;
	}
}
