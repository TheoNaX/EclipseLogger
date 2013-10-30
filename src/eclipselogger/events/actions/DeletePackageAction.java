package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.utils.PackageUtils;

public class DeletePackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "delete_package";
	
	private final String previousFile;
	private final String deletedPackage;
	private final boolean samePackage;
	private final boolean sameProject;	
	
	public DeletePackageAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFolder deletedPackage, final IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.deletedPackage = deletedPackage.getProjectRelativePath().toOSString();
		this.previousFile = previousFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(deletedPackage, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(deletedPackage, previousFile);
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public String getDeletedPackage() {
		return this.deletedPackage;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.DELETE_PACKAGE;
	}
}
