package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import eclipselogger.utils.PackageUtils;

public class AddPackageAction extends EclipseAction {
	private IFile previousFile;
	private IFolder addedPackage;
	private boolean samePackage;
	private boolean sameProject;
	
	public AddPackageAction(IFolder addedPackage, IFile previousFile) {
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
	
	
}
