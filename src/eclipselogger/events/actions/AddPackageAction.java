package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.utils.PackageUtils;

public class AddPackageAction extends EclipseAction {
	
	public static final String TABLE_NAME = "add_package";
	
	private final String previousFile;
	private final String addedPackage;
	private final boolean samePackage;
	private final boolean sameProject;
	
	public AddPackageAction(final long timeSinceLastAction, final EclipseAction previousAction, final IFolder addedPackage, final IFile previousFile) {
		super(timeSinceLastAction, previousAction);
		this.addedPackage = addedPackage.getProjectRelativePath().toOSString();
		this.previousFile = previousFile.getProjectRelativePath().toOSString();
		this.samePackage = PackageUtils.checkIfSamePackage(addedPackage, previousFile);
		this.sameProject = PackageUtils.checkIfSameProject(addedPackage, previousFile);
	}

	public String getPreviousFile() {
		return this.previousFile;
	}

	public String getAddedPackage() {
		return this.addedPackage;
	}

	public boolean isSamePackage() {
		return this.samePackage;
	}

	public boolean isSameProject() {
		return this.sameProject;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.ADD_PACKAGE;
	}
	
	
}
