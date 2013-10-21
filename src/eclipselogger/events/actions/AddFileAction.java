package eclipselogger.events.actions;

import org.eclipse.core.resources.IFile;

import eclipselogger.utils.PackageUtils;

public class AddFileAction extends EclipseAction {
	private IFile previousFile;
	private IFile addedFile;
	private boolean samePackage;
	private boolean sameProject;
	
	public AddFileAction(IFile addedFile, IFile previousFile) {
		this.addedFile = addedFile;
		this.previousFile = previousFile;
		samePackage = PackageUtils.checkIfSamePackage(addedFile, previousFile);
		sameProject = PackageUtils.checkIfSameProject(addedFile, previousFile);
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
}
